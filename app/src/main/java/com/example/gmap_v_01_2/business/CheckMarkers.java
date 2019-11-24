package com.example.gmap_v_01_2.business;

import com.example.gmap_v_01_2.repository.ProvideMarkers;
import com.example.gmap_v_01_2.repository.ProvideMarkersStateRepo;
import com.example.gmap_v_01_2.repository.model.users.Markers;
import com.example.gmap_v_01_2.repository.model.users.UserDocumentAll;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.HashMap;

public class CheckMarkers implements CheckMarkersUseCase {

    ProvideMarkersStateRepo provideMarkersStateRepo;

    public CheckMarkers(ProvideMarkersStateRepo provideMarkersStateRepo) {
        this.provideMarkersStateRepo = provideMarkersStateRepo;
    }

    @Override
    public ArrayList markersToBeRemoved(ArrayList<Markers> markerList, ArrayList<UserDocumentAll> listInBounds) {
        return provideMarkersStateRepo.markersToBeRemoved(markerList, listInBounds);
    }

    @Override
    public ArrayList markersToBeAdded(ArrayList<Markers> markerList, ArrayList<UserDocumentAll> listInBounds) {
        return provideMarkersStateRepo.markersToBeAdded(markerList, listInBounds);
    }

    @Override
    public HashMap addMarker(String documentId, String userName, String userPicture, GeoPoint userLocatoin, int userFollowers, boolean userVisible, boolean moveCamera) {
        return provideMarkersStateRepo.addMarker(documentId, userName, userPicture, userLocatoin, userFollowers, userVisible, moveCamera);
    }
}
