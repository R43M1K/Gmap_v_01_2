package com.example.gmap_v_01_2.presenter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import com.example.gmap_v_01_2.R;
import com.example.gmap_v_01_2.presenter.fragments.FrontMapFragment;
import com.example.gmap_v_01_2.presenter.fragments.InstagramLoaderFragment;
import com.google.android.gms.location.FusedLocationProviderClient;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements FrontMapFragment.OnFragmentInteractionListener {

    Fragment frontMapFragment = new FrontMapFragment();
    Fragment instagramLoaderFragment = new InstagramLoaderFragment();

    //PERMISSIONS
    final int PERMISSION_REQUEST_ENABLE_GPS = 9000;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private Boolean mLocationPermissionsGranted = false;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    private ViewModelProviderFactory factory;
    private MainViewModel mainViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        factory = new ViewModelProviderFactory(getApplicationContext(), null);
        mainViewModel = ViewModelProviders.of(this, factory).get(MainViewModel.class);

        setContentView(R.layout.activity_main);
        getLocationPermission();
        checkConnections();
    }

    //CHECK GOOGLE PLAYS,GPS,INTERNET
    private void checkConnections(){
        mainViewModel.checkConnections();

        mainViewModel.getConnectionsStateLiveData().observe(this, state -> {
            if(state) {
                addInstagramLoaderFragment();
            } else {
                addFrontMapFragment();
            }
        });

        mainViewModel.getConnectionsStateOnFragmentInteractLiveData().observe(this, state -> {
            if(state) {
                replaceWithInstagramLoaderFragment();
            } else {
                addFrontMapFragment();
            }
        });

        mainViewModel.getShowAlertLiveData().observe(this, obj -> buildAlertMessagesNoGPS());
    }

    //CHECK PERMISSION FOR LOCATION GOOGLE MAP
    private void getLocationPermission(){
        String[] permissions = {FINE_LOCATION,COARSE_LOCATION};
        mainViewModel.checkPermissions();

        mainViewModel.getPermissionsStateLiveData().observe(this, aBoolean -> {
            if(aBoolean) {
                mLocationPermissionsGranted = true;
            }else{
                ActivityCompat.requestPermissions(this,permissions,LOCATION_PERMISSION_REQUEST_CODE);
            }
        });
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
                    Toast.makeText(this,"Permission granted",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void buildAlertMessagesNoGPS(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("This application requires GPS to work properly,do you want to enable it?").setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent enableGPSIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(enableGPSIntent,PERMISSION_REQUEST_ENABLE_GPS);
            }
        });
        final AlertDialog alert = builder.create();
        alert.show();
    }


    //ADD FRONT MAP FRAGMENT TO ACTIVITY (WITH REFRESH BUTTON)
    private void addFrontMapFragment(){
        FragmentNavigationController.addFragmentWithAnimation(R.id.fragframe, frontMapFragment,null,R.anim.fade_in,R.anim.fade_out, getSupportFragmentManager());
    }

    private void addInstagramLoaderFragment() {
        FragmentNavigationController.addFragment(R.id.fragframe, instagramLoaderFragment, null, getSupportFragmentManager());
    }

    private void replaceWithInstagramLoaderFragment() {
        FragmentNavigationController.replaceFragment(R.id.fragframe, instagramLoaderFragment, getSupportFragmentManager());
    }

    //THIS METHOD INDICATES THAT BUTTON OF FRAGMENT IS PRESSED
    @Override
    public void onFragmentInteraction(Boolean bool) {
        if(bool) {
            mainViewModel.checkFragmentConnections();
        }
    }

}
