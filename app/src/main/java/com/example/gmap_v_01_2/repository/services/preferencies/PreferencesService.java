package com.example.gmap_v_01_2.repository.services.preferencies;

public interface PreferencesService {
    <T> void put(String key, T value);

    <T> T get(String key, T defaultValue);
}
