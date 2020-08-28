package com.starnet.ipcmonitorlocal.task;

import com.starnet.ipcmonitorlocal.component.FFmpegComponent;
import com.starnet.ipcmonitorlocal.config.FFmpegProperties;
import com.starnet.ipcmonitorlocal.exception.CloudNginxException;
import com.starnet.ipcmonitorlocal.exception.DeviceException;
import com.starnet.ipcmonitorlocal.exception.LocalServerException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.*;

/**
 * TaskPoolExecutor
 *
 * @author wzz
 * @date 2020/8/19 19:07
 **/
@Slf4j
@Component
public class TaskPoolExecutor {
    @Autowired
    FFmpegProperties ffmpegProperties;
    @Autowired
    FFmpegComponent ffmpegComponent;
    @Autowired
    private TaskPool taskPool;

    private ExecutorService executorService;

    @PostConstruct
    public void init() {
        int threadNum = ffmpegProperties.getPushStreamNum();
        executorService = new ThreadPoolExecutor(threadNum, threadNum,
                0, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(threadNum),
                new ThreadPoolExecutor.DiscardPolicy());

    }

    public void executeTask(PushStreamTask task) {
        log.info("=> Starting pull[{}] to nignx[{}]", task.getPullAddr(), task.getPushAddr());
        executorService.execute(() -> {
            try {
                log.info("=> Starting pull ipc stream...");
                ffmpegComponent.executeFFprobeCmd(task, 3000);
            } catch (LocalServerException | DeviceException e) {
                // 从ipc拉流失败
                task.setPullPrepared(false);
                task.setState(PushStreamTask.State.INIT);
                log.error(e.getMessage());
                e.printStackTrace();
            }

            if (task.isPullPrepared()) {
                try {
                    log.info("=> Starting push ipc to nginx......");
                    // 如果正常执行的话，线程进入推流，会一直阻塞
                    ffmpegComponent.executeFFmpegCmd(task, 3000);
                } catch (LocalServerException | CloudNginxException e) {
                    // 拉流成功，但是推流到nginx失败
                    task.setState(PushStreamTask.State.STOP);
                    log.error(e.getMessage());
                }
            }
        });
    }

}
