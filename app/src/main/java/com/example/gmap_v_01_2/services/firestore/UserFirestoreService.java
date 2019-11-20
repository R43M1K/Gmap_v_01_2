package com.example.gmap_v_01_2.services.firestore;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.ArrayAdapter;

import androidx.annotation.Nullable;

import com.example.gmap_v_01_2.editor.BoundProcessing;
import com.example.gmap_v_01_2.model.users.UserDocument;
import com.example.gmap_v_01_2.model.users.UserDocumentAll;
import com.example.gmap_v_01_2.services.preferencies.DefaultPreferencesService;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.api.LogDescriptor;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.firestore.core.Bound;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserFirestoreService implements UserService {
    private FirebaseFirestore firestore;
    private DefaultPreferencesService preferences;
    private UserDocument document;
    private BoundProcessing boundProcessing;

    private final String TAG = getClass().toString();
    private final String SHARED_DOCUMENT_ID = "DocumentId";
    private final String SHARED_USERNAME = "Username";
    private final String TABLE_NAME = "userinfo";
    private boolean foundId = false;
    private boolean foundUsername = false;
    private ArrayList<UserDocumentAll> usersInBound;

    public UserFirestoreService(Context context) {
        preferences = DefaultPreferencesService.getInstance(context);
        firestore = FirebaseFirestore.getInstance();
        document =  UserDocument.getInstance();
        boundProcessing = new BoundProcessing();
        usersInBound = new ArrayList<>();
    }

    @Override
    public void addUser() {
        Map<String, Object> user = new HashMap<>();
        user.put("username", document.getUsername());
        user.put("location", document.getLocation());
        user.put("picture", document.getPicture());
        user.put("followers", document.getFollowers());
        user.put("visible", document.getVisible());
        firestore.collection(TABLE_NAME).add(user).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                preferences.put(SHARED_DOCUMENT_ID, documentReference.getId());
                Log.d(TAG, "User added to Firebase");
            }
        });
    }

    @Override
    public void updateUser() {
        Map<String, Object> user = new HashMap<>();
        user.put("username", document.getUsername());
        user.put("location", document.getLocation());
        user.put("picture", document.getPicture());
        user.put("followers", document.getFollowers());
        user.put("visible", document.getVisible());
        firestore.collection(TABLE_NAME).document(preferences.get(SHARED_DOCUMENT_ID,"")).update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "User information updated");
            }
        });
    }

    public ArrayList<UserDocumentAll> getInBoundUsers(GoogleMap map) {
        firestore.collection(TABLE_NAME).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                usersInBound.clear();
                if(!queryDocumentSnapshots.getDocuments().isEmpty()) {
                    for (int i = 0; i < queryDocumentSnapshots.getDocuments().size(); i++) {
                        UserDocumentAll userDocumentAll = queryDocumentSnapshots.getDocuments().get(i).toObject(UserDocumentAll.class);
                        if(boundProcessing.isMarkerInsideBound(map, userDocumentAll.getLocation())) {
                            usersInBound.add(userDocumentAll);
                        }
                    }
                }
            }
        });
        return usersInBound;
    }

    public boolean findUserByUsername() {
        firestore.collection(TABLE_NAME).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(!queryDocumentSnapshots.isEmpty()) {
                    UserDocument document;
                    String username;
                    for (int i = 0; i < queryDocumentSnapshots.getDocuments().size(); i++) {
                        document = queryDocumentSnapshots.getDocuments().get(i).toObject(UserDocument.class);
                        if (document.getUsername() != null) {
                            if (document.getUsername().equals(preferences.get(SHARED_USERNAME, ""))) {

                                foundUsername = true;
                                Log.d(TAG, "Successfully added user document id");
                                break;
                            }
                        }
                    }
                }else{
                    foundUsername = false;
                }
            }
        });
        return foundUsername;
    }

    public void findUserByDocumentId() {
        firestore.collection(TABLE_NAME).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(!queryDocumentSnapshots.isEmpty()) {
                    boolean foundUser = false;
                    String s = preferences.get(SHARED_DOCUMENT_ID,"");
                    for (int i = 0; i < queryDocumentSnapshots.getDocuments().size(); i++) {
                        if (queryDocumentSnapshots.getDocuments().get(i).getId().equals(preferences.get(SHARED_DOCUMENT_ID, ""))) {
                            document = queryDocumentSnapshots.getDocuments().get(i).toObject(UserDocument.class);
                            Log.d(TAG, "Found user document id, and updated UserDocument class");
                            foundUser = true;
                            break;
                        }else if(queryDocumentSnapshots.getDocuments().get(i).toObject(UserDocument.class).getUsername().equals(preferences.get(SHARED_USERNAME,""))) {
                            document = queryDocumentSnapshots.getDocuments().get(i).toObject(UserDocument.class);
                            preferences.put(SHARED_DOCUMENT_ID, queryDocumentSnapshots.getDocuments().get(i).getId());
                            foundUser = true;
                            break;
                        }
                    }
                    if(!foundUser) {
                        addUser();
                    }
                }else{
                    addUser();
                }
            }
        });
    }
}
