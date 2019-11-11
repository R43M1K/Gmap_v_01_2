package com.example.gmap_v_01_2.editor;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.firebase.firestore.GeoPoint;

public class BoundProcessing {

    public Boolean isMarkerInsideBound(GoogleMap mMap, GeoPoint geoPoint) {

        LatLngBounds bounds = mMap.getProjection().getVisibleRegion().latLngBounds;

        double neLongitude =  bounds.northeast.longitude;
        double neLatitude = bounds.northeast.latitude;
        double swLongitude = bounds.southwest.longitude;
        double swLatitude = bounds.southwest.latitude;

        double myLongitude = geoPoint.getLongitude();
        double myLatitude = geoPoint.getLatitude();

        if((myLatitude >= swLatitude) && (myLatitude <= neLatitude)) {
            if((myLongitude >= swLongitude) && (myLongitude <= neLongitude)) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
    
}
