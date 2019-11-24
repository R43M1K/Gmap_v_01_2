package com.example.gmap_v_01_2.presenter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.gmap_v_01_2.business.CheckConnectionsUseCase;
import com.example.gmap_v_01_2.business.CheckInternetGpsServices;
import com.example.gmap_v_01_2.business.CheckPermissions;
import com.example.gmap_v_01_2.repository.ProvideConnectionsStateRepo;
import com.example.gmap_v_01_2.repository.ProvideInternetGpsServicesStateRepo;
import com.example.gmap_v_01_2.repository.ProvidePermissionsStateRepo;

public class ViewModelProviderFactory extends ViewModelProvider.NewInstanceFactory {

    private Context context;

    public ViewModelProviderFactory(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {

        if (modelClass.isAssignableFrom(MainViewModel.class)) {

            ProvideConnectionsStateRepo provideConnectionsStateRepo = new ProvideInternetGpsServicesStateRepo(context);
            CheckConnectionsUseCase checkConnectionsUseCase = new CheckInternetGpsServices(provideConnectionsStateRepo);
            ProvidePermissionsStateRepo providePermissionsStateRepo = new ProvidePermissionsStateRepo(context);
            CheckPermissions checkPermissions = new CheckPermissions(providePermissionsStateRepo);

            return (T) new MainViewModel(checkConnectionsUseCase, checkPermissions);
        }else if(modelClass.isAssignableFrom(MapViewModel.class)) {

        }

        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}
