package com.starnet.ipcmonitorlocal.mq.handler;

import com.starnet.ipcmonitorlocal.mq.resp.MqResponse;
import com.starnet.ipcmonitorlocal.mq.MqHandler;
import com.starnet.ipcmonitorlocal.mq.MqHandlerFactory;
import com.starnet.ipcmonitorlocal.mq.req.MqRequest;
import com.starnet.ipcmonitorlocal.mq.resp.MqStatus;
import com.starnet.ipcmonitorlocal.service.FFmpegService;
import com.starnet.ipcmonitorlocal.task.TaskPool;
import com.starnet.ipcmonitorlocal.utils.JsonUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * PushStreamHandler
 *
 * @author wzz
 * @date 2020/8/21 17:03
 **/
@Slf4j
@Component
public class MqMonitorHandler implements MqHandler {

    @Autowired
    private MqHandlerFactory handlerFactory;

    @Autowired
    private FFmpegService ffmpegService;

    @Autowired
    private TaskPool taskPool;

    @Bean
    public MqMonitorHandler registerMqMonitorHandler() {
        handlerFactory.registerHandler(this);
        return this;
    }

    @Override
    public Command getCommand() {
        return Command.START_MONITOR;
    }

    @Override
    public MqResponse process(MqRequest request, String message) {
        StartMonitorReq req = null;
        try {
            req = JsonUtils.fromJson(message, StartMonitorReq.class);
        } catch (IOException e) {
            log.error(e.getMessage());
            return new StartMonitorResp(MqStatus.CLOUD_REQUEST_PARAMS_ERROR);
        }
        if (taskPool.existRunningTask(req.getIpcAddr())) {
            return new StartMonitorResp(MqStatus.OK);
        }
        ffmpegService.push(req.getIpcAddr(), req.getPushAddr());
        // 延时5s，等待推流任务完成
        try {
            Thread.sleep(8000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (taskPool.existRunningTask(req.getIpcAddr())) {
            return new StartMonitorResp(MqStatus.OK);
        } else {
            return new StartMonitorResp(MqStatus.SERVER_PUSH_STREAM_ERROR);

        }
    }

    @Data
    @EqualsAndHashCode(callSuper = false)
    public static class StartMonitorReq extends MqRequest{
        private String ipcAddr;
        private String pushAddr;
    }

    @Data
    @EqualsAndHashCode(callSuper = false)
    public static class StartMonitorResp extends MqResponse {
        public StartMonitorResp(MqStatus status) {
            super(status);
        }
    }
}
