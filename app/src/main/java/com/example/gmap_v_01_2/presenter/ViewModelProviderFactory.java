package com.example.gmap_v_01_2.presenter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.gmap_v_01_2.business.CheckConnectionsUseCase;
import com.example.gmap_v_01_2.business.CheckInternetGpsServices;
import com.example.gmap_v_01_2.business.CheckPermissions;
import com.example.gmap_v_01_2.business.markers.MarkersMain;
import com.example.gmap_v_01_2.business.markers.MarkersMainUseCase;
import com.example.gmap_v_01_2.repository.ProvideConnectionsStateRepo;
import com.example.gmap_v_01_2.repository.ProvideInternetGpsServicesStateRepo;
import com.example.gmap_v_01_2.repository.services.markers.UserMarkersService;
import com.example.gmap_v_01_2.repository.services.markers.MarkerService;
import com.example.gmap_v_01_2.repository.ProvidePermissionsStateRepo;
import com.example.gmap_v_01_2.repository.markers.repo.ProvideMarkersOp;
import com.example.gmap_v_01_2.repository.markers.repo.ProvideMarkersOperations;
import com.google.android.gms.maps.GoogleMap;

public class ViewModelProviderFactory extends ViewModelProvider.NewInstanceFactory {

    private Context context;
    private GoogleMap googleMap;

    public ViewModelProviderFactory(Context context, GoogleMap googleMap) {
        this.context = context;
        this.googleMap = googleMap;
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
        } else if(modelClass.isAssignableFrom(MapViewModel.class)) {

            ProvideMarkersOperations provideMarkersOperations = new ProvideMarkersOp(context,googleMap);
            MarkersMainUseCase markersMainUseCase = new MarkersMain(provideMarkersOperations);

            return (T) new MapViewModel(markersMainUseCase);
        }

        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}
