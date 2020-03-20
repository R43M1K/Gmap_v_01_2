package com.example.gmap_v_01_2.repository.model.instagram.personal;

import com.google.gson.annotations.SerializedName;

public class ObjectResponse {

    @SerializedName("graphql")
    private Graphql graphql;

    public Graphql getGraphql() {
        return graphql;
    }
}
