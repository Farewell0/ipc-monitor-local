package com.starnet.ipcmonitorlocal.utils;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Map;

/**
 * HttpUtils
 *
 * @author wzz
 * @date 2020/8/25 10:17
 **/
@Slf4j
public class HttpUtils {

    public static Object post(String url, Map<String, String> map) {
        return post(url, map, null);
    }

    public static Object post(String url, Map<String, String> map, String token) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        if (!StringUtils.isEmpty(token)) {
            headers.set("Authorization", token);
        }
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        if (null != map && !map.isEmpty()) {
            map.forEach(params::add);
        }
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(params, headers);
        String jsonRes = restTemplate.postForObject(url, httpEntity, String.class);
        CloudHttpResp resp = null;
        try {
            resp = JsonUtils.fromJson(jsonRes, CloudHttpResp.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (null != resp && 200 == resp.code) {
            return resp.data;
        } else {
            return null;
        }
    }

    @Data
    public static class CloudHttpResp {
        private int code;
        private String message;
        private Object data;
    }
}
