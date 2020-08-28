package com.starnet.ipcmonitorlocal.component;

import com.starnet.ipcmonitorlocal.config.FFmpegProperties;
import com.starnet.ipcmonitorlocal.exception.CloudNginxException;
import com.starnet.ipcmonitorlocal.exception.DeviceException;
import com.starnet.ipcmonitorlocal.exception.LocalServerException;
import com.starnet.ipcmonitorlocal.task.PushStreamTask;
import com.starnet.ipcmonitorlocal.task.TaskPool;
import com.starnet.ipcmonitorlocal.utils.OSUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.*;

/**
 * FFmpegUtils
 * 调用ffmpeg进行转码等操作的工具类，需本地安装有ffmpeg
 * @author wzz
 * @date 2020/8/19 13:52
 **/
@Slf4j
@Component
public class FFmpegComponent {

    @Autowired
    private FFmpegProperties ffmpegProperties;

    @Autowired
    private HttpComponent httpComponent;

    @Autowired
    private TaskPool taskPool;

    private static final String PULL_IPC_SUCCESS_FLAG = "streams";
    private static final String PUSH_NGINX_SUCCESS_FLAG = "Press [q] to stop, [?] for help";

    public String getFFmpegPath() {
        if (OSUtils.isLinux()) {
            return "ffmpeg";
        }

        if (OSUtils.isWindows()) {
            return ffmpegProperties.getWindowsFfmpegPath();
        }

        throw new UnsupportedOperationException("unsupported operation system");
    }

    public String getFFprobePath() {
        if (OSUtils.isLinux()) {
            return "ffprobe";
        }

        if (OSUtils.isWindows()) {
            return ffmpegProperties.getWindowsFfProbePath();
        }

        throw new UnsupportedOperationException("unsupported operation system");
    }

    private String ffprobeCommand(String in) {
        return getFFprobePath() + " -v quiet -print_format json -show_error -show_format -show_streams " + in;
    }

    private String ffmpegCommand(String in, String out) {
        StringBuilder builder = new StringBuilder();
        builder.append(getFFmpegPath())
                .append(" -rtsp_transport tcp -loglevel info ")
                .append(" -i ")
                .append(in)
                .append(" -acodec copy -vcodec copy -f flv ")
                .append(out)
                .append("?token=")
                .append(httpComponent.getPushStreamToken());
        return builder.toString();
    }

    public boolean executeFFprobeCmd(PushStreamTask task, long timeout) {
        Runtime rt = Runtime.getRuntime();
        String command = ffprobeCommand(task.getPullAddr());
        Process p = null;
        try {
            p = rt.exec(command);
        }catch (IOException e) {
            log.error(e.getMessage());
            throw new LocalServerException("Execute ffprobe command fail");
        }
        try {
            String content = getOutputContent(p, timeout);
            log.info("=> output stream: {}", content);
            // 输出流包含stream，表示正常输出，拉流成功，拉流失败没有输出
            if (content.contains(PULL_IPC_SUCCESS_FLAG)) {
                log.info("=> Pull ipc success");
                task.setPullPrepared(true);
                p.waitFor(timeout, TimeUnit.MILLISECONDS);
                p.destroy();
                return true;
            }else {
                log.info("=> Destroy pull ipc process");
                p.destroy();
                throw new DeviceException("=> Pull ipc stream fail");
            }
        } catch (InterruptedException ex) {
            log.error(ex.getMessage());
            throw new DeviceException("=> Pull ipc stream fail");
        } finally {
            if (null != p) {
                p.destroy();
            }
        }
    }

    public void executeFFmpegCmd(PushStreamTask task, long timeout){
        Runtime rt = Runtime.getRuntime();
        String command = ffmpegCommand(task.getPullAddr(), task.getPushAddr());
        Process p = null;
        try {
            p = rt.exec(command);
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new LocalServerException("Execute ffmpeg command fail");
        }
        String content = null;
        try {
            content = getOutputContent(p, timeout);
            log.info("=> output stream: {}", content);
            if (content.contains(PUSH_NGINX_SUCCESS_FLAG)) {
                task.setProcess(p);
                task.setState(PushStreamTask.State.RUNNING);
                // 推流已经在执行中，移除等待队列中的任务
                taskPool.takeTask(task.getKey());
                taskPool.putRunningTask(task);
                p.waitFor();
            } else {
                p.destroy();
                throw new CloudNginxException("=> Push stream to nginx fail");
            }
        } catch (InterruptedException e) {
            p.destroy();
            throw new CloudNginxException("=> Push stream to nginx fail");
        }
    }

    private String getOutputContent(Process p, long timeout) throws InterruptedException {
        StringBuffer stringBuffer = new StringBuffer();
        //获取进程的标准输入流
        final InputStream input = p.getInputStream();
        //获取进程的错误流
        final InputStream error = p.getErrorStream();
        //启动一个线程负责读标准输出流
        getOutput(input, stringBuffer);
        getOutput(error, stringBuffer);
        Thread.sleep(timeout);
        return stringBuffer.toString();
    }

    private void getOutput(InputStream input, StringBuffer stringBuffer) {
        new Thread(() -> {
            BufferedReader br2 = new  BufferedReader(new InputStreamReader(input));
            try {
                String line = null ;
                while ((line = br2.readLine()) !=  null ) {
                    if (line != null){
                        stringBuffer.append(line + "\n");
//                        log.info("=> Output stream : {}", line);
                    }
                }
            } catch (IOException e) {
                log.error(e.getMessage());
            } finally {
                try {
                    input.close();
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }
        }).start();
    }


}
