package com.example.gmap_v_01_2.business;

import com.example.gmap_v_01_2.repository.ProvidePermissionsStateRepo;

public class CheckPermissions implements CheckPermissionsUseCase {

    ProvidePermissionsStateRepo providePermissionsStateRepo;

    public CheckPermissions(ProvidePermissionsStateRepo providePermissionsStateRepo) {
        this.providePermissionsStateRepo = providePermissionsStateRepo;
    }

    @Override
    public boolean checkRequestCode() {
        return providePermissionsStateRepo.checkFineCoarsePermissions();
    }
}
