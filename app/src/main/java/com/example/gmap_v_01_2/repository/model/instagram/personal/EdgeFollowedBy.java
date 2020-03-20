package com.example.gmap_v_01_2.repository.model.instagram.personal;

import com.google.gson.annotations.SerializedName;

public class EdgeFollowedBy {

    @SerializedName("count")
    private Long followersCount;

    public Long getFollowersCount() {
        return followersCount;
    }
}
