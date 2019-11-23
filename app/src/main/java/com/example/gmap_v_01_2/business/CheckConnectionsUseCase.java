package com.example.gmap_v_01_2.business;

public interface CheckConnectionsUseCase {
    boolean check();

    boolean checkInternet();

    boolean checkGps();

    boolean checkServices();
}
