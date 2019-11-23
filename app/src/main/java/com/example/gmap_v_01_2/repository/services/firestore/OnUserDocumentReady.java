package com.example.gmap_v_01_2.repository.services.firestore;

import com.example.gmap_v_01_2.repository.services.firestore.model.UserDocument;

public interface OnUserDocumentReady {
    void onReady(UserDocument userDocument);

    void onFail();

    void onFail(Throwable cause);
}
