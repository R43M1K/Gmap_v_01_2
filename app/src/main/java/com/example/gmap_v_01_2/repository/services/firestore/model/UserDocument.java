package com.example.gmap_v_01_2.repository.services.firestore.model;

import com.google.firebase.firestore.GeoPoint;

public class UserDocument {
    //USE THIS CLASS AS A DOCUMENT, INCLUDED VALUES ARE PARAMETERS FROM COLLECTION DOCUMENT userinfo
    //WRITE DOCUMENT WITH THIS PARAMETERS IN THIS CLASS. LATER USE THIS CLASS GETTERS TO READ DATA

    private String username;

    private GeoPoint location;

    private String picture;

    private boolean isprivate;

    private boolean isverified;

    private Long followers;

    private boolean isVisible;

    private Long userId;

    private String token;

    public UserDocument() {

    }


    public Long getFollowers() {
        return followers;
    }

    public void setFollowers(Long followers) {
        this.followers = followers;
    }

    public boolean getVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        this.isVisible = visible;
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

    public boolean getIsprivate() {
        return isprivate;
    }

    public void setIsprivate(boolean isprivate) {
        this.isprivate = isprivate;
    }

    public boolean getIsverified() {
        return isverified;
    }

    public void setIsverified(boolean isverified) {
        this.isverified = isverified;
    }

    public boolean getIsVisible() {
        return isVisible;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
