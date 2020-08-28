package com.starnet.ipcmonitorlocal.mq.handler;

import com.starnet.ipcmonitorlocal.database.model.IpcEntity;
import com.starnet.ipcmonitorlocal.mq.MqHandler;
import com.starnet.ipcmonitorlocal.mq.MqHandlerFactory;
import com.starnet.ipcmonitorlocal.mq.req.MqRequest;
import com.starnet.ipcmonitorlocal.mq.resp.MqResponse;
import com.starnet.ipcmonitorlocal.mq.resp.MqStatus;
import com.starnet.ipcmonitorlocal.service.IpcService;
import com.starnet.ipcmonitorlocal.utils.JsonUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

/**
 * MqIpcHandler
 *
 * @author wzz
 * @date 2020/8/28 10:01
 **/
@Slf4j
@Component
public class MqIpcHandler implements MqHandler {
    @Autowired
    private MqHandlerFactory mqHandlerFactory;
    @Autowired
    private IpcService ipcService;

    @Bean
    public MqIpcHandler registerMqIpcHandler() {
        mqHandlerFactory.registerHandler(this);
        return this;
    }


    @Override
    public Command getCommand() {
        return Command.GET_IPC_LIST;
    }

    @Override
    public MqResponse process(MqRequest request, String message) {
        MqRequest req;
        try {
            req = JsonUtils.fromJson(message, MqRequest.class);
        } catch (IOException e) {
            log.error(e.getMessage());
            return new GetIpcListResp(MqStatus.CLOUD_REQUEST_PARAMS_ERROR);
        }
        GetIpcListResp resp = new GetIpcListResp(MqStatus.OK);
        resp.setIpcList(ipcService.findAll());
        return resp;
    }

    @Data
    @EqualsAndHashCode(callSuper = false)
    public static class GetIpcListResp extends MqResponse {
        private List<IpcEntity> ipcList;

        public GetIpcListResp(MqStatus status) {
            super(status);
        }
    }
}
