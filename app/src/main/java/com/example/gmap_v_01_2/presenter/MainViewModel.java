package com.example.gmap_v_01_2.presenter;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.gmap_v_01_2.business.CheckConnectionsUseCase;
import com.example.gmap_v_01_2.business.CheckPermissions;
import com.example.gmap_v_01_2.business.CheckPermissionsUseCase;


public class MainViewModel extends ViewModel {

    private CheckConnectionsUseCase checkConnectionsUseCase;
    private CheckPermissionsUseCase checkPermissionsUseCase;

    private MutableLiveData<Boolean> connectionsStateLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> connectionsStateOnFragmentInteractLiveData = new MutableLiveData<>();
    private MutableLiveData<Object> showAlertLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> permissionsStateLiveData = new MutableLiveData<>();

    public MainViewModel(CheckConnectionsUseCase checkConnectionsUseCase, CheckPermissionsUseCase checkPermissionsUseCase) {
        this.checkConnectionsUseCase = checkConnectionsUseCase;
        this.checkPermissionsUseCase = checkPermissionsUseCase;
    }

    public void checkConnections() {
        connectionsStateLiveData.setValue(checkConnectionsUseCase.check());
    }

    public void checkFragmentConnections() {
        if(checkConnectionsUseCase.check()) {
            connectionsStateOnFragmentInteractLiveData.setValue(true);
        } else if(!checkConnectionsUseCase.checkGps()){
            showAlertLiveData.setValue(new Object());
        }
    }

    public MutableLiveData<Boolean> getConnectionsStateLiveData() {
        return connectionsStateLiveData;
    }

    public MutableLiveData<Boolean> getConnectionsStateOnFragmentInteractLiveData() {
        return connectionsStateOnFragmentInteractLiveData;
    }

    public MutableLiveData<Object> getShowAlertLiveData() {
        return showAlertLiveData;
    }

    public void checkPermissions() {
        permissionsStateLiveData.setValue(checkPermissionsUseCase.checkRequestCode());
    }

    public MutableLiveData<Boolean> getPermissionsStateLiveData() {
        return permissionsStateLiveData;
    }
}
