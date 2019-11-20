package com.example.gmap_v_01_2.services.firestore;

import com.example.gmap_v_01_2.model.users.UserDocument;

public interface UserService {
    void updateUser(UserDocument userDocument);
    void addUser(UserDocument userDocument);
}
