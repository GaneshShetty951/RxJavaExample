package com.example.ganeshshetty.rxjavaexample;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MAIN ACTIVITY";

    private final CompositeDisposable disposable = new CompositeDisposable();

    static Observable<String> sampleObservable() {
        return Observable.defer(new Callable<ObservableSource<? extends String>>() {
            @Override
            public ObservableSource<? extends String> call() {
                //Do some long running operation
                SystemClock.sleep(5000);
                return Observable.just("One", "Two", "Three", "Four", "Five");
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.test_rxjava_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRunSchedulerExampleButtonClicked();
            }
        });
    }

    void onRunSchedulerExampleButtonClicked() {
        disposable.add(sampleObservable()
                .subscribeOn(Schedulers.io())                                       // Threading the processes
                .observeOn(AndroidSchedulers.mainThread())                          // Handling some task on UIthread
                .subscribeWith(new DisposableObserver<String>() {

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete()");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError()", e);
                    }

                    @Override
                    public void onNext(String string) {
                        Log.d(TAG, "onNext(" + string + ")");
                    }
                }));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.clear();
    }
}
