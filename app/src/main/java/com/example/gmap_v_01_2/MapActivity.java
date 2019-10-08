package com.example.gmap_v_01_2;


import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.gmap_v_01_2.Editor.FollowerProcessing;
import com.example.gmap_v_01_2.Editor.ImageProcessing;
import com.example.gmap_v_01_2.Reader.ReadFromAsset;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {


    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    //vars
    private GoogleMap mMap;
    private Boolean mLocationPermissionsGranted = false;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    public double latitude;
    public double longitude;
    public int followers;

    //classes
    ImageProcessing imageProcessing = new ImageProcessing();
    FollowerProcessing followersProcessing = new FollowerProcessing();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        getLocationPermission();
    }


    //INITIALIZE MAP
    private void initMap(){
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapActivity.this);
    }

    //CHECK PERMISSION FOR LOCATION GOOGLE MAP
    private void getLocationPermission(){
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionsGranted = true;
                initMap();
            }else{
                ActivityCompat.requestPermissions(this,permissions,LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else{
            ActivityCompat.requestPermissions(this,permissions,LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    //CHECK REQUEST PERMISSION RESULT FOR GOOGLE MAP
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionsGranted = false;

        switch (requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if(grantResults.length > 0){
                    for(int i = 0; i < grantResults.length; i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            mLocationPermissionsGranted = false;
                            return;
                        }
                    }
                    mLocationPermissionsGranted = true;
                    //map can be initialized
                    initMap();
                }
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        MarkerOptions markerOptions = new MarkerOptions();
        Bitmap bitmap;
        Bitmap roundBitMap;
        Bitmap resizedBitMap;
        ArrayList<Double> LongArray;
        ArrayList<Double> LatArray;
        ArrayList<Integer> FollowersArray;
        ArrayList<String> ProfilePicArray;
        String drawableFolderName = "drawable";
        ReadFromAsset readFromAsset = new ReadFromAsset();



        //SHOW CUSTOM MARKERS ON MAP
        LongArray = readFromAsset.readLong(getApplicationContext());
        LatArray = readFromAsset.readLat(getApplicationContext());
        FollowersArray = readFromAsset.ReadFollowers(getApplicationContext());
        ProfilePicArray = readFromAsset.ReadPic(getApplicationContext());

        for (int i = 0; i < LatArray.size(); i++) {


            LatLng userLongLat = new LatLng(LatArray.get(i), LongArray.get(i)); // Get current "i" user longitude latitude

            int indexofDrawable = ProfilePicArray.get(i).indexOf("drawable") + drawableFolderName.length() + 1; // Get profile pic position from path
            String profileImg = ProfilePicArray.get(i).substring(indexofDrawable); // Get profile pic name from it's position
            int picture = getResources().getIdentifier(profileImg, "drawable", getPackageName()); // Get ID of drawable profile pic name
            //Add Custom Icon on MAP
            bitmap = BitmapFactory.decodeResource(getResources(), picture); // Create bitmap with drawable ID of pic name
            resizedBitMap = imageProcessing.getResizedBitmap(bitmap, FollowersArray.get(i)); // Resize bitmap
            roundBitMap = imageProcessing.getCroppedBitmap(resizedBitMap); // Make current bitmap to round type
            String userFollowers = followersProcessing.instagramFollowersType(FollowersArray.get(i));

            markerOptions.position(userLongLat);
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(roundBitMap));
            markerOptions.title(ProfilePicArray.get(i).substring(indexofDrawable) + " : " + userFollowers + " Followers");
            mMap.setMinZoomPreference(18f); // User cannot zoom out of 18 zoom distance
            mMap.addMarker(markerOptions); // Adding marker on map
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLongLat, 19f)); // Move camera to user location in 19 zoom value
        }
        Toast.makeText(this,"Map is ready",Toast.LENGTH_SHORT).show();
        getDeviceLocation(mMap);
    }



    public void getDeviceLocation(GoogleMap googleMap) {
        mMap = googleMap;
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

                try {
                        Task location = mFusedLocationProviderClient.getLastLocation();
                        location.addOnCompleteListener(new OnCompleteListener() {
                            public void onComplete(@NonNull Task task) {
                                if (task.isSuccessful()) {
                                    MarkerOptions markerOptions = new MarkerOptions();
                                    Bitmap bitmap;
                                    Bitmap roundBitMap;
                                    Bitmap resizedBitMap;
                                    String drawableFolderName = "drawable";
                                    //PLACE HERE READ USER PROFILE PICTURE FROM INSTAGRAM
                                    String papka = "D:\\Android\\Gmap_v_01\\app\\src\\main\\res\\drawable\\babydriver";
                                    Location currentLocation = (Location) task.getResult();


                                    if ((currentLocation != null)) {

                                        latitude = currentLocation.getLatitude();
                                        longitude = currentLocation.getLongitude();
                                        //GeoPoint geo_point =  new GeoPoint(latitude,longitude);

                                        //PLACE HERE READ USER FOLLOWERS FROM INSTAGRAM
                                        followers = 45;
                                        String userFollowers = followersProcessing.instagramFollowersType(followers);


                                        int indexofDrawable = papka.indexOf("drawable") + drawableFolderName.length() + 1; // Get profile pic position from path
                                        String profileImg = papka.substring(indexofDrawable); // Get profile pic name from it's position
                                        int picture = getResources().getIdentifier(profileImg, "drawable", getPackageName()); // Get ID of drawable profile pic name
                                        //Add Custom Icon on MAP
                                        bitmap = BitmapFactory.decodeResource(getResources(), picture); // Create bitmap with drawable ID of pic name
                                        resizedBitMap = imageProcessing.getResizedBitmap(bitmap, followers); // Resize bitmap
                                        roundBitMap = imageProcessing.getCroppedBitmap(resizedBitMap); // Make current bitmap to round type

                                        LatLng userLongLat = new LatLng(latitude, longitude);
                                        markerOptions.position(userLongLat);
                                        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(roundBitMap));
                                        markerOptions.title(papka.substring(indexofDrawable) + " : " + userFollowers + " Followers");
                                        mMap.setMinZoomPreference(18f); // User cannot zoom out of 18 zoom distance
                                        mMap.addMarker(markerOptions); // Adding marker on map
                                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLongLat, 19f)); // Move camera to user location in 19 zoom value
                                        LatLngBounds bounds = mMap.getProjection().getVisibleRegion().latLngBounds; // Find current camera position rectancle boundaries

                                        //registerUserToFirebase(geo_point);

                                    } else {
                                        Toast.makeText(MapActivity.this, "Cant Find your location", Toast.LENGTH_SHORT).show();
                                    }


                                } else {
                                    Toast.makeText(MapActivity.this, "task was not succesfull", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                } catch (SecurityException e) {
                }

    }

    /*private void registerUserToFirebase(GeoPoint geoPoint){

        Map<String, Object> user = new HashMap<>();
        user.put("username", "Alone Under Rain");
        user.put("location", geoPoint);

        db.collection("userinfo")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(MapActivity.this, "Your location added to server", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MapActivity.this, "Error adding your location to server", Toast.LENGTH_SHORT).show();
                    }
                });

    }*/
}
