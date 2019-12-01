package com.example.gmap_v_01_2.repository.markers.repo;

import android.content.Context;
import android.util.Log;

import com.example.gmap_v_01_2.repository.model.users.Markers;
import com.example.gmap_v_01_2.repository.model.users.UserDocumentAll;
import com.example.gmap_v_01_2.repository.services.firestore.OnUserDocumentReady;
import com.example.gmap_v_01_2.repository.services.firestore.UserFirestoreService;
import com.example.gmap_v_01_2.repository.services.firestore.UserService;
import com.example.gmap_v_01_2.repository.services.firestore.model.UserDocument;
import com.example.gmap_v_01_2.repository.services.markers.MarkerService;
import com.example.gmap_v_01_2.repository.services.markers.UserMarkersService;
import com.example.gmap_v_01_2.repository.services.preferencies.DefaultPreferencesService;
import com.google.android.gms.maps.GoogleMap;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.HashMap;

public class ProvideMarkersOp implements ProvideMarkersOperations {

    private Context context;
    private GoogleMap mMap;
    private UserDocument userDocument;
    private UserDocumentAll userDocumentAll;
    private DefaultPreferencesService preferences;
    private UserService firestoreService;
    private MarkerService markerService;
    private MarkersPoJo markersPoJo;
    //Constants
    private final String TAG = getClass().toString();
    private final String SHARED_LONGITUDE = "Longitude";
    private final String SHARED_LATITUDE = "Latitude";
    private final String VISIBLE = "Visible";
    private final String DOCUMENT_ID = "DocumentId";
    //Arrays
    private ArrayList<Markers> markerList = new ArrayList<>();
    private ArrayList<UserDocumentAll> listInBounds = new ArrayList<>();
    private ArrayList<UserDocumentAll> addableList = new ArrayList<>();
    private ArrayList<Integer> removableList = new ArrayList<>();

    public ProvideMarkersOp(Context context, GoogleMap mMap) {
        this.context = context;
        this.mMap = mMap;
        userDocument = new UserDocument();
        userDocumentAll = new UserDocumentAll();
        preferences = DefaultPreferencesService.getInstance(context);
        firestoreService = UserFirestoreService.getInstance(context);
        markerService = new UserMarkersService(context);
        markersPoJo = MarkersPoJo.getInstance();
        init();
    }

    private void init() {
        //Find user , if user not found create user in firebase
        firestoreService.findUserById(new OnUserDocumentReady() {
            @Override
            public void onReady(UserDocument document) {
                if(document == null) {
                    firestoreService.addUser(userDocument);
                }else{
                    Log.d(TAG, "User found");
                }
            }

            @Override
            public void onFail() {
                firestoreService.addUser(userDocument);
            }

            @Override
            public void onFail(Throwable cause) {
                firestoreService.addUser(userDocument);
            }
        });
        //Add created user info to MarkersPoJo
        String id = preferences.get(DOCUMENT_ID, "");
        String username = getUserInfo().getUsername();
        String link = getUserInfo().getPicture();
        GeoPoint location = getUserInfo().getLocation();
        int followers = getUserInfo().getFollowers();
        boolean visible = getUserInfo().getVisible();
        //Add current user to ListInBounds
        userDocumentAll.setDocumentid(id);
        userDocumentAll.setUsername(username);
        userDocumentAll.setPicture(link);
        userDocumentAll.setLocation(location);
        userDocumentAll.setFollowers(followers);
        userDocumentAll.setVisible(visible);
        addableList.add(userDocumentAll);
    }

    private UserDocument getUserInfo() {
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
        return userDocument;
    }

    @Override
    public void checkMarkers() {
        listInBounds = firestoreService.getInBoundUsers(mMap);
        markerList = markersPoJo.getMarkerList();
        markersPoJo.setListInBounds(listInBounds);
        //Remove
        //TODO get removable list from service
        markersPoJo.setRemovableList(markerService.markersToBeRemoved(markerList, listInBounds));
        removableList = markersPoJo.getRemovableList();
        //Add
        //TODO get addable list from service
        markersPoJo.setAddableList(markerService.markersToBeAdded(markerList, listInBounds));
        addableList = markersPoJo.getAddableList();
        firestoreService.updateUser(getUserInfo());
    }

    @Override
    public ArrayList<Integer> removeMarkers() {
        ArrayList<Integer> result = new ArrayList<>();
        if(removableList != null && !removableList.isEmpty()) {
            if (markerList != null && !markerList.isEmpty()) {
                for (int i = 0; i < removableList.size(); i++) {
                    int myIndex = removableList.get(i);
                    result.add(removableList.get(i));
                    markersPoJo.getUserfullpicture().remove(myIndex);
                    markersPoJo.getUserpictureList().remove(myIndex);
                    markersPoJo.getUsernameList().remove(myIndex);
                    markersPoJo.getUserfollowersList().remove(myIndex);
                }
            }
        }
        return result;
    }

    @Override
    public ArrayList<HashMap> addMarkers() {
        ArrayList<HashMap> result = new ArrayList<>();
        if(addableList != null && !addableList.isEmpty()) {
            for (int i = 0; i < addableList.size(); i++) {
                String id = addableList.get(i).getDocumentid();
                String link = addableList.get(i).getPicture();
                String username = addableList.get(i).getUsername();
                GeoPoint location = addableList.get(i).getLocation();
                int followers = addableList.get(i).getFollowers();
                boolean visible = addableList.get(i).getVisible();
                boolean moveCamera = false;
                if(id.equals(preferences.get(DOCUMENT_ID,""))) {
                    moveCamera = true;
                }
                result.add(markerService.addMarker(id, username, link, location, followers, visible, moveCamera));
            }
        }
        return result;
    }

}
