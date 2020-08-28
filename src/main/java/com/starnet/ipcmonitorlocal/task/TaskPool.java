package com.starnet.ipcmonitorlocal.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Enumeration;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * TaskPool
 *
 * @author wzz
 * @date 2020/8/21 9:20
 **/
@Slf4j
@Component
public class TaskPool {

    /**
     * 存放所有等待执行的推流任务
     */
    private Map<String, PushStreamTask> taskMap = new ConcurrentHashMap<>(16);
    /**
     * 存放正在执行中的推流任务
     */
    private Map<String, PushStreamTask> runningTaskMap = new ConcurrentHashMap<>(16);

    public void putTask(PushStreamTask task) {
        if (!taskMap.containsKey(task.getKey()) && !runningTaskMap.containsKey(task.getKey())) {
            taskMap.put(task.getKey(), task);
        }
    }

    public Set<String> getTaskKeys() {
        return taskMap.keySet();
    }

    public boolean existTask(String key) {
        return taskMap.containsKey(key);
    }

    public PushStreamTask takeTask(String key) {
        return taskMap.remove(key);
    }

    public PushStreamTask getTask(String key) {
        return taskMap.get(key);
    }

    public void putRunningTask(PushStreamTask task) {
        if (!runningTaskMap.containsKey(task.getKey())) {
            runningTaskMap.put(task.getKey(), task);
        }
    }

    public boolean existRunningTask(String key) {
        return runningTaskMap.containsKey(key);
    }

    public PushStreamTask takeRunningTask(String key) {
        return runningTaskMap.remove(key);
    }
}
