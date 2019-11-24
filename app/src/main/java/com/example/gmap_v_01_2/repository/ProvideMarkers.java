package com.example.gmap_v_01_2.repository;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.annotation.Nullable;

import com.example.gmap_v_01_2.editor.FollowerProcessing;
import com.example.gmap_v_01_2.editor.ImageProcessing;
import com.example.gmap_v_01_2.editor.ImageURLProcessing;
import com.example.gmap_v_01_2.repository.model.users.Markers;
import com.example.gmap_v_01_2.repository.model.users.UserDocumentAll;
import com.example.gmap_v_01_2.repository.services.firestore.UserFirestoreService;
import com.example.gmap_v_01_2.repository.services.firestore.UserService;
import com.example.gmap_v_01_2.repository.services.firestore.model.UserDocument;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class ProvideMarkers implements ProvideMarkersStateRepo {
    Context context;
    UserService firestoreService;
    UserDocument userDocument;
    ImageProcessing imageProcessing;
    FollowerProcessing followersProcessing;

    public ProvideMarkers(Context context) {
        this.context = context;
        firestoreService = UserFirestoreService.getInstance(context);
        userDocument = new UserDocument();
        followersProcessing = new FollowerProcessing();
        imageProcessing = new ImageProcessing(followersProcessing);
    }

    @Nullable
    @Override
    public ArrayList markersToBeRemoved(ArrayList<Markers> markerList, ArrayList<UserDocumentAll> listInBounds) {
        if (!markerList.isEmpty()) {
            //Remove from markerList those markers which don't exist anymore
            ArrayList<Markers> markerListTemp = new ArrayList<>();
            ArrayList<Integer> removable = new ArrayList<>();
            for (int i = 0; i < markerList.size(); i++) {
                boolean found = false;
                for (int j = 0; j < listInBounds.size(); j++) {
                    if (markerList.get(i).getDocumentId().equals(listInBounds.get(j).getDocumentid())) {
                        if (listInBounds.get(j).getVisible()) {
                            double longitude = markerList.get(i).getLatLng().longitude;
                            double latitude = markerList.get(i).getLatLng().latitude;
                            if (longitude == listInBounds.get(j).getLocation().getLongitude() && latitude == listInBounds.get(j).getLocation().getLatitude()) {
                                found = true;
                                markerListTemp.add(markerList.get(i));
                            } else {
                                found = false;
                            }
                        } else {
                            found = false;
                        }
                        break;
                    }
                }
                if (!found) {
                    removable.add(i);
                }
            }
            markerList.clear();
            markerList = markerListTemp;
            return removable;
        }
        return null;
    }

    @Nullable
    @Override
    public ArrayList markersToBeAdded(ArrayList<Markers> markerList, ArrayList<UserDocumentAll> listInBounds) {
        ArrayList<UserDocumentAll> list = new ArrayList<>();
        UserDocumentAll document = new UserDocumentAll();
        if(markerList.isEmpty()) {
            if(!listInBounds.isEmpty()) {
                return listInBounds;
            }
        }else{
            //Add markers from Firebase, if they do not exist on map
            for(int i = 0; i < listInBounds.size(); i++) {
                boolean found = false;
                for(int j = 0; j<markerList.size(); j++) {
                    if(listInBounds.get(i).getDocumentid().equals(markerList.get(j).getDocumentId())) {
                        found = true;
                        break;
                    }
                }
                if(!found) {
                    document.setDocumentid(listInBounds.get(i).getDocumentid());
                    document.setUsername(listInBounds.get(i).getUsername());
                    document.setPicture(listInBounds.get(i).getPicture());
                    document.setLocation(listInBounds.get(i).getLocation());
                    document.setFollowers(listInBounds.get(i).getFollowers());
                    document.setVisible(listInBounds.get(i).getVisible());
                    list.add(document);
                }
            }
            return list;
        }
        return null;
    }

    @Override
    public HashMap addMarker(String documentId, String userName, String userPicture, GeoPoint userLocation, int userFollowers, boolean userVisible, boolean moveCamera) {
            if (userVisible) {
                HashMap markerParams = new HashMap();
                ImageURLProcessing imageURLProcessing = new ImageURLProcessing();
                imageURLProcessing.execute(userPicture);
                try {
                    Bitmap bitmap = imageURLProcessing.get();
                    MarkerOptions markerOptions = new MarkerOptions();
                    Bitmap roundBitMap;
                    Bitmap resizedBitMap;
                    Bitmap userListFragmentBitmap;
                    resizedBitMap = imageProcessing.getResizedBitmap(bitmap, userFollowers); // Resize bitmap
                    roundBitMap = imageProcessing.getCroppedBitmap(resizedBitMap); // Make current bitmap to round type
                    userListFragmentBitmap = imageProcessing.getCroppedBitmap(imageProcessing.getResizedBitmapForUserListFragment(bitmap)); // Make current bitmap for userlist fragment type
                    String userPictureString = imageProcessing.bitmapToString(userListFragmentBitmap); //Convert bitmap to String to send to fragment as param
                    String fullPictureString = imageProcessing.bitmapToString(bitmap);

                    //TODO read current location data from shared prefs
                    LatLng userLongLat = new LatLng(1, 1);

                    if (userLocation != null) {
                        userLongLat = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());
                    }
                    markerOptions.position(userLongLat);
                    markerOptions.icon(BitmapDescriptorFactory.fromBitmap(roundBitMap));
                    String currentFollowers = followersProcessing.instagramFollowersType(userFollowers);
                    markerOptions.title(userName + " : " + currentFollowers + " Followers");
                    LatLng finalUserLongLat = userLongLat;
                    markerParams.put("markerOptions", markerOptions);
                    markerParams.put("documentId", documentId);
                    markerParams.put("LongLat", finalUserLongLat);
                    markerParams.put("userPictureAsString", userPictureString);
                    markerParams.put("userName", userName);
                    markerParams.put("userFollowers", currentFollowers);
                    markerParams.put("fullPictureAsString", fullPictureString);
                    return markerParams;

                } catch (ExecutionException e) {
                    e.printStackTrace();
                    return null;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return null;
                }
            }else{
                return null;
            }
    }
}
