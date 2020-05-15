package com.lcd.base.rx;

import androidx.lifecycle.MutableLiveData;

import com.lcd.base.exception.GlobalError;
import com.lcd.base.remote.Ret;

import org.reactivestreams.Publisher;

import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;

/**
 * Created by Chen on 2019/11/18.
 */
public class GlobalErrorTransformer<T> implements FlowableTransformer<Ret<T>, Ret<T>>, ObservableTransformer<Ret<T>, Ret<T>> {

    private ExceptionTransformer<Ret<T>> exceptionTransformer;

    public GlobalErrorTransformer(RetryConfig retryConfig) {
        this.exceptionTransformer = new ExceptionTransformer<>(retryConfig);
    }

    @Override
    public Publisher<Ret<T>> apply(Flowable<Ret<T>> upstream) {
        return upstream.compose(new ServerRetTransformer<>()).compose(exceptionTransformer);
    }

    @Override
    public ObservableSource<Ret<T>> apply(Observable<Ret<T>> upstream) {
        return upstream.compose(new ServerRetTransformer<>()).compose(exceptionTransformer);
    }

    public MutableLiveData<GlobalError> getErrorMutableLiveData() {
        return exceptionTransformer.getErrorMutableLiveData();
    }
}
