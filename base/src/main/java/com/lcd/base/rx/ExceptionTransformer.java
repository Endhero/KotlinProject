package com.lcd.base.rx;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.lcd.base.BuildConfig;
import com.lcd.base.exception.GlobalError;
import com.lcd.base.exception.ServerRetException;

import org.reactivestreams.Publisher;

import java.net.UnknownHostException;
import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.functions.Consumer;
import retrofit2.HttpException;

/**
 * Created by Chen on 2019/11/28.
 */
public class ExceptionTransformer<T> implements FlowableTransformer<T, T>, ObservableTransformer<T, T> {

    private static final String TAG = "GlobalErrorTransformer";
    private MutableLiveData<GlobalError> errorMutableLiveData = new MutableLiveData<>();

    private Consumer<Throwable> globalDoOnErrorConsumer = t -> {
        if (t instanceof HttpException) {
            errorMutableLiveData.postValue(new GlobalError(GlobalError.ERROR_SERVER, t));
        } else if (t instanceof UnknownHostException) {
            errorMutableLiveData.postValue(new GlobalError(GlobalError.ERROR_NETWORK, t));
        } else if (t instanceof ServerRetException) {
            ServerRetException exception = (ServerRetException) t;
            errorMutableLiveData.postValue(new GlobalError(GlobalError.ERROR_RET, exception.getCode(), t));
        } else {
            errorMutableLiveData.postValue(new GlobalError(GlobalError.ERROR_UNKNOWN, t));
        }

        if (BuildConfig.DEBUG) {
            Log.e(TAG, t.getMessage(), t);
        }
    };

    private RetryConfig config;

    ExceptionTransformer(RetryConfig config) {
        this.config = config;
    }

    @Override
    public Publisher<T> apply(Flowable<T> upstream) {
        return upstream
                .retryWhen(new FlowableRetryDelay(config))
                .doOnError(globalDoOnErrorConsumer);
    }

    @Override
    public ObservableSource<T> apply(Observable<T> upstream) {
        return upstream
                .retryWhen(new ObservableRetryDelay(config))
                .doOnError(globalDoOnErrorConsumer);
    }

    MutableLiveData<GlobalError> getErrorMutableLiveData() {
        return errorMutableLiveData;
    }
}