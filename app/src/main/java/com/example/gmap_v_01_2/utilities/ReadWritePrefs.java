package com.example.gmap_v_01_2.utilities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.example.gmap_v_01_2.MapActivity;
import com.example.gmap_v_01_2.R;

public class ReadWritePrefs {

    private Context context;

    public ReadWritePrefs(Context context) {
        this.context = context;
    }

    public void saveData(String string) {
        MapActivity map = (MapActivity) context;
        SharedPreferences sharedPreferences = map.getPreferences(Context.MODE_PRIVATE); // Make cookie with private only this application access
        SharedPreferences.Editor editor = sharedPreferences.edit(); // Open editor for cookie
        editor.putString(map.getString(R.string.userDocID), string); // Write "string" to R.string.userDocID string file from strings.xml
        editor.apply();
    }

    public String readData() {
        MapActivity map = (MapActivity) context;
        SharedPreferences sharedPreferences = map.getPreferences(Context.MODE_PRIVATE); // Make cookie with private only this application access
        try {
            return sharedPreferences.getString(map.getString(R.string.userDocID), "User Cookies are not found"); // return string value from userDocID string in strings.xml, otherwise return "User Cookies are not found"
        } catch (Exception e) {
            return null;
        }

    }

}
