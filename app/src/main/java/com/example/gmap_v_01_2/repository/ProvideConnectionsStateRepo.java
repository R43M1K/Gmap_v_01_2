package com.example.gmap_v_01_2.repository;

public interface ProvideConnectionsStateRepo {

    boolean checkInternet();

    boolean checkGps();

    boolean checkServices();
}
