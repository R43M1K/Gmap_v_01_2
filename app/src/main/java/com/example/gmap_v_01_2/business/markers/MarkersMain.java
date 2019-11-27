package com.example.gmap_v_01_2.business.markers;

import com.example.gmap_v_01_2.repository.markers.repo.ProvideMarkersOp;
import com.example.gmap_v_01_2.repository.markers.repo.ProvideMarkersOperations;
import com.example.gmap_v_01_2.repository.model.users.UserDocumentAll;

import java.util.ArrayList;
import java.util.HashMap;

public class MarkersMain implements MarkersMainUseCase {

    ProvideMarkersOperations provideMarkersOp;

    public MarkersMain(ProvideMarkersOperations provideMarkersOp) {
        this.provideMarkersOp = provideMarkersOp;
    }

    @Override
    public void checkMarkers() {
        provideMarkersOp.checkMarkers();
    }

    @Override
    public ArrayList<HashMap> addMarkers() {
        return provideMarkersOp.addMarkers();
    }

    @Override
    public ArrayList<Integer> removeMarkers() {
        return provideMarkersOp.removeMarkers();
    }
}
