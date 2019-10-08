package com.example.gmap_v_01_2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import com.example.gmap_v_01_2.Checker.CheckerV;
import com.example.gmap_v_01_2.Fragments.FrontMap;


public class MainActivity extends AppCompatActivity implements FrontMap.OnFragmentInteractionListener {

    CheckerV checker = new CheckerV();
    Fragment fragment = new FrontMap();

    //PERMISSIONS
    final int PERMISSION_REQUEST_ENABLE_GPS = 9000;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private Boolean mLocationPermissionsGranted = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getLocationPermission();
        if(checkConnections()){
            Intent intent = new Intent(MainActivity.this,MapActivity.class);
            startActivity(intent);
        }else{
            callFragment();
        }

    }

    //CHECK GOOGLEPLAYS,GPS,INTERNET
    private boolean checkConnections(){
        if(!checker.checkGPS(this)){
            Toast.makeText(this,"GPS DISABLED",Toast.LENGTH_SHORT).show();
        }
        if(!checker.checkINTERNET(this)){
            Toast.makeText(this,"INTERNET DISABLED", Toast.LENGTH_SHORT).show();
        }
        if(!checker.checkServices(this)){
            Toast.makeText(this,"GOOGLEPLAY SERVICES ERROR",Toast.LENGTH_SHORT).show();
        }
        return (checker.checkINTERNET(this) && checker.checkGPS(this) && checker.checkServices(this));
    }

    //CHECK PERMISSION FOR LOCATION GOOGLE MAP
    private void getLocationPermission(){
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this,FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this,COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionsGranted = true;
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


    //ADD FRAGMENT TO ACITIVITY
    private void callFragment(){

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.fade_in,R.anim.fade_out);
        fragmentTransaction.add(R.id.fragframe, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }

    //THIS METHOD INDICATES THAT BUTTON OF FRAGMENT IS PRESSED
    @Override
    public void onFragmentInteraction(Boolean bool) {

        if((checker.checkINTERNET(this) && checker.checkGPS(this) && checker.checkServices(this))){
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.hide(fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
            Intent intent = new Intent(MainActivity.this,MapActivity.class);
            startActivity(intent);
        }else{
           if(!checker.checkGPS(this)){
               buildAlertMessagesNoGPS();
           }else if(!checker.checkINTERNET(this)){
               Toast.makeText(this,"No Internet Connection",Toast.LENGTH_SHORT).show();
           }
        }
    }


}
