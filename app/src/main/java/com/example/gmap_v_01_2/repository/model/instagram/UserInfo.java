package com.example.gmap_v_01_2.repository.model.instagram;

import com.google.gson.annotations.SerializedName;

public class UserInfo {

    @SerializedName("account_type")
    private String account_type;

    @SerializedName("id")
    private Long id;

    @SerializedName("media_count")
    private Long media_count;

    @SerializedName("username")
    private String username;

    public String getAccount_type() {
        return account_type;
    }

    public Long getId() {
        return id;
    }

    public Long getMedia_count() {
        return media_count;
    }

    public String getUsername() {
        return username;
    }
}
