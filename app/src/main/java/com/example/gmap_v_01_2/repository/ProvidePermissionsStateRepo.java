package com.example.gmap_v_01_2.repository;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.content.ContextCompat;

public class ProvidePermissionsStateRepo implements ProvideAllPermissionsStateRepo{

   private Context context;
   private final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
   private final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;

   public ProvidePermissionsStateRepo(Context context) {
        this.context = context;
   }

    @Override
    public boolean checkFineCoarsePermissions() {

        if (ContextCompat.checkSelfPermission(context, FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(context, COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                return false;
            }
        }else{
            return false;
        }
    }

}
