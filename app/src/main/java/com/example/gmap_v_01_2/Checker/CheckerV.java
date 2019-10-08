package com.example.gmap_v_01_2.Checker;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class CheckerV {

    private static final int ERROR_DIALOG_REQUEST = 9001;

    //RETURN TRUE IF GPS IS ENABLED, RETURN FALSE IF GPS IS DISABLED
    public boolean checkGPS(Context context){

        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        return isGPSEnabled;

    }

    //RETURN TRUE IF WIFI OR MOBILE INTERNET ARE ENABLED, OTHERWISE RETURN FALSE
    public boolean checkINTERNET(Context context){

        boolean isWiFiEnabled = false;
        boolean isMobileEnabled = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] networkInfos = connectivityManager.getAllNetworkInfo();

        for(int i=0; i < networkInfos.length; i++){
            if(networkInfos[i].getTypeName().equalsIgnoreCase("WIFI")){
                if(networkInfos[i].isConnected()) {
                    isWiFiEnabled = true;
                }
            }
            if(networkInfos[i].getTypeName().equalsIgnoreCase("MOBILE")){
                if(networkInfos[i].isConnected()){
                    isMobileEnabled = true;
                }
            }
        }

        return isWiFiEnabled || isMobileEnabled;

    }

    //RETURN TRUE IF GOOGLE PLAY SERVICES ARE CORRECT, OTHERWISE RETURN FALSE
    public boolean checkServices(Context context) {

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context);

        if (available == ConnectionResult.SUCCESS) {
            return true;
        }else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog((Activity) context,available,ERROR_DIALOG_REQUEST);
            dialog.show();
        }
        return false;
    }

}
