package com.starnet.ipcmonitorlocal.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * TaskDispatch
 *
 * @author wzz
 * @date 2020/8/21 9:18
 **/
@Slf4j
@Component
public class TaskDispatch {

    @Autowired
    private TaskPool taskPool;
    @Autowired
    private TaskPoolExecutor taskPoolExecutor;

    @Scheduled(initialDelayString = "1000", fixedDelayString = "1000")
    public void dispatch() {
        Set<String> taskKeys = taskPool.getTaskKeys();
        for (String key : taskKeys) {
            if (taskPool.existRunningTask(key)) {
                continue;
            }
            PushStreamTask task = taskPool.takeTask(key);
            if (null != task) {
                taskPoolExecutor.executeTask(task);
            }
        }
    }
}
