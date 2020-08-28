package com.starnet.ipcmonitorlocal.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * ActiveMqProperties
 *
 * @author wzz
 * @date 2020/8/21 17:52
 **/
@Data
@Component
@ConfigurationProperties(prefix = "activemq")
public class ActiveMqProperties {
    private String username;
    private String password;
    private String brokerUrl;
    private int messageLiveTime;
    private int heartbeatTime;
    private int maxHeartbeatTime;

    private String commandQueueName;
}
