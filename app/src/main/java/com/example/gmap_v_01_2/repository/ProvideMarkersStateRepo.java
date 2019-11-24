package com.example.gmap_v_01_2.repository;

import com.example.gmap_v_01_2.repository.model.users.Markers;
import com.example.gmap_v_01_2.repository.model.users.UserDocumentAll;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.HashMap;

public interface ProvideMarkersStateRepo {

    ArrayList markersToBeRemoved(ArrayList<Markers> markerList, ArrayList<UserDocumentAll> listInBounds);

    ArrayList markersToBeAdded(ArrayList<Markers> markerList, ArrayList<UserDocumentAll> listInBounds);

    HashMap addMarker(String documentId, String userName, String userPicture, GeoPoint userLocatoin, int userFollowers, boolean userVisible, boolean moveCamera);

}
