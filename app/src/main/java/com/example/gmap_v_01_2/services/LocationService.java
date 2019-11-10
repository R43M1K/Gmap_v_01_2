package com.example.gmap_v_01_2.services;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.example.gmap_v_01_2.MapActivity;
import com.example.gmap_v_01_2.model.users.UserDocument;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.HashMap;
import java.util.Map;

public class LocationService extends Service {

    private FusedLocationProviderClient fusedLocationProviderClient;
    private final static long UPDATE_INTERVAL = 4 * 1000; // 4 seconds
    private final static long FASTEST_INTERVAL = 2 * 1000; // 2 seconds


    public LocationService() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "my channel 01";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "My Channel", NotificationManager.IMPORTANCE_DEFAULT);
            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);
            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID).setContentTitle("").setContentText("").build();
            startForeground(1, notification);
        }
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        String documentID = intent.getStringExtra("doc");
        String link = intent.getStringExtra("pic");
        String username = intent.getStringExtra("name");
        int followers = intent.getIntExtra("fol",0);
        getLocation(documentID, link, username, followers);
        return START_NOT_STICKY;
    }

    private void getLocation(final String documentID, final String link, final String username, final int followers) {
        //-------------------------------Location-Request--------------------------//
        LocationRequest mLocationRequestHighAccuracy = new LocationRequest();
        mLocationRequestHighAccuracy.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequestHighAccuracy.setInterval(UPDATE_INTERVAL);
        mLocationRequestHighAccuracy.setFastestInterval(FASTEST_INTERVAL);
        //-------------------------------------------------------------------------//
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            stopSelf();
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(mLocationRequestHighAccuracy, new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Location location = locationResult.getLastLocation();
                if (location != null) {
                    GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                    UserDocument userDocument = new UserDocument();
                    userDocument.setPicture(link);
                    userDocument.setUsername(username);
                    userDocument.setLocation(geoPoint);
                    userDocument.setFollowers(followers);
                    saveUserLocation(userDocument, documentID);
                }
            }
        }, Looper.myLooper());
    }

    private void saveUserLocation(final UserDocument userDocument, String documentid) {
        if (documentid != null) {
            try {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                Map<String, Object> user = new HashMap<>();
                user.put("username", userDocument.getUsername());
                user.put("location", userDocument.getLocation());
                user.put("picture", userDocument.getPicture());
                user.put("followers", userDocument.getFollowers());
                db.collection("userinfo").document(documentid).update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
            } catch (Exception e) {
               stopSelf();
            }
        }
    }

}
