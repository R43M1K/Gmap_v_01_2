package com.example.gmap_v_01_2.repository.markers.repo;

import android.content.Context;

import com.example.gmap_v_01_2.business.CheckMarkersUseCase;
import com.example.gmap_v_01_2.repository.model.users.Markers;
import com.example.gmap_v_01_2.repository.model.users.UserDocumentAll;
import com.example.gmap_v_01_2.repository.services.firestore.UserFirestoreService;
import com.example.gmap_v_01_2.repository.services.firestore.UserService;
import com.example.gmap_v_01_2.repository.services.firestore.model.UserDocument;
import com.example.gmap_v_01_2.repository.services.preferencies.DefaultPreferencesService;
import com.google.android.gms.maps.GoogleMap;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.HashMap;

public class ProvideMarkersOp implements ProvideMarkersOperations {

    private Context context;
    private GoogleMap mMap;
    private UserDocument userDocument;
    private DefaultPreferencesService preferences;
    private UserService firestoreService;
    private CheckMarkersUseCase checkMarkersUseCase;
    private MarkersPoJo markersPoJo;
    //Constants
    private final String SHARED_LONGITUDE = "Longitude";
    private final String SHARED_LATITUDE = "Latitude";
    private final String VISIBLE = "Visible";
    //Arrays
    private ArrayList<Markers> markerList = new ArrayList<>();
    private ArrayList<UserDocumentAll> listInBounds = new ArrayList<>();
    private ArrayList<UserDocumentAll> addableList = new ArrayList<>();
    private ArrayList<Integer> removableList = new ArrayList<>();


    public ProvideMarkersOp(Context context, GoogleMap mMap, CheckMarkersUseCase checkMarkersUseCase) {
        this.context = context;
        this.mMap = mMap;
        userDocument = new UserDocument();
        preferences = DefaultPreferencesService.getInstance(context);
        firestoreService = UserFirestoreService.getInstance(context);
        this.checkMarkersUseCase = checkMarkersUseCase;
        markersPoJo = MarkersPoJo.getInstance();
    }

    @Override
    public void checkMarkers() {
        userDocument.setUsername("Razmik1993");
        userDocument.setPicture("https://image.freepik.com/free-vector/abstract-dynamic-pattern-wallpaper-vector_53876-59131.jpg");
        userDocument.setFollowers(5478);

        String longitudePrefs = preferences.get(SHARED_LONGITUDE, "");
        longitudePrefs = longitudePrefs == null || longitudePrefs.isEmpty()? "0": longitudePrefs;
        double longitude = Double.valueOf(longitudePrefs);

        String latitudePrefs = preferences.get(SHARED_LATITUDE, "");
        latitudePrefs = latitudePrefs == null || latitudePrefs.isEmpty()? "0": latitudePrefs;
        double latitude = Double.valueOf(latitudePrefs);

        userDocument.setVisible(preferences.get(VISIBLE, true));
        userDocument.setLocation(new GeoPoint(latitude, longitude));
        listInBounds = firestoreService.getInBoundUsers(mMap);
        markerList = markersPoJo.getMarkerList();
        markersPoJo.setListInBounds(listInBounds);
        //Remove
        markersPoJo.setRemovableList(checkMarkersUseCase.markersToBeRemoved(markerList, listInBounds));
        removableList = markersPoJo.getRemovableList();
        if(removableList != null && !removableList.isEmpty()) {
            removeMarkers();
        }
        //Add
        markersPoJo.setAddableList(checkMarkersUseCase.markersToBeAdded(markerList, listInBounds));
        addableList = markersPoJo.getAddableList();
        if(addableList != null && !addableList.isEmpty()) {
            addMarkers();
        }
        firestoreService.updateUser(userDocument);
    }

    @Override
    public ArrayList<HashMap> addMarkers() {
        ArrayList<HashMap> result = new ArrayList<>();
        for (int i = 0; i < addableList.size(); i++) {
            String id = addableList.get(i).getDocumentid();
            String link = addableList.get(i).getPicture();
            String username = addableList.get(i).getUsername();
            GeoPoint location = addableList.get(i).getLocation();
            int followers = addableList.get(i).getFollowers();
            boolean visible = addableList.get(i).getVisible();
            result.add(checkMarkersUseCase.addMarker(id, username, link, location, followers, visible, false));
        }
        return result;
    }

    @Override
    public ArrayList<Integer> removeMarkers() {
        ArrayList<Integer> result = new ArrayList<>();
        if(markerList != null && !markerList.isEmpty()) {
            for (int i = 0; i < removableList.size(); i++) {
                result.add(i);
                markersPoJo.getUserpictureList().remove(i);
                markersPoJo.getUsernameList().remove(i);
                markersPoJo.getUserfollowersList().remove(i);
                markersPoJo.getUserfullpicture().remove(i);
            }
        }
        return result;
    }
}
