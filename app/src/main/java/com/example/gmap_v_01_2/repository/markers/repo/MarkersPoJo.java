package com.example.gmap_v_01_2.repository.markers.repo;

import com.example.gmap_v_01_2.repository.model.users.Markers;
import com.example.gmap_v_01_2.repository.model.users.UserDocumentAll;

import java.util.ArrayList;

public class MarkersPoJo {

    private ArrayList<String> usernameList = new ArrayList<>();
    private ArrayList<String> userpictureList = new ArrayList<>();
    private ArrayList<String> userfollowersList = new ArrayList<>();
    private ArrayList<String> userfullpicture = new ArrayList<>();
    private ArrayList<Markers> markerList = new ArrayList<>();
    private ArrayList<UserDocumentAll> listInBounds = new ArrayList<>();
    private ArrayList<UserDocumentAll> addableList = new ArrayList<>();
    private ArrayList<Integer> removableList = new ArrayList<>();

    private MarkersPoJo() {

    }

    private static MarkersPoJo INSTANCE = null;

    public static MarkersPoJo getInstance() {
        if(INSTANCE == null) {
            synchronized (MarkersPoJo.class) {
                if(INSTANCE == null) {
                    INSTANCE = new MarkersPoJo();
                }
            }
        }
        return INSTANCE;
    }

    public ArrayList<String> getUsernameList() {
        return usernameList;
    }

    public void setUsernameList(ArrayList<String> usernameList) {
        this.usernameList = usernameList;
    }

    public ArrayList<String> getUserpictureList() {
        return userpictureList;
    }

    public void setUserpictureList(ArrayList<String> userpictureList) {
        this.userpictureList = userpictureList;
    }

    public ArrayList<String> getUserfollowersList() {
        return userfollowersList;
    }

    public void setUserfollowersList(ArrayList<String> userfollowersList) {
        this.userfollowersList = userfollowersList;
    }

    public ArrayList<String> getUserfullpicture() {
        return userfullpicture;
    }

    public void setUserfullpicture(ArrayList<String> userfullpicture) {
        this.userfullpicture = userfullpicture;
    }

    public ArrayList<Markers> getMarkerList() {
        return markerList;
    }

    public void setMarkerList(ArrayList<Markers> markerList) {
        this.markerList = markerList;
    }

    public ArrayList<UserDocumentAll> getListInBounds() {
        return listInBounds;
    }

    public void setListInBounds(ArrayList<UserDocumentAll> listInBounds) {
        this.listInBounds = listInBounds;
    }

    public ArrayList<UserDocumentAll> getAddableList() {
        return addableList;
    }

    public void setAddableList(ArrayList<UserDocumentAll> addableList) {
        this.addableList = addableList;
    }

    public ArrayList<Integer> getRemovableList() {
        return removableList;
    }

    public void setRemovableList(ArrayList<Integer> removableList) {
        this.removableList = removableList;
    }

    public static void setINSTANCE(MarkersPoJo INSTANCE) {
        MarkersPoJo.INSTANCE = INSTANCE;
    }
}
