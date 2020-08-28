package com.starnet.ipcmonitorlocal.mq.handler;

import com.starnet.ipcmonitorlocal.mq.req.MqRequest;
import com.starnet.ipcmonitorlocal.mq.resp.MqResponse;
import com.starnet.ipcmonitorlocal.mq.MqHandler;
import com.starnet.ipcmonitorlocal.mq.MqHandlerFactory;
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
 * MqStopMonitorHandler
 *
 * @author wzz
 * @date 2020/8/21 17:16
 **/
@Slf4j
@Component
public class MqStopMonitorHandler implements MqHandler{
    @Autowired
    private MqHandlerFactory handlerFactory;

    @Autowired
    private FFmpegService ffmpegService;

    @Autowired
    private TaskPool taskPool;

    @Bean
    public MqStopMonitorHandler registerMqStopMonitorHandler() {
        handlerFactory.registerHandler(this);
        return this;
    }

    @Override
    public Command getCommand() {
        return Command.STOP_MONITOR;
    }

    @Override
    public MqResponse process(MqRequest request, String message) {
        StopMonitorReq req = null;
        try {
            req = JsonUtils.fromJson(message, StopMonitorReq.class);
        } catch (IOException e) {
            log.error(e.getMessage());
            return new MqResponse(MqStatus.CLOUD_REQUEST_PARAMS_ERROR);
        }
        if (taskPool.existRunningTask(req.getIpcAddr())) {
            ffmpegService.stopPush(req.getIpcAddr());
            return new MqResponse(MqStatus.OK);
        } else {
            return new MqResponse(MqStatus.SERVER_NO_PUSH_STREAM_ERROR);
        }
    }

    @Data
    @EqualsAndHashCode(callSuper = false)
    public static class StopMonitorReq extends MqRequest {
        private String ipcAddr;
    }

}
