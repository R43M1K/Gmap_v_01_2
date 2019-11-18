package com.example.gmap_v_01_2.services.firestore;

import com.example.gmap_v_01_2.model.users.UserDocument;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserFirestoreService implements UserService {
    private FirebaseFirestore firestore;

    public UserFirestoreService(FirebaseFirestore firestore) {
        this.firestore = firestore;
    }

    @Override
    public UserDocument readUser() {
        //TODO implement
        return null;
    }
}
