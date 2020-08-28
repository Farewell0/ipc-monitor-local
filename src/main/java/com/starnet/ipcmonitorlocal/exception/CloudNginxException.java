package com.starnet.ipcmonitorlocal.exception;

/**
 * CloudNginxException
 *
 * @author wzz
 * @date 2020/8/19 15:40
 **/
public class CloudNginxException extends RuntimeException {
    public CloudNginxException() {
        super();
    }

    public CloudNginxException(String message) {
        super(message);
    }
}
