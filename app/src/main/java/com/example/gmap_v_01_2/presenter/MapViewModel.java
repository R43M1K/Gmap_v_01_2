package com.example.gmap_v_01_2.presenter;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.gmap_v_01_2.business.CheckMarkersUseCase;
import com.example.gmap_v_01_2.repository.model.users.Markers;
import com.example.gmap_v_01_2.repository.model.users.UserDocumentAll;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.HashMap;

public class MapViewModel extends ViewModel {

    private CheckMarkersUseCase checkMarkersUseCase;
    private MutableLiveData<ArrayList<Integer>> removableList = new MutableLiveData<>();
    private MutableLiveData<ArrayList<UserDocumentAll>> addableList = new MutableLiveData<>();
    private MutableLiveData<HashMap> marker = new MutableLiveData<>();

    public MapViewModel(CheckMarkersUseCase checkMarkersUseCase) {
        this.checkMarkersUseCase = checkMarkersUseCase;
    }

    //Remove
    public void checkRemovableMarkers(ArrayList<Markers> markerList, ArrayList<UserDocumentAll> listInBounds) {
        //TODO after checkMarkersUseCase.markersToBeRemoved(markerList, listInBounds) use array list in other use case to remove markers
        ArrayList<Integer> list = checkMarkersUseCase.markersToBeRemoved(markerList, listInBounds);
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

    public void checkAddableMarkers(ArrayList<Markers> markerList, ArrayList<UserDocumentAll> listInBounds) {
        ArrayList<UserDocumentAll> list = checkMarkersUseCase.markersToBeAdded(markerList, listInBounds);
        if(list != null) {
            if(!list.isEmpty()) {
                addableList.setValue(list);
            }
        }
    }

    public LiveData<ArrayList<UserDocumentAll>> getAddableArray() {
        return addableList;
    }

    //Add Marker

    //TODO userLocation GeoPoint -> to pojo
    public void addMarker(String documentId, String userName, String userPicture, GeoPoint userLocatoin, int userFollowers, boolean userVisible, boolean moveCamera) {
        marker.setValue(checkMarkersUseCase.addMarker(documentId,userName,userPicture,userLocatoin,userFollowers,userVisible,moveCamera));
    }

    public LiveData<HashMap> getMarker() {
        return marker;
    }





}
