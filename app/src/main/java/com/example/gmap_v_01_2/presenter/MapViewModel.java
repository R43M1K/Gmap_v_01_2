package com.example.gmap_v_01_2.presenter;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.gmap_v_01_2.business.CheckMarkersUseCase;
import com.example.gmap_v_01_2.business.markers.MarkersMainUseCase;
import com.example.gmap_v_01_2.repository.model.users.Markers;
import com.example.gmap_v_01_2.repository.model.users.UserDocumentAll;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.HashMap;

public class MapViewModel extends ViewModel {

    private MarkersMainUseCase markersMainUseCase;
    private MutableLiveData<ArrayList<Integer>> removableList = new MutableLiveData<>();
    private MutableLiveData<ArrayList<HashMap>> addableList = new MutableLiveData<>();

    public MapViewModel(MarkersMainUseCase markersMainUseCase) {
        this.markersMainUseCase = markersMainUseCase;
    }

    //Check
    public void checkMarkers() {
        markersMainUseCase.checkMarkers();
    }

    //Remove
    public void checkRemovableMarkers() {
        ArrayList<Integer> list = markersMainUseCase.removeMarkers();
        if(list != null) {
            if(!list.isEmpty()) {
                removableList.setValue(list);
            }
        }
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
