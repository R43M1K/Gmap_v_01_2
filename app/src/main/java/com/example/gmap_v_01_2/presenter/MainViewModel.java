package com.example.gmap_v_01_2.presenter;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.gmap_v_01_2.business.CheckConnectionsUseCase;


public class MainViewModel extends ViewModel {

    private CheckConnectionsUseCase checkConnectionsUseCase;

    private MutableLiveData<Boolean> connectionsStateLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> connectionsStateOnFragmentInteractLiveData = new MutableLiveData<>();
    private MutableLiveData<Object> showAlertLiveData = new MutableLiveData<>();

    public MainViewModel(CheckConnectionsUseCase checkConnectionsUseCase) {
        this.checkConnectionsUseCase = checkConnectionsUseCase;
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
}
