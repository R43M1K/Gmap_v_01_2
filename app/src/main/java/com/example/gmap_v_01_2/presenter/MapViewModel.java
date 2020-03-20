package com.example.gmap_v_01_2.presenter;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.gmap_v_01_2.business.markers.MarkersMainUseCase;

import java.util.ArrayList;
import java.util.HashMap;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class MapViewModel extends ViewModel {

    private final String TAG = getClass().toString();
    private MarkersMainUseCase markersMainUseCase;
    private MutableLiveData<ArrayList<Integer>> removableList = new MutableLiveData<>();
    private MutableLiveData<ArrayList<HashMap>> addableList = new MutableLiveData<>();

    private CompositeDisposable compositeDisposable;

    public MapViewModel(MarkersMainUseCase markersMainUseCase) {
        this.markersMainUseCase = markersMainUseCase;
        this.compositeDisposable = new CompositeDisposable();
    }

    //Check
    public void checkMarkers() {

        compositeDisposable.add(
                Single.fromCallable(() -> markersMainUseCase.checkMarkers())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                hashMap -> {
                                    ArrayList<Integer> list = (ArrayList<Integer>) hashMap.get("remove");
                                    if(list != null && !list.isEmpty()) removableList.setValue(list);

                                    ArrayList<HashMap> mymap = (ArrayList<HashMap>) hashMap.get("add");
                                    if(mymap != null && !mymap.isEmpty()) addableList.setValue(mymap);
                                },
                                error -> Log.e(TAG, "error: " + error.getMessage()))
        );

    }


    public LiveData<ArrayList<Integer>> getRemovableArray() {
        return removableList;
    }

    public LiveData<ArrayList<HashMap>> getAddableArray() {
        return addableList;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }
}
