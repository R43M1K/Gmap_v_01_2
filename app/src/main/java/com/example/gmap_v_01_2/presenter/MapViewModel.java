package com.example.gmap_v_01_2.presenter;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.gmap_v_01_2.business.markers.MarkersMainUseCase;
import com.example.gmap_v_01_2.repository.model.users.Markers;

import java.util.ArrayList;
import java.util.HashMap;

import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableObserver;
import io.reactivex.CompletableOnSubscribe;
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
import io.reactivex.internal.observers.SubscriberCompletableObserver;
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
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                emitter.onNext(markersMainUseCase.checkMarkers());
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Object o) {
                        HashMap hashMap = (HashMap) o;
                        ArrayList<Integer> list =(ArrayList<Integer>) ((HashMap) o).get("remove");
                        if(list != null) {
                            if(!list.isEmpty()) {
                                removableList.setValue(list);
                            }
                        }
                        ArrayList<HashMap> mymap = (ArrayList<HashMap>) ((HashMap) o).get("add");
                        if(mymap != null) {
                            if(!mymap.isEmpty()) {
                                addableList.setValue(mymap);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "ERRR");
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }


    public LiveData<ArrayList<Integer>> getRemovableArray() {
        return removableList;
    }

    public LiveData<ArrayList<HashMap>> getAddableArray() {
        return addableList;
    }


}
