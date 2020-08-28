package com.starnet.ipcmonitorlocal.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * CloudProperties
 *
 * @author wzz
 * @date 2020/8/25 14:13
 **/
@Data
@Component
@ConfigurationProperties(prefix = "cloud")
public class CloudProperties {
    private String getPushStreamTokenUrl;
    private String getLoginTokenUrl;
    private String username;
    private String password;
}
