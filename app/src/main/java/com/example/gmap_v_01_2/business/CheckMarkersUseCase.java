package com.example.gmap_v_01_2.business;

import com.example.gmap_v_01_2.repository.model.users.Markers;
import com.example.gmap_v_01_2.repository.model.users.UserDocumentAll;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.HashMap;

public interface CheckMarkersUseCase {

    ArrayList<Integer> markersToBeRemoved(ArrayList<Markers> markerList, ArrayList<UserDocumentAll> listInBounds);

    ArrayList<UserDocumentAll> markersToBeAdded(ArrayList<Markers> markerList, ArrayList<UserDocumentAll> listInBounds);

    HashMap addMarker(String documentId, String userName, String userPicture, GeoPoint userLocatoin, int userFollowers, boolean userVisible, boolean moveCamera);

}
