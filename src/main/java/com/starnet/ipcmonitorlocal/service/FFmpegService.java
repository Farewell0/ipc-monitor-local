package com.starnet.ipcmonitorlocal.service;

import com.starnet.ipcmonitorlocal.task.PushStreamTask;
import org.springframework.stereotype.Service;

/**
 * FFmpegService
 *
 * @author wzz
 * @date 2020/8/20 13:54
 **/
public interface FFmpegService {

    /**
     * 使用ffmpeg推流
     */
    void push(String ipcAddr, String pushAddr);

    /**
     * 停止推流
     */
    void stopPush(String ipcAddr);

}
