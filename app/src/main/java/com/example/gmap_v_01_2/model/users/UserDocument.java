package com.example.gmap_v_01_2.model.users;

import com.google.firebase.firestore.GeoPoint;

public class UserDocument {
    //USE THIS CLASS AS A DOCUMENT, INCLUDED VALUES ARE PARAMETERS FROM COLLECTION DOCUMENT userinfo
    //WRITE DOCUMENT WITH THIS PARAMETERS IN THIS CLASS. LATER USE THIS CLASS GETTERS TO READ DATA

    private String username;
    private GeoPoint location;
    private String picture;
    private int followers;
    private boolean visible;

    public UserDocument(String username, GeoPoint location, String picture, int followers, boolean visible) {
        this.username = username;
        this.location = location;
        this.picture = picture;
        this.followers = followers;
        this.visible = visible;
    }

    public int getFollowers() {
        return followers;
    }

    public void setFollowers(int followers) {
        this.followers = followers;
    }

    public boolean getVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public UserDocument() {

    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }
}
