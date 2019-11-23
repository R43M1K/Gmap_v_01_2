package com.example.gmap_v_01_2.repository.services.preferencies;

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
        } else if (value instanceof  Long) {
            editor.putLong(key, (Long) value);
            Log.d(TAG, "put Long value " + value);
        } else if (value instanceof Boolean) {
            editor.putBoolean(key, (Boolean) value);
            Log.d(TAG, "put double value " + value);
        } else if (value instanceof Integer) {
            editor.putInt(key, (Integer) value);
            Log.d(TAG, "put integer value " + value);
        }

        editor.apply();
    }

    @Override
    public <T> T get(String key, T defaultValue) {

        if (defaultValue instanceof Float) {
            Float value = sharedPreferences.getFloat(key, (Float) defaultValue);
            Log.d(TAG, "get float value: " + value);
            return (T) value;
        } else if (defaultValue instanceof String) {
            String value = sharedPreferences.getString(key, (String) defaultValue);
            Log.d(TAG, "get string value: " + value);
            return (T) value;
        } else if (defaultValue instanceof Long) {
            Long value = sharedPreferences.getLong(key, (Long) defaultValue);
            Log.d(TAG, "get long value: " + value);
            return (T) value;
        } else if(defaultValue instanceof Integer) {
            Integer value = sharedPreferences.getInt(key, (Integer) defaultValue);
            Log.d(TAG, "get integer value " + value);
            return (T) value;
        } else if(defaultValue instanceof Boolean) {
            Boolean value = sharedPreferences.getBoolean(key, (Boolean) defaultValue);
            Log.d(TAG, "get boolean value " + value);
            return (T) value;
        }

        return null;
    }
}
