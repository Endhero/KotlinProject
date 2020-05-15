package com.lcd.base.vm;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.lcd.base.exception.GlobalError;
import com.lcd.base.rx.GlobalErrorTransformer;
import com.lcd.base.rx.RetryConfig;
import com.uber.autodispose.lifecycle.CorrespondingEventsFunction;
import com.uber.autodispose.lifecycle.LifecycleEndedException;
import com.uber.autodispose.lifecycle.LifecycleScopeProvider;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;

/**
 * Created by Chen on 2019/11/12.
 */
public abstract class BaseViewModel extends AndroidViewModel implements LifecycleScopeProvider<BaseViewModel.ViewModelEvent> {

    private BehaviorSubject<ViewModelEvent> lifecycleEvents = BehaviorSubject.createDefault(ViewModelEvent.CREATED);
    private MediatorLiveData<GlobalError> mediatorLiveData = new MediatorLiveData<>();

    @Override
    public Observable<ViewModelEvent> lifecycle() {
        return lifecycleEvents.hide();
    }

    @Override
    public CorrespondingEventsFunction<ViewModelEvent> correspondingEvents() {
        return DEFAULT_CORRESPONDING_EVENTS;
    }

    @Override
    public ViewModelEvent peekLifecycle() {
        return lifecycleEvents.getValue();
    }

    enum ViewModelEvent {
        CREATED,
        CLEARED
    }

    public BaseViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<GlobalError> getGlobalErrorLiveData() {
        return mediatorLiveData;
    }

    public final <T> GlobalErrorTransformer<T> globalErrorTransformer(int maxRetry, int delay) {
        return globalErrorTransformer(new RetryConfig(maxRetry, delay));
    }

    public final <T> GlobalErrorTransformer<T> globalErrorTransformer() {
        return globalErrorTransformer(0, 0);
    }

    public final <T> GlobalErrorTransformer<T> globalErrorTransformer(RetryConfig config) {
        GlobalErrorTransformer<T> transformer = new GlobalErrorTransformer<>(config);
        mediatorLiveData.addSource(transformer.getErrorMutableLiveData(), globalError -> mediatorLiveData.setValue(globalError));
        return transformer;
    }

    @Override
    protected void onCleared() {
        lifecycleEvents.onNext(ViewModelEvent.CLEARED);
        super.onCleared();
    }

    private static final CorrespondingEventsFunction<ViewModelEvent> DEFAULT_CORRESPONDING_EVENTS = event -> {
        if (event == ViewModelEvent.CREATED) {
            return ViewModelEvent.CLEARED;
        }
        throw new LifecycleEndedException(
                "Cannot bind to ViewModel lifecycle after onCleared.");
    };
}
