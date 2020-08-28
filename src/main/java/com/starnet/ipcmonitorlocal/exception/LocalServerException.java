package com.starnet.ipcmonitorlocal.exception;

/**
 * LocalServerException
 *
 * @author wzz
 * @date 2020/8/19 15:38
 **/
public class LocalServerException extends RuntimeException {
    public LocalServerException() {
        super();
    }

    public LocalServerException(String message) {
        super(message);
    }
}
