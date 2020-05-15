package com.lcd.base.exception;

/**
 * Created by Chen on 2019/11/28.
 */
public class ServerRetException extends RuntimeException {

    private int code;

    public ServerRetException(String message) {
        super(message);
    }

    public ServerRetException(int code, String message) {
        this(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
