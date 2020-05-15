package com.lcd.base.rx;

import com.lcd.base.exception.ServerRetException;
import com.lcd.base.remote.Ret;

import org.reactivestreams.Publisher;

import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.functions.Function;

/**
 * Created by Chen on 2019/11/28.
 */
public class ServerRetTransformer<T> implements ObservableTransformer<Ret<T>, Ret<T>>, FlowableTransformer<Ret<T>, Ret<T>> {

    private static final String TAG = "GlobalRetHandle";

    @Override
    public ObservableSource<Ret<T>> apply(Observable<Ret<T>> upstream) {
        return upstream.flatMap((Function<Ret<T>, ObservableSource<Ret<T>>>) t -> {
            if (!t.success) {
                throw new ServerRetException(t.code, t.msg);
            }
            return Observable.just(t);
        });
    }

    @Override
    public Publisher<Ret<T>> apply(Flowable<Ret<T>> upstream) {
        return upstream.flatMap((Function<Ret<T>, Publisher<Ret<T>>>) ret -> {
            if (!ret.success) {
                throw new ServerRetException(ret.code, ret.msg);
            }
            return Flowable.just(ret);
        });
    }
}
