package com.starnet.ipcmonitorlocal.mq.resp;

/**
 * MqStatus
 *
 * @author wzz
 * @date 2020/8/24 14:53
 **/
public enum  MqStatus {
    /**
     * MQ返回的正常回应状态
     */
    OK(200, "OK"),
    /**
     * 云端请求参数错误
     */
    CLOUD_REQUEST_PARAMS_ERROR(400, "Cloud request params error"),
    /**
     * 本地服务端响应超时
     */
    ERROR_SERVER_BUSY(500, "Server busy Error"),
    /**
     * 本地服务端暂无此命令
     */
    SERVER_NO_CMD(501, "Local server not support this command"),
    SERVER_PUSH_STREAM_ERROR(502, "Local server push stream error, you can try again later"),
    SERVER_NO_PUSH_STREAM_ERROR(503, "Local server not push this stream"),
    /**
     * 未知错误
     */
    ERROR_UNKNOWN(100000, "Unknown Error");

    private int code;
    private String msg;
    MqStatus(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    @Override
    public String toString() {
        return "MqStatus{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                '}';
    }

    public static MqStatus valueOf(int code) {
        for(MqStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        return ERROR_UNKNOWN;
    }

}
