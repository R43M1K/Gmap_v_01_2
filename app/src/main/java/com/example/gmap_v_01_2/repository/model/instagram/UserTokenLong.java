package com.example.gmap_v_01_2.repository.model.instagram;

import com.google.gson.annotations.SerializedName;

public class UserTokenLong {

    @SerializedName("access_token")
    private String access_token;

    @SerializedName("token_type")
    private String token_type;

    @SerializedName("expires_in")
    private Long expires_in;

    public String getAccess_token() {
        return access_token;
    }

    public String getToken_type() {
        return token_type;
    }

    public Long getExpires_in() {
        return expires_in;
    }
}
