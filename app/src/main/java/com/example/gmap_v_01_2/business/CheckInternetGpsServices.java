package com.example.gmap_v_01_2.business;

import com.example.gmap_v_01_2.repository.ProvideConnectionsStateRepo;

public class CheckInternetGpsServices implements CheckConnectionsUseCase {

    private ProvideConnectionsStateRepo checkerRepo;

    public CheckInternetGpsServices(ProvideConnectionsStateRepo checkerRepo) {
        this.checkerRepo = checkerRepo;
    }

    @Override
    public boolean check() {
        return checkGps() && checkInternet() && checkServices();
    }

    @Override
    public boolean checkInternet() {
        return checkerRepo.checkInternet();
    }

    @Override
    public boolean checkGps() {
        return checkerRepo.checkGps();
    }

    @Override
    public boolean checkServices() {
        return checkerRepo.checkServices();
    }
}
