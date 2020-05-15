package com.lcd.base.rx;

import java.util.concurrent.TimeUnit;

/**
 * Created by Chen on 2019/11/18.
 */
public class RetryConfig {

    int maxRetries;
    int retryDelay;
    TimeUnit unit;

    public RetryConfig(int maxRetries, int retryDelay) {
        this(maxRetries, retryDelay, TimeUnit.SECONDS);
    }

    public RetryConfig(int maxRetries, int retryDelay, TimeUnit unit) {
        this.maxRetries = maxRetries;
        this.retryDelay = retryDelay;
        this.unit = unit;
    }

}
