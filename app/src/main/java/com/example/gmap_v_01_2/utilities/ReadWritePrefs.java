package com.example.gmap_v_01_2.utilities;

import com.example.gmap_v_01_2.services.preferencies.PreferencesService;

import javax.annotation.Nullable;

// This class is used to write values in string.xml
// or read from already written file
// in our case we use this to store documentID, which will be unique for every user
// so every user will have it own documentID in his/her phone
// Application is using this string to check if there is a match with this name of documentID
// and any other name of documentID from whole FireBase database
public class ReadWritePrefs {

    private PreferencesService preferencesService;

    // Get context from Activity and build MapActivity object with it's context
    // This is done to use map object as an Activity reference, because getPreferences() method
    // require a reference of the Activity it's called on
    public ReadWritePrefs(PreferencesService preferencesService) {
        //map = (MapActivity) context;
        this.preferencesService = preferencesService;
    }

    // Use this method to write String value to string.xml file (in our case documentID)
    public void writeData(String string) {
        //SharedPreferences sharedPreferences = map.getPreferences(Context.MODE_PRIVATE); // Make preference with private only this application access
        //SharedPreferences.Editor editor = sharedPreferences.edit(); // Open editor for preference
        preferencesService.put("", string);

        //editor.putString(map.getString(R.string.userDocID), string); // Write a value in our case documentID to R.string.userDocID string file from strings.xml
        //editor.apply();
    }

    // Use this method to read String value from string.xml file (in our case documentID)

    @Nullable
    public String readData() {
        //SharedPreferences sharedPreferences = map.getPreferences(Context.MODE_PRIVATE); // Make cookie with private only this application access
        try {
            return preferencesService.get("", "User Cookies are not found");
            //return sharedPreferences.getString(map.getString(R.string.userDocID), "User Cookies are not found"); // return string value from userDocID string in strings.xml, otherwise return "User Cookies are not found"
        } catch (Exception e) {
            return null;
        }

    }

}
