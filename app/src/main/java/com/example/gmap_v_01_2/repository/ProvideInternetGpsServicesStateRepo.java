package com.example.gmap_v_01_2.repository;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class ProvideInternetGpsServicesStateRepo implements ProvideConnectionsStateRepo {

    private static final int ERROR_DIALOG_REQUEST = 9001;

    private Context context;

    public ProvideInternetGpsServicesStateRepo(Context context) {
        this.context = context;
    }

    @Override
    public boolean checkInternet() {
        boolean isWiFiEnabled = false;
        boolean isMobileEnabled = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] networkInfos = new NetworkInfo[0];

        if (connectivityManager != null) networkInfos = connectivityManager.getAllNetworkInfo();

        for (NetworkInfo networkInfo : networkInfos) {
            if (networkInfo.getTypeName().equalsIgnoreCase("WIFI")) {
                isWiFiEnabled = networkInfo.isConnected();
            }
            if (networkInfo.getTypeName().equalsIgnoreCase("MOBILE")) {
                isMobileEnabled = networkInfo.isConnected();
            }
        }

        return isWiFiEnabled || isMobileEnabled;
    }

    @Override
    public boolean checkGps() {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        return locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    @Override
    public boolean checkServices() {
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context);

        if (available == ConnectionResult.SUCCESS) {
            return true;
        } else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog((Activity) context,available,ERROR_DIALOG_REQUEST);
            dialog.show();
        }
        return false;
    }
}
