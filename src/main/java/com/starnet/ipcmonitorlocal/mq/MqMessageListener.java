package com.starnet.ipcmonitorlocal.mq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.starnet.ipcmonitorlocal.config.ActiveMqProperties;
import com.starnet.ipcmonitorlocal.exception.MqException;
import com.starnet.ipcmonitorlocal.mq.req.MqRequest;
import com.starnet.ipcmonitorlocal.mq.resp.MqResponse;
import com.starnet.ipcmonitorlocal.mq.resp.MqStatus;
import com.starnet.ipcmonitorlocal.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.jms.*;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * MqMessageListener
 *
 * @author wzz
 * @date 2020/8/21 16:21
 **/
@Slf4j
@Component
public class MqMessageListener implements MessageListener {

    @Autowired
    private ActiveMqProperties activeMqProperties;

    @Autowired
    private MqHandlerFactory mqHandlerFactory;

    private Session session;

    private long receiveTime;

    private ExecutorService executorService;

    @PostConstruct
    public void init() {
        int threadNum = 16;
        executorService = new ThreadPoolExecutor(threadNum, threadNum,
                0L, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(threadNum), new ThreadPoolExecutor.AbortPolicy());
    }

    @Override
    public void onMessage(Message message) {
        this.receiveTime = System.currentTimeMillis()  / 1000;
        executorService.execute(() -> {
            TextMessage textMessage = (TextMessage) message;
            MqResponse response = process(textMessage);
            feedback(textMessage, response);
        });

    }

    private MqResponse process(TextMessage textMessage) {
        MqResponse response = null;
        try {
            String message = textMessage.getText();
            MqRequest request = JsonUtils.fromJson(message, MqRequest.class);
            log.info("Receive cloud request: {}", request);
            if (null == request) {
                return new MqResponse(MqStatus.SERVER_NO_CMD);
            }
            MqHandler handler = mqHandlerFactory.findHandler(request.getCommand());
            if (null == handler) {
                return new MqResponse(MqStatus.SERVER_NO_CMD);
            }
            response = handler.process(request, message);
        } catch (MqException e) {
            log.error(e.getMessage());
            response = new MqResponse(e.getMqStatus());
        } catch (JMSException | IOException e) {
            log.error(e.getMessage());
            response = new MqResponse(MqStatus.ERROR_UNKNOWN, e.getMessage());
        }
        return response;
    }

    private void feedback(TextMessage textMessage, MqResponse response) {
        try {
            log.info("mq request : {};\n mq response : {}", textMessage.getText(), JsonUtils.toJson(response));

            String correlationID = textMessage.getJMSCorrelationID();
            Destination destination = textMessage.getJMSReplyTo();

            MessageProducer producer = session.createProducer(null);
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
            producer.setTimeToLive(activeMqProperties.getMessageLiveTime() * 1000L);
            TextMessage respMsg = session.createTextMessage();
            respMsg.setJMSCorrelationID(correlationID);
            respMsg.setStringProperty("correlationID", correlationID);
            respMsg.setText(JsonUtils.toJson(response));
            producer.send(destination, respMsg);
            producer.close();
        } catch (JsonProcessingException | JMSException e) {
            log.error(e.getMessage());
        }
    }

    public long getReceiveTime() {
        return receiveTime;
    }

    public void setSession(Session session) {
        this.session = session;
    }
}
