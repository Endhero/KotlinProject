package com.lcd.base.exception;

/**
 * Created by Chen on 2019/11/18.
 */
public class GlobalError extends RuntimeException {

    public static final int ERROR_SERVER = 1;
    public static final int ERROR_NETWORK = 2;
    public static final int ERROR_UNKNOWN = 3;
    public static final int ERROR_RET = 4;

    public final int type;
    public int code = -1;
    public final Throwable throwable;

    public GlobalError(int type, Throwable throwable) {
        this.type = type;
        this.throwable = throwable;
    }

    public GlobalError(int type, int code, Throwable throwable) {
        this(type, throwable);
        this.code = code;
    }
}
