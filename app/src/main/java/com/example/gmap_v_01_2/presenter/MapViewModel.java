package com.example.gmap_v_01_2.presenter;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.gmap_v_01_2.business.markers.MarkersMainUseCase;

import java.util.ArrayList;
import java.util.HashMap;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleObserver;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MapViewModel extends ViewModel {

    private final String TAG = getClass().toString();
    private MarkersMainUseCase markersMainUseCase;
    private MutableLiveData<ArrayList<Integer>> removableList = new MutableLiveData<>();
    private MutableLiveData<ArrayList<HashMap>> addableList = new MutableLiveData<>();

    public MapViewModel(MarkersMainUseCase markersMainUseCase) {
        this.markersMainUseCase = markersMainUseCase;
    }

    //Check
    public void checkMarkers() {
        Single single = Single.create(emitter -> markersMainUseCase.checkMarkers());
        single.subscribeOn(Schedulers.io())
                .observeOn(Schedulers.single())
                .subscribe(new SingleObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "Subscribed");
                    }

                    @Override
                    public void onSuccess(Object o) {
                        Log.d(TAG, "Success");
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });
        //markersMainUseCase.checkMarkers();
    }

    //Remove
    public void checkRemovableMarkers() {
        Observable o = Observable.create(emitter -> emitter.onNext(markersMainUseCase.removeMarkers()));
        o.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Object o) {
                        ArrayList<Integer> list = (ArrayList<Integer>) o;
                        if(list != null) {
                            if(!list.isEmpty()) {
                                removableList.setValue(list);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
        /*
        ArrayList<Integer> list = markersMainUseCase.removeMarkers();
        if(list != null) {
            if(!list.isEmpty()) {
                removableList.setValue(list);
            }
        }

         */
    }

    public LiveData<ArrayList<Integer>> getRemovableArray() {
        return removableList;
    }

    //Add

    public void checkAddableMarkers() {
        ArrayList<HashMap> list = markersMainUseCase.addMarkers();
        if(list != null) {
            if(!list.isEmpty()) {
                addableList.setValue(list);
            }
        }
    }

    public LiveData<ArrayList<HashMap>> getAddableArray() {
        return addableList;
    }


}
