package com.example.gmap_v_01_2.presenter.recyclerview;

public class User_Item {

    private String mPicture;
    private String mUsername;
    private String mFollowers;

    public User_Item (String picture, String username, String followers) {
         mPicture = picture;
         mUsername = username;
         mFollowers = followers;
    }

    public String getPicture() {
        return mPicture;
    }

    public String getUsername() {
        return mUsername;
    }

    public String getFollowers() {
        return mFollowers;
    }

}
