package com.example.gmap_v_01_2.repository.model.instagram.personal;

import com.google.gson.annotations.SerializedName;

public class Graphql {

    @SerializedName("user")
    private User user;

    public User getUser() {
        return user;
    }
}
