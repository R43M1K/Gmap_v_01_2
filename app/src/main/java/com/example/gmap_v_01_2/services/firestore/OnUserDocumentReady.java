package com.example.gmap_v_01_2.services.firestore;

import com.example.gmap_v_01_2.services.firestore.model.UserDocument;

public interface OnUserDocumentReady {
    void onReady(UserDocument userDocument);

    void onFail();

    void onFail(Throwable cause);
}
