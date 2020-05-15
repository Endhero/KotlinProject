package com.lcd.base.remote;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Default server response
 * Created by Chen on 2016/1/18.
 */
public class Ret<T> {

    @JSONField(name = "Code")
    public int code;
    @JSONField(name = "Message")
    public String msg;
    @JSONField(name = "Success")
    public boolean success;
    @JSONField(name = "Data")
    public T data;

    @Override
    public String toString() {
        return "RET:" + code + ", MSG:" + msg;
    }

    public static <T> Ret<T> newRet(boolean success) {
        Ret<T> ret = new Ret<>();
        ret.success = success;
        return ret;
    }
}
