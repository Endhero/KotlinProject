package com.lcd.base.rx;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;

/**
 * Created by Chen on 2019/11/18.
 */
public class ObservableRetryDelay implements Function<Observable<? extends Throwable>, Observable<?>> {

    private RetryConfig config;
    private int count;

    public ObservableRetryDelay(RetryConfig config) {
        this.config = config;
    }

    @Override
    public Observable<?> apply(Observable<? extends Throwable> observable) {
        return observable.flatMap((Function<Throwable, ObservableSource<?>>) throwable -> {
            if (++count < config.maxRetries) {
                return Observable.timer(config.retryDelay, config.unit);
            } else {
                return Observable.error(throwable);
            }
        });
    }
}
