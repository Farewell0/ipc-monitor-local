package com.starnet.ipcmonitorlocal.mq;

import com.starnet.ipcmonitorlocal.exception.MqException;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * MqHandlerFactory
 *
 * @author wzz
 * @date 2020/8/21 16:16
 **/
@Component
public class MqHandlerFactory {

    private Map<Integer, MqHandler> handlerMap = new HashMap<>();

    public void registerHandler(MqHandler handler) {
        if (!handlerMap.containsKey(handler.getCommand().getType())) {
            handlerMap.put(handler.getCommand().getType(), handler);
        } else {
            throw new MqException("Handler existed");
        }
    }

    public MqHandler findHandler(int commandType) {
        return handlerMap.get(commandType);
    }

}
