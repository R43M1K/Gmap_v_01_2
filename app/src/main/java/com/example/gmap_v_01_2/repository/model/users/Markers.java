package com.example.gmap_v_01_2.repository.model.users;

import com.google.android.gms.maps.model.Marker;

public class Markers {

    private String markerId;
    private String documentId;
    private Marker marker;

    public Markers() {

    }

    public String getMarkerId() {
        return markerId;
    }

    public void setMarkerId(String markerId) {
        this.markerId = markerId;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public Marker getMarker() {
        return marker;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }
}
