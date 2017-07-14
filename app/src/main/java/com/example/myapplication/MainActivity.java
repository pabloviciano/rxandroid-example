package com.example.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    RecyclerView mainRecyclerView;
    Button updateButton;
    CompositeDisposable disposable = new CompositeDisposable();
    private TemperatureAdapter temperatureAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        updateButton = (Button) findViewById(R.id.update_bt);
        mainRecyclerView = (RecyclerView) findViewById(R.id.list_rcv);
        mainRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        temperatureAdapter = new TemperatureAdapter(this);
        mainRecyclerView.setAdapter(temperatureAdapter);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateButton.setEnabled(false);
                Observable<List<RestClient.Temperature>> titleObservable = Observable.fromCallable(new Callable<List<RestClient.Temperature>>() {
                    @Override
                    public List<RestClient.Temperature> call() throws Exception {
                        return new RestClient().getTemperatures();
                    }
                });
                Disposable subscriber = titleObservable.
                        subscribeOn(Schedulers.io()).
                        observeOn(AndroidSchedulers.mainThread()).
                        subscribe(new Consumer<List<RestClient.Temperature>>() {
                            @Override
                            public void accept(@NonNull List<RestClient.Temperature> strings) throws Exception {
                                reloadRecyclerView(strings);
                                updateButton.setEnabled(true);
                            }
                        });
                disposable.add(subscriber);
            }
        });
    }

    private void reloadRecyclerView(List<RestClient.Temperature> strings) {
        temperatureAdapter.setTemperatures(strings);

    }

    @Override
    protected void onStop() {
        super.onStop();
        if(disposable != null && !disposable.isDisposed()){
            disposable.dispose();
        }
    }
}
