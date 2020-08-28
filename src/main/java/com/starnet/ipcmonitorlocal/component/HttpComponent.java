package com.starnet.ipcmonitorlocal.component;

import com.starnet.ipcmonitorlocal.config.CloudProperties;
import com.starnet.ipcmonitorlocal.config.FFmpegProperties;
import com.starnet.ipcmonitorlocal.utils.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * HttpComponent
 *
 * @author wzz
 * @date 2020/8/25 14:09
 **/
@Slf4j
@Component
public class HttpComponent {

    @Autowired
    private CloudProperties cloudProperties;

    public String getLoginToken() {
        Map<String, String> params = new HashMap<>(4);
        params.put("username", cloudProperties.getUsername());
        params.put("password", cloudProperties.getPassword());
        return (String) HttpUtils.post(cloudProperties.getGetLoginTokenUrl(), params);
    }

    public String getPushStreamToken() {
        String loginToken = getLoginToken();
        return (String) HttpUtils.post(cloudProperties.getGetPushStreamTokenUrl(), null, loginToken);
    }

}
