package com.example.gmap_v_01_2.services.firestore;

import com.example.gmap_v_01_2.model.users.UserDocumentAll;
import com.example.gmap_v_01_2.services.firestore.model.UserDocument;
import com.google.android.gms.maps.GoogleMap;

import java.util.ArrayList;

public interface UserService {
    void addUser(UserDocument userDocument);

    void updateUser(UserDocument userDocument);

    void findUserById(final OnUserDocumentReady onUserDocumentReadyCallback);

    ArrayList<UserDocumentAll> getInBoundUsers(GoogleMap map);
}
