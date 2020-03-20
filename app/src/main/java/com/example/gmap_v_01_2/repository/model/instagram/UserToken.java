package com.example.gmap_v_01_2.repository.model.instagram;

import com.google.gson.annotations.SerializedName;

public class UserToken {

    @SerializedName("access_token")
    private String access_token;

    @SerializedName("user_id")
    private Long user_id;

    public String getAccess_token() {
        return access_token;
    }

    public long getUser_id() {
        return user_id;
    }
}
