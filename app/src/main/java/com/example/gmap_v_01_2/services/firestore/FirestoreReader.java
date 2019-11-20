package com.example.gmap_v_01_2.services.firestore;

import com.example.gmap_v_01_2.model.users.UserDocument;

public interface FirestoreReader {
    void getUser(UserDocument userDocument);
}
