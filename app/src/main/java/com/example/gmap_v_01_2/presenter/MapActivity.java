package com.example.gmap_v_01_2.presenter;


import android.Manifest;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.gmap_v_01_2.R;
import com.example.gmap_v_01_2.editor.FollowerProcessing;
import com.example.gmap_v_01_2.editor.ImageProcessing;
import com.example.gmap_v_01_2.editor.ImageURLProcessing;
import com.example.gmap_v_01_2.presenter.fragments.UserListFragment;
import com.example.gmap_v_01_2.presenter.fragments.UserPhotoViewerFragment;
import com.example.gmap_v_01_2.repository.markers.repo.MarkersPoJo;
import com.example.gmap_v_01_2.repository.model.users.Markers;
import com.example.gmap_v_01_2.repository.model.users.UserDocumentAll;
import com.example.gmap_v_01_2.repository.services.firestore.OnUserDocumentReady;
import com.example.gmap_v_01_2.repository.services.firestore.UserFirestoreService;
import com.example.gmap_v_01_2.repository.services.firestore.UserService;
import com.example.gmap_v_01_2.repository.services.location.LocationService;
import com.example.gmap_v_01_2.repository.services.firestore.model.UserDocument;
import com.example.gmap_v_01_2.repository.services.preferencies.DefaultPreferencesService;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback , UserListFragment.OnFragmentInteractionListener , UserPhotoViewerFragment.OnPhotoFragmentInteractionListener {


    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final int LOCATION_UPDATE_INTERVAL = 2000;
    private final String TAG = getClass().toString();

    //vars
    private GoogleMap mMap;
    private Boolean mLocationPermissionsGranted = false;
    private Handler iHandler = new Handler();
    public static String username;
    public static GeoPoint location;
    public static String link;
    public static int followers;
    public static boolean visible = true;
    public String documentID;

    //Constants
    private final String SHARED_USERNAME = "Username";
    private final String VISIBLE = "Visible";

    //classes
    Fragment fragment = new UserListFragment();
    Fragment photoFragment = new UserPhotoViewerFragment();
    DefaultPreferencesService defaultPreferencesService;
    UserService firestoreService;
    UserDocument userDocument;
    MarkersPoJo markersPoJo;

    private ViewModelProviderFactory factory;
    private MapViewModel mapViewModel;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        defaultPreferencesService = DefaultPreferencesService.getInstance(getBaseContext());
        userDocument = new UserDocument();
        markersPoJo = MarkersPoJo.getInstance();
        firestoreService = UserFirestoreService.getInstance(getBaseContext());
        getLocationPermission();
        startLocationService();
    }


    //INITIALIZE MAP
    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) mapFragment.getMapAsync(MapActivity.this);
    }

    //CHECK PERMISSION FOR LOCATION GOOGLE MAP
   private void getLocationPermission() {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionsGranted = true;
                initMap();
            } else {
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    //CHECK REQUEST PERMISSION RESULT FOR GOOGLE MAP
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionsGranted = false;

        //TODO show dialog to explain user why this app needs this permission to function correctly, and probably re ask for permissions
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0) {
                for (int grantResult : grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMinZoomPreference(18f); // User cannot zoom out of 18 zoom distance
        factory = new ViewModelProviderFactory(getApplicationContext(),mMap);
        mapViewModel = ViewModelProviders.of(this, factory).get(MapViewModel.class);
        Toast.makeText(this, "Map is ready", Toast.LENGTH_SHORT).show();
        defaultPreferencesService.put(VISIBLE, true);
        visibleChecker();
        openUserList();
        readDocFromFirebase();
    }
    //USE THIS METHOD TO GET ALL USERS INFORMATION, THEN ADD MARKERS IN CURRENT AREA
    private void readDocFromFirebase() {
        mapViewModel.checkAddableMarkers();
        iHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mapViewModel.checkMarkers();
                mapViewModel.checkRemovableMarkers();
                mapViewModel.checkAddableMarkers();
                iHandler.postDelayed(this, LOCATION_UPDATE_INTERVAL);
            }
        }, LOCATION_UPDATE_INTERVAL);
        loadMarkers();
    }

    private void loadMarkers() {
        if(mapViewModel != null) {
            mapViewModel.getRemovableArray().observe(this, arrayList -> {
                for (int i = 0; i < arrayList.size(); i++) {
                    int myIndex = arrayList.get(i);
                    if (myIndex != 0 && i != 0) {
                        myIndex--;
                    }
                    markersPoJo.getMarkerList().get(myIndex).getMarker().remove();
                    markersPoJo.getMarkerList().remove(myIndex);
                }
            });
            mapViewModel.getAddableArray().observe(this, hashMaps -> {
                if (hashMaps != null && !hashMaps.isEmpty() && !hashMaps.contains(null)) {
                    for (int i = 0; i < hashMaps.size(); i++) {
                        MarkerOptions markerOptions = (MarkerOptions) hashMaps.get(i).get("markerOptions");
                        String documentId = (String) hashMaps.get(i).get("documentId");
                        LatLng latLng = (LatLng) hashMaps.get(i).get("LongLat");
                        Marker marker = mMap.addMarker(markerOptions);
                        if ((Boolean) hashMaps.get(i).get("moveCamera")) {
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 19f));
                        }
                        String markerId = marker.getId();
                        ArrayList<Markers> markerList = markersPoJo.getMarkerList();
                        Markers markers = new Markers();
                        markers.setDocumentId(documentId);
                        markers.setLatLng(latLng);
                        markers.setMarker(marker);
                        markers.setMarkerId(markerId);
                        markerList.add(markers);
                        markersPoJo.setMarkerList(markerList);
                    }
                }
            });
        }
    }

    //START BACKGROUND SERVICE THAT GET USER LOCATION WHEN IT'S CHANGED AND WRITE TO FIREBASE
    private void startLocationService() {
        if (!isLocationServiceRunning()) {
            LocationService locationService = new LocationService();
            Intent serviceIntent = new Intent(this, locationService.getClass());
            serviceIntent.putExtra("doc", documentID);
            serviceIntent.putExtra("pic", link);
            serviceIntent.putExtra("fol", followers);
            serviceIntent.putExtra("name", username);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                MapActivity.this.startForegroundService(serviceIntent);
            } else {
                startService(serviceIntent);
            }
        }
    }

    //CHECK IF LOCATION SERVICE IS RUNNING
    private boolean isLocationServiceRunning() {
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if ("com.example.gmap_v_01_2.googledirectionstest.services.LocationService".equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    //CHECK VISIBLE SWITCH POSITION
    private void visibleChecker() {
        Switch aSwitch = findViewById(R.id.visibleSwitch);
        aSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                visible = true;
                defaultPreferencesService.put(VISIBLE, true);
            } else {
                visible = false;
                defaultPreferencesService.put(VISIBLE, false);
            }
        });
    }

    //CLICK ON USERLIST BUTTON
    private void openUserList() {
        Button button = findViewById(R.id.userlist);
        button.setOnClickListener(v -> {
            Switch aSwitch = findViewById(R.id.visibleSwitch);
            if(fragment.isAdded()) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.remove(fragment);
                fragmentTransaction.commit();
                aSwitch.setVisibility(View.VISIBLE);
            } else {
                callFragment();
                aSwitch.setVisibility(View.GONE);
            }
        });
    }

    //ADD FRAGMENT TO ACTIVITY
    private void callFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fframe,fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }


    //FRAGMENT INTERACTION VIA THIS METHOD
    // This method is connectivity between activity and fragment
    // Boolean bool here is read from onFragmentInteraction method defined in UserListFragment
    // bool indicates that in our case Swipe action (from right to left) is used, so we can remove fragment, and show switch button again
    @Override
    public void onFragmentInteraction(Boolean bool,Boolean openPhotoFragment,int pos) {
        Switch aSwitch = findViewById(R.id.visibleSwitch);
        if (bool) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.remove(fragment);
            fragmentTransaction.commit();
            aSwitch.setVisibility(View.VISIBLE);
        }
        if (openPhotoFragment) {
            Bundle bundle = new Bundle();
            bundle.putInt("userFullPicturePosition", pos);
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.fframe,photoFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
            //Send user picture to PhotoView Fragment
            photoFragment.setArguments(bundle);
            aSwitch.setVisibility(View.GONE);
        }
    }

    //Remove Full Size Photo Fragment
    @Override
    public void onPhotoFragmentInteraction(Boolean bool) {
        if(bool) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.remove(photoFragment);
            fragmentTransaction.commit();
        }
    }

    public MapViewModel getMapViewModel() {
        return mapViewModel;
    }
}
