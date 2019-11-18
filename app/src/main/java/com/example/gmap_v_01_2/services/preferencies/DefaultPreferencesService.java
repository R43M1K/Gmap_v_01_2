package com.example.gmap_v_01_2.services.preferencies;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public final class DefaultPreferencesService implements PreferencesService {

    private static DefaultPreferencesService INSTANCE = null;

    public static DefaultPreferencesService getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (DefaultPreferencesService.class) {
                if (INSTANCE == null) {
                    INSTANCE = new DefaultPreferencesService(context);
                }
            }
        }

        return INSTANCE;
    }

    private static final String TAG = DefaultPreferencesService.class.getSimpleName();
    private SharedPreferences sharedPreferences;

    private DefaultPreferencesService(Context context) {
        sharedPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
    }

    @Override
    public <T> void put(String key, T value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (value instanceof Float) {
            editor.putFloat(key, (Float) value);
            Log.d(TAG, "put float value: " + value);
        } else if (value instanceof String) {
            editor.putString(key, (String) value);
            Log.d(TAG, "put String value: " + value);
        }

        editor.apply();
        //TODO add all other types
    }

    @Override
    public <T> T get(String key, T defaultValue) {

        if (defaultValue instanceof Float) {
            Float value = Float.valueOf(sharedPreferences.getFloat(key, (Float) defaultValue));
            Log.d(TAG, "get float value: " + value);
            return (T) value;
        } else if (defaultValue instanceof String) {
            String value = sharedPreferences.getString(key, (String) defaultValue);
            Log.d(TAG, "get string value: " + value);
            return (T) value;
        }

        return null;
    }
}
