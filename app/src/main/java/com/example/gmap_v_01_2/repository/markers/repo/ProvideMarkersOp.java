package com.example.gmap_v_01_2.repository.markers.repo;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

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
    private ArrayList<UserDocumentAll> oneTimeAddableList = new ArrayList<>();

    public ProvideMarkersOp(Context context, GoogleMap mMap) {
        this.context = context;
        this.mMap = mMap;
        userDocument = new UserDocument();
        userDocumentAll = new UserDocumentAll();
        preferences = DefaultPreferencesService.getInstance(context);
        firestoreService = UserFirestoreService.getInstance(context);
        markerService = new UserMarkersService(context);
        markersPoJo = MarkersPoJo.getInstance();
        addUserAsMarker();
    }

    private void addUserAsMarker() {
        //Add created user info to MarkersPoJo
        firestoreService.findUserById(new OnUserDocumentReady() {
            @Override
            public void onReady(UserDocument userDocument) {
                if(userDocument != null && userDocument.getUserid().equals(preferences.get("user_id",0L))) {
                    String id = preferences.get(DOCUMENT_ID, "");
                    //Add current user to ListInBounds
                    userDocumentAll.setDocumentid(id);
                    userDocumentAll.setUsername(userDocument.getUsername());
                    userDocumentAll.setPicture(userDocument.getPicture());
                    userDocumentAll.setLocation(userDocument.getLocation());
                    userDocumentAll.setFollowers(userDocument.getFollowers());
                    userDocumentAll.setIsvisible(userDocument.getIsvisible());
                    userDocumentAll.setIsprivate(userDocument.getIsprivate());
                    userDocumentAll.setIsverified(userDocument.getIsverified());
                    userDocumentAll.setUserid(userDocument.getUserid());
                    userDocumentAll.setToken(userDocument.getToken());
                    oneTimeAddableList.add(userDocumentAll);
                }
            }

            @Override
            public void onFail() {

            }

            @Override
            public void onFail(Throwable cause) {

            }
        });

    }


    @Override
    public HashMap checkMarkers() {
        HashMap hashMap = new HashMap();
        listInBounds = firestoreService.getInBoundUsers(mMap);
        markerList = markersPoJo.getMarkerList();
        markersPoJo.setListInBounds(listInBounds);
        //Remove
        //TODO get removable list from service
        markersPoJo.setRemovableList(markerService.markersToBeRemoved(markerList, listInBounds));
        removableList = markersPoJo.getRemovableList();
        if (removableList != null && !removableList.isEmpty() && markerList != null && !markerList.isEmpty())
            hashMap.put("remove", removeMarkers(markerList, removableList));
        //Add
        if(!oneTimeAddableList.isEmpty()) {
            addableList.add(oneTimeAddableList.get(0));
            oneTimeAddableList.clear();
        }else {
            markersPoJo.setAddableList(markerService.markersToBeAdded(markerList, listInBounds));
            addableList = markersPoJo.getAddableList();
        }
        hashMap.put("add", addMarkers(addableList));
        firestoreService.updateUser(updateCurrentMarker(userDocument));
        return hashMap;
    }

    private UserDocument updateCurrentMarker(UserDocument userDocument) {
        double longitude = Double.valueOf(preferences.get(SHARED_LONGITUDE,""));
        double latitude = Double.valueOf(preferences.get(SHARED_LATITUDE,""));
        userDocument.setUsername(userDocumentAll.getUsername());
        userDocument.setFollowers(userDocumentAll.getFollowers());
        userDocument.setPicture(userDocumentAll.getPicture());
        userDocument.setIsvisible(userDocumentAll.getIsvisible());
        userDocument.setIsprivate(userDocumentAll.getIsprivate());
        userDocument.setIsverified(userDocumentAll.getIsverified());
        userDocument.setUserid(userDocumentAll.getUserid());
        userDocument.setToken(userDocumentAll.getToken());
        userDocument.setLocation(new GeoPoint(latitude,longitude));
        return userDocument;
    }

    private ArrayList<Integer> removeMarkers(@NonNull ArrayList<Markers> markers, @NonNull ArrayList<Integer> myList) {
        ArrayList<Integer> result = new ArrayList<>();
        for (int i = 0; i < myList.size(); i++) {
                int myIndex = myList.get(i);
                if (myIndex != 0 && i != 0) {
                    myIndex = myIndex - i;
                }
                result.add(myList.get(i));
                markersPoJo.getUserfullpicture().remove(myIndex);
                markersPoJo.getUserpictureList().remove(myIndex);
                markersPoJo.getUsernameList().remove(myIndex);
                markersPoJo.getUserfollowersList().remove(myIndex);
                Log.d(TAG, "Deleted index " + myIndex);
        }
        return result;
    }

    private ArrayList<HashMap> addMarkers(ArrayList<UserDocumentAll> mylist) {
        ArrayList<HashMap> result = new ArrayList<>();
        if(mylist != null && !mylist.isEmpty()) {
            for (int i = 0; i < mylist.size(); i++) {
                String id = mylist.get(i).getDocumentid();
                String link = mylist.get(i).getPicture();
                String username = mylist.get(i).getUsername();
                GeoPoint location = mylist.get(i).getLocation();
                Long followers = mylist.get(i).getFollowers();
                boolean visible = mylist.get(i).getIsvisible();
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
