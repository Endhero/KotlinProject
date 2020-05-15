package com.lcd.base.rx;

import org.reactivestreams.Publisher;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;

/**
 * Created by Chen on 2019/11/18.
 */
public class FlowableRetryDelay implements Function<Flowable<? extends Throwable>, Publisher<?>> {

    private RetryConfig config;
    private int count;

    public FlowableRetryDelay(RetryConfig config) {
        this.config = config;
    }

    @Override
    public Publisher<?> apply(Flowable<? extends Throwable> flowable) {
        return flowable.flatMap((Function<Throwable, Publisher<?>>) throwable -> {
            if (++count < config.maxRetries) {
                return Flowable.timer(config.retryDelay, config.unit);
            } else {
                return Flowable.error(throwable);
            }
        });
    }
}
