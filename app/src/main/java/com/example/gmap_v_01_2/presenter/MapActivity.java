package com.example.gmap_v_01_2.presenter;


import android.Manifest;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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

import com.example.gmap_v_01_2.R;
import com.example.gmap_v_01_2.editor.FollowerProcessing;
import com.example.gmap_v_01_2.editor.ImageProcessing;
import com.example.gmap_v_01_2.editor.ImageURLProcessing;
import com.example.gmap_v_01_2.presenter.fragments.UserListFragment;
import com.example.gmap_v_01_2.presenter.fragments.UserPhotoViewerFragment;
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
import java.util.concurrent.ExecutionException;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback , UserListFragment.OnFragmentInteractionListener , UserPhotoViewerFragment.OnPhotoFragmentInteractionListener {


    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final int LOCATION_UPDATE_INTERVAL = 2000;

    //vars
    private String instagramUsername = "Razmik1993";
    private GoogleMap mMap;
    private Boolean mLocationPermissionsGranted = false;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Handler iHandler = new Handler();
    private Runnable mRunnable;
    public static String username;
    public static GeoPoint location;
    public static String link;
    public static int followers;
    public static boolean visible = true;
    public String documentID;

    //Constants
    private final String SHARED_DOCUMENT_ID = "DocumentId";
    private final String SHARED_LONGITUDE = "Longitude";
    private final String SHARED_LATITUDE = "Latitude";
    private final String SHARED_USERNAME = "Username";
    private final String SHARED_FOLLOWERS = "Followers";

    //vars to send to fragment
    private ArrayList<String> usernameList = new ArrayList<>();
    private ArrayList<String> userpictureList = new ArrayList<>();
    private ArrayList<String> userfollowersList = new ArrayList<>();
    private ArrayList<String> userfullpicture = new ArrayList<>();
    private ArrayList<Markers> markerList = new ArrayList<>();
    ArrayList<UserDocumentAll> listInBounds = new ArrayList<>();

    //classes
    FollowerProcessing followersProcessing = new FollowerProcessing();
    ImageProcessing imageProcessing = new ImageProcessing(followersProcessing);
    Fragment fragment = new UserListFragment();
    Fragment photoFragment = new UserPhotoViewerFragment();
    DefaultPreferencesService defaultPreferencesService;
    UserService firestoreService;
    UserDocument userDocument;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        defaultPreferencesService = DefaultPreferencesService.getInstance(getBaseContext());
        userDocument = new UserDocument();
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
        Toast.makeText(this, "Map is ready", Toast.LENGTH_SHORT).show();
        defaultPreferencesService.put(SHARED_USERNAME, instagramUsername);
        visibleChecker();
        openUserList();
        readDocFromFirebase();
    }
    //USE THIS METHOD TO GET ALL USERS INFORMATION, THEN ADD MARKERS IN CURRENT AREA
    private void readDocFromFirebase() {

        userDocument.setUsername(instagramUsername);
        userDocument.setPicture("https://image.freepik.com/free-vector/abstract-dynamic-pattern-wallpaper-vector_53876-59131.jpg");
        userDocument.setFollowers(5478);
        userDocument.setVisible(true);
        if(userDocument.getLocation() == null) {

            String longitudePrefs = defaultPreferencesService.get(SHARED_LONGITUDE,"");
            longitudePrefs = longitudePrefs == null || longitudePrefs.isEmpty()? "0": longitudePrefs;
            double longitude = Double.valueOf(longitudePrefs);

            String latitudePrefs = defaultPreferencesService.get(SHARED_LATITUDE, "");
            latitudePrefs = latitudePrefs == null || latitudePrefs.isEmpty()? "0": latitudePrefs;
            double latitude = Double.valueOf(latitudePrefs);

            userDocument.setLocation(new GeoPoint(latitude, longitude));
        }

        String username = userDocument.getUsername();
        GeoPoint location = userDocument.getLocation();
        String link = userDocument.getPicture();
        int followers = userDocument.getFollowers();
        boolean visible = userDocument.getVisible();

        Runnable addMarkerRunnable = new AddMarkerRunnable(defaultPreferencesService.get(SHARED_DOCUMENT_ID,""),link, username, location, followers, visible, true);
        Thread thread = new Thread(addMarkerRunnable);
        thread.start();

        Thread loaderThread = new Thread(() -> {
            while(true) {

                String longitudePrefs = defaultPreferencesService.get(SHARED_LONGITUDE, "");
                longitudePrefs = longitudePrefs == null || longitudePrefs.isEmpty()? "0": longitudePrefs;
                double longitude = Double.valueOf(longitudePrefs);

                String latitudePrefs = defaultPreferencesService.get(SHARED_LATITUDE, "");
                latitudePrefs = latitudePrefs == null || latitudePrefs.isEmpty()? "0": latitudePrefs;
                double latitude = Double.valueOf(latitudePrefs);

                userDocument.setVisible(MapActivity.visible);
                userDocument.setLocation(new GeoPoint(latitude, longitude));
                firestoreService.findUserById(new OnUserDocumentReady() {
                    @Override
                    public void onReady(UserDocument document) {
                        firestoreService.updateUser(userDocument);
                        loadMarkers();
                    }

                    @Override
                    public void onFail() {
                        firestoreService.addUser(userDocument);
                    }

                    @Override
                    public void onFail(Throwable cause) {
                        firestoreService.addUser(userDocument);
                    }
                });

                try {
                    Thread.sleep(LOCATION_UPDATE_INTERVAL);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        loaderThread.start();
    }

    private void loadMarkers() {
        listInBounds = firestoreService.getInBoundUsers(mMap);
        if(markerList.isEmpty()){
            if(!listInBounds.isEmpty()) {
                for(int i=0; i<listInBounds.size(); i++) {
                    String id = listInBounds.get(i).getDocumentid();
                    String link = listInBounds.get(i).getPicture();
                    String username = listInBounds.get(i).getUsername();
                    GeoPoint location = listInBounds.get(i).getLocation();
                    int followers = listInBounds.get(i).getFollowers();
                    boolean visible = listInBounds.get(i).getVisible();
                    /*
                    Thread thread = new Thread(new AddMarkerRunnable(id, link, username, location, followers, visible, false));
                    thread.start();
                     */
                    addMarker(id,link,username,location,followers,visible,false);
                }
            }
        }else{
            //Remove from markerList those markers which don't exist anymore
            ArrayList<Markers> markerListTemp = new ArrayList<>();
            ArrayList<Integer> removable = new ArrayList<>();
            for(int i=0; i<markerList.size(); i++) {
                boolean found = false;
                for(int j=0; j<listInBounds.size(); j++) {
                    if(markerList.get(i).getDocumentId().equals(listInBounds.get(j).getDocumentid())) {
                        if(listInBounds.get(j).getVisible()){
                            double longitude = markerList.get(i).getLatLng().longitude;
                            double latitude = markerList.get(i).getLatLng().latitude;
                            if(longitude == listInBounds.get(j).getLocation().getLongitude() && latitude == listInBounds.get(j).getLocation().getLatitude()) {
                                found = true;
                                markerListTemp.add(markerList.get(i));
                            }else{
                                found = false;
                            }
                        }else{
                            found = false;
                        }
                        break;
                    }
                }
                if(!found) {
                    markerList.get(i).getMarker().remove();
                    removable.add(i);
                }
            }
            markerList.clear();
            markerList = markerListTemp;
            for(int i=0; i<removable.size(); i++) {
                int myIndex = removable.get(i);
                if(myIndex != 0) {
                    myIndex--;
                }
                userpictureList.remove(myIndex);
                usernameList.remove(myIndex);
                userfollowersList.remove(myIndex);
                userfullpicture.remove(myIndex);
            }

            //Add markers from Firebase, if they do not exist on map
            for(int i = 0; i < listInBounds.size(); i++) {
                boolean found = false;
                for(int j = 0; j<markerList.size(); j++) {
                    if(listInBounds.get(i).getDocumentid().equals(markerList.get(j).getDocumentId())) {
                        found = true;
                        break;
                    }
                }
                if(!found) {
                    /*
                    Thread thread = new Thread(new AddMarkerRunnable(listInBounds.get(i).getDocumentid(),
                            listInBounds.get(i).getPicture(), listInBounds.get(i).getUsername(),
                            listInBounds.get(i).getLocation(), listInBounds.get(i).getFollowers(),
                            listInBounds.get(i).getVisible(), false));
                    thread.start();
                    */
                    addMarker(listInBounds.get(i).getDocumentid(),
                            listInBounds.get(i).getPicture(), listInBounds.get(i).getUsername(),
                            listInBounds.get(i).getLocation(), listInBounds.get(i).getFollowers(),
                            listInBounds.get(i).getVisible(), false);
                }
            }
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
            } else {
                visible = false;
            }
        });
    }

    //CLICK ON USERLIST BUTTON
    private void openUserList() {
        Button button = findViewById(R.id.userlist);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Switch aSwitch = findViewById(R.id.visibleSwitch);
                if(fragment.isAdded()){
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.remove(fragment);
                    fragmentTransaction.commit();
                    aSwitch.setVisibility(View.VISIBLE);
                }else{
                    callFragment();
                    aSwitch.setVisibility(View.GONE);
                }
            }
        });
    }

    //ADD FRAGMENT TO ACTIVITY
    private void callFragment() {
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("username", usernameList);
        bundle.putStringArrayList("userpicture", userpictureList);
        bundle.putStringArrayList("userfollowers", userfollowersList);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fframe,fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        //Send user params to User List Fragment
        fragment.setArguments(bundle);
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
            bundle.putString("userfullpicture", userfullpicture.get(pos));
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

    private class AddMarkerRunnable implements Runnable {

        private String id;
        private String picture;
        private String username;
        private GeoPoint location;
        private int followers;
        private boolean visible;
        private boolean movecamera;

        public AddMarkerRunnable(String id, String picture, String username, GeoPoint location, int followers, boolean visible, boolean movecamera) {
            this.picture = picture;
            this.username = username;
            this.followers = followers;
            this.location = location;
            this.visible = visible;
            this.movecamera = movecamera;
            this.id = id;
        }

        @Override
        public void run() {
            //ADD MARKER TO MAP AND SEND USER PARAMS TO FRAGMENT
            if (visible) {
                ImageURLProcessing imageURLProcessing = new ImageURLProcessing();
                imageURLProcessing.execute(picture);
                try {
                    Bitmap bitmap = imageURLProcessing.get();
                    MarkerOptions markerOptions = new MarkerOptions();
                    Bitmap roundBitMap;
                    Bitmap resizedBitMap;
                    Bitmap userListFragmentBitmap;
                    resizedBitMap = imageProcessing.getResizedBitmap(bitmap, followers); // Resize bitmap
                    roundBitMap = imageProcessing.getCroppedBitmap(resizedBitMap); // Make current bitmap to round type
                    userListFragmentBitmap = imageProcessing.getCroppedBitmap(imageProcessing.getResizedBitmapForUserListFragment(bitmap)); // Make current bitmap for userlist fragment type
                    String userPictureString = imageProcessing.bitmapToString(userListFragmentBitmap); //Convert bitmap to String to send to fragment as param
                    String fullPictureString = imageProcessing.bitmapToString(bitmap);

                    //TODO read current location data from shared prefs
                    LatLng userLongLat = new LatLng(1, 1);

                    if (location != null) {
                        userLongLat = new LatLng(location.getLatitude(), location.getLongitude());
                    }
                    markerOptions.position(userLongLat);
                    markerOptions.icon(BitmapDescriptorFactory.fromBitmap(roundBitMap));
                    String userFollowers = followersProcessing.instagramFollowersType(followers);
                    markerOptions.title(username + " : " + userFollowers + " Followers");
                    LatLng finalUserLongLat = userLongLat;
                    iHandler.post(() -> {
                        mMap.setMinZoomPreference(18f); // User cannot zoom out of 18 zoom distance
                        Marker marker = mMap.addMarker(markerOptions); // Adding marker on map.
                        Markers markers = new Markers();
                        markers.setDocumentId(id);
                        markers.setLatLng(finalUserLongLat);
                        markers.setMarkerId(marker.getId());
                        markers.setMarker(marker);
                        markerList.add(markers);
                        if (movecamera) {
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(finalUserLongLat, 19f)); // Move camera to user location in 19 zoom value
                        }
                    });
                    //Save user params for userList fragment
                    userpictureList.add(userPictureString);
                    usernameList.add(username);
                    userfollowersList.add(userFollowers);
                    userfullpicture.add(fullPictureString);

                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    private void addMarker(String id,String picture, String username, GeoPoint location, int followers, boolean visible, boolean movecamera) {
        if (visible) {
            ImageURLProcessing imageURLProcessing = new ImageURLProcessing();
            imageURLProcessing.execute(picture);
            try {
                Bitmap bitmap = imageURLProcessing.get();
                MarkerOptions markerOptions = new MarkerOptions();
                Bitmap roundBitMap;
                Bitmap resizedBitMap;
                Bitmap userListFragmentBitmap;
                resizedBitMap = imageProcessing.getResizedBitmap(bitmap, followers); // Resize bitmap
                roundBitMap = imageProcessing.getCroppedBitmap(resizedBitMap); // Make current bitmap to round type
                userListFragmentBitmap = imageProcessing.getCroppedBitmap(imageProcessing.getResizedBitmapForUserListFragment(bitmap)); // Make current bitmap for userlist fragment type
                String userPictureString = imageProcessing.bitmapToString(userListFragmentBitmap); //Convert bitmap to String to send to fragment as param
                String fullPictureString = imageProcessing.bitmapToString(bitmap);

                //TODO read current location data from shared prefs
                LatLng userLongLat = new LatLng(1, 1);

                if (location != null) {
                    userLongLat = new LatLng(location.getLatitude(), location.getLongitude());
                }
                markerOptions.position(userLongLat);
                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(roundBitMap));
                String userFollowers = followersProcessing.instagramFollowersType(followers);
                markerOptions.title(username + " : " + userFollowers + " Followers");
                LatLng finalUserLongLat = userLongLat;
                iHandler.post(() -> {
                    mMap.setMinZoomPreference(18f); // User cannot zoom out of 18 zoom distance
                    Marker marker = mMap.addMarker(markerOptions); // Adding marker on map.
                    Markers markers = new Markers();
                    markers.setDocumentId(id);
                    markers.setLatLng(finalUserLongLat);
                    markers.setMarkerId(marker.getId());
                    markers.setMarker(marker);
                    markerList.add(markers);
                    if (movecamera) {
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(finalUserLongLat, 19f)); // Move camera to user location in 19 zoom value
                    }
                });
                //Save user params for userList fragment
                userpictureList.add(userPictureString);
                usernameList.add(username);
                userfollowersList.add(userFollowers);
                userfullpicture.add(fullPictureString);

            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}
