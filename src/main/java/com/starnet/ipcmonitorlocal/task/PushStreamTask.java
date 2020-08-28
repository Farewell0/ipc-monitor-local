package com.starnet.ipcmonitorlocal.task;

import lombok.Data;

/**
 * PushStreamTask
 *
 * @author wzz
 * @date 2020/8/19 19:00
 **/
@Data
public class PushStreamTask {
    /**
     * 拉流地址，一般是ipc的rtsp流
     */
    private String pullAddr;
    /**
     * 推流地址，推到nginx上
     */
    private String pushAddr;
    /**
     * ipc流是否可以拉取，因为只有ipc流可拉，才有后面的推流等步骤
     */
    private volatile boolean isPullPrepared;
    /**
     * 用来保存推流时的进程
     */
    private Process process;

    private State state;

    public enum State {
        /**
         * 初始状态
         */
        INIT,
        /**
         * 推流任务执行中
         */
        RUNNING,
        /**
         * 推流任务中止
         */
        STOP;
    }

    public String getKey() {
        return this.pullAddr;
    }
}
