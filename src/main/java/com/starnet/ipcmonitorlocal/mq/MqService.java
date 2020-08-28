package com.starnet.ipcmonitorlocal.mq;

import com.starnet.ipcmonitorlocal.config.ActiveMqProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import javax.jms.*;

/**
 * MqService
 *
 * @author wzz
 * @date 2020/8/21 17:58
 **/
@Slf4j
@Service
public class MqService {

    @Autowired
    private ActiveMqProperties activeMqProperties;

    @Autowired
    private MqMessageListener mqMessageListener;

    private ConnectionFactory factory;
    private Connection connection;
    private Session session;
    private MessageConsumer messageConsumer;

    @Bean
    public MqService initMqService() {
        new Thread(this::execute).start();
        return this;
    }

    public void execute() {
        while (true) {
            try {
                long receiveTime = mqMessageListener.getReceiveTime();
                long now = System.currentTimeMillis() / 1000;
                if (now - receiveTime > (activeMqProperties.getMaxHeartbeatTime() * 1000)) {
                    close();
                    open();
                }
                Thread.sleep(activeMqProperties.getHeartbeatTime() * 1000L);
            } catch (InterruptedException | JMSException e) {
                log.error(e.getMessage());
                try {
                    Thread.sleep(activeMqProperties.getHeartbeatTime() * 1000L);
                } catch (InterruptedException ex) {
                    log.error(ex.getMessage());
                }
            }
        }

    }

    private void open() throws JMSException {
        factory = new ActiveMQConnectionFactory(activeMqProperties.getUsername(),
                activeMqProperties.getPassword(), activeMqProperties.getBrokerUrl());
        connection = factory.createConnection();
        connection.start();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        // 创建一个名为CommandQueue的消息队列
        Destination destination = session.createQueue(activeMqProperties.getCommandQueueName());
        // 创建消息消费者
        messageConsumer = session.createConsumer(destination);
        mqMessageListener.setSession(session);
        messageConsumer.setMessageListener(mqMessageListener);
    }

    private void close() throws JMSException {
        if (messageConsumer != null) {
            messageConsumer.close();
            messageConsumer = null;
        }
        if (session != null) {
            session.close();
            session = null;
        }
        if (connection != null) {
            connection.close();
            connection = null;
        }
    }
}
