package com.starnet.ipcmonitorlocal.mq.resp;

import lombok.Data;

/**
 * MqResponse
 *
 * @author wzz
 * @date 2020/8/21 15:37
 **/
@Data
public class MqResponse {
    private int status;
    private String message;

    public MqResponse(MqStatus mqStatus) {
        this.status = mqStatus.getCode();
        this.message = mqStatus.getMsg();
    }

    public MqResponse(MqStatus mqStatus, String message) {
        this.status = mqStatus.getCode();
        this.message = message;
    }

}
