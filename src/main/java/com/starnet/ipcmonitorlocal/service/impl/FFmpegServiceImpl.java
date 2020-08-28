package com.starnet.ipcmonitorlocal.service.impl;

import com.starnet.ipcmonitorlocal.component.FFmpegComponent;
import com.starnet.ipcmonitorlocal.config.FFmpegProperties;
import com.starnet.ipcmonitorlocal.exception.DeviceException;
import com.starnet.ipcmonitorlocal.service.FFmpegService;
import com.starnet.ipcmonitorlocal.task.PushStreamTask;
import com.starnet.ipcmonitorlocal.task.TaskPool;
import com.starnet.ipcmonitorlocal.task.TaskPoolExecutor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.jni.Proc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * FFmpegServiceImpl
 *
 * @author wzz
 * @date 2020/8/20 13:54
 **/
@Slf4j
@Service
public class FFmpegServiceImpl implements FFmpegService {
    @Autowired
    private FFmpegProperties ffmpegProperties;

    @Autowired
    private TaskPool taskPool;

    private AtomicInteger pushStreamId = new AtomicInteger();

    @Override
    public void push(String ipcAddr, String pushAddr) {
        PushStreamTask task = new PushStreamTask();
        task.setPullAddr(ipcAddr);
        task.setPushAddr(pushAddr);
//        task.setPushAddr(ffmpegProperties.getPushAddrPrefix() + getPushStreamId());
        task.setState(PushStreamTask.State.INIT);
        taskPool.putTask(task);
    }

    @Override
    public void stopPush(String ipcAddr) {
        if (taskPool.existRunningTask(ipcAddr)) {
            PushStreamTask task = taskPool.takeRunningTask(ipcAddr);
            task.getProcess().destroy();
        }
    }

    public int getPushStreamId() {
        int id = pushStreamId.incrementAndGet();
        if (id >= Byte.MAX_VALUE) {
            return 0;
        }
        return id;
    }

}
