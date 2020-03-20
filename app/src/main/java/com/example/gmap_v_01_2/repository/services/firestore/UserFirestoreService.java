package com.example.gmap_v_01_2.repository.services.firestore;

import android.content.Context;
import android.util.Log;


import com.example.gmap_v_01_2.editor.BoundProcessing;
import com.example.gmap_v_01_2.repository.services.firestore.model.UserDocument;
import com.example.gmap_v_01_2.repository.model.users.UserDocumentAll;
import com.example.gmap_v_01_2.repository.services.preferencies.DefaultPreferencesService;
import com.google.android.gms.maps.GoogleMap;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserFirestoreService implements UserService {
    private FirebaseFirestore firestore;
    private DefaultPreferencesService preferences;
    private UserDocument document;
    private BoundProcessing boundProcessing;

    private static UserFirestoreService INSTANCE = null;

    private final String TAG = getClass().toString();
    private final String SHARED_DOCUMENT_ID = "DocumentId";
    private final String SHARED_USERNAME = "Username";
    private final String TABLE_NAME = "userinfo";
    private ArrayList<UserDocumentAll> usersInBound;

    public static UserFirestoreService getInstance(Context context) {
        if(INSTANCE == null) {
            synchronized (UserFirestoreService.class) {
                if(INSTANCE == null) {
                    INSTANCE = new UserFirestoreService(context);
                }
            }
        }
        return INSTANCE;
    }

    private UserFirestoreService(Context context) {
        preferences = DefaultPreferencesService.getInstance(context);
        firestore = FirebaseFirestore.getInstance();
        document =  new UserDocument();
        boundProcessing = new BoundProcessing();
        usersInBound = new ArrayList<>();
    }

    @Override
    public void addUser(UserDocument userDocument) {
        Map<String, Object> user = new HashMap<>();
        user.put("username", userDocument.getUsername());
        user.put("location", userDocument.getLocation());
        user.put("picture", userDocument.getPicture());
        user.put("followers", userDocument.getFollowers());
        user.put("isvisible", userDocument.getVisible());
        user.put("isprivate", userDocument.getIsprivate());
        user.put("isverified", userDocument.getIsverified());
        user.put("token", userDocument.getToken());
        user.put("userid", userDocument.getUserId());
        firestore.collection(TABLE_NAME).add(user).addOnSuccessListener(documentReference -> {
            preferences.put(SHARED_DOCUMENT_ID, documentReference.getId());
            Log.d(TAG, "User added to Firebase");
        });
    }

    @Override
    public void updateUser(UserDocument userDocument) {
        Map<String, Object> user = new HashMap<>();
        user.put("username", userDocument.getUsername());
        user.put("location", userDocument.getLocation());
        user.put("picture", userDocument.getPicture());
        user.put("followers", userDocument.getFollowers());
        user.put("isvisible", userDocument.getVisible());
        user.put("isprivate", userDocument.getIsprivate());
        user.put("isverified", userDocument.getIsverified());
        user.put("token", userDocument.getToken());
        user.put("userid", userDocument.getUserId());

        String documentPath = preferences.get(SHARED_DOCUMENT_ID,"");

        documentPath = documentPath == null ? "": documentPath;

        firestore.collection(TABLE_NAME)
                .document(documentPath)
                .update(user)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "User information updated"));
    }

    @Override
    public void findUserById(final OnUserDocumentReady onUserDocumentReadyCallback){
        firestore.collection(TABLE_NAME).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    document = null;
                    if(!queryDocumentSnapshots.isEmpty()) {
                        List<DocumentSnapshot> documentSnapshots = queryDocumentSnapshots.getDocuments();

                        for (int i = 0; i < documentSnapshots.size(); i++) {
                            DocumentSnapshot documentSnapshot = documentSnapshots.get(i);

                            if (documentSnapshot.getId().equals(preferences.get(SHARED_DOCUMENT_ID, ""))) {
                                document = documentSnapshot.toObject(UserDocument.class);
                                Log.d(TAG, "Found user document id, and updated UserDocument class");
                                break;
                            } else if(documentSnapshot.toObject(UserDocument.class).getUsername().equals(preferences.get(SHARED_USERNAME,""))) {
                                document = documentSnapshot.toObject(UserDocument.class);
                                preferences.put(SHARED_DOCUMENT_ID, documentSnapshot.getId());
                                break;
                            }
                        }

                        onUserDocumentReadyCallback.onReady(document);
                    } else {
                        onUserDocumentReadyCallback.onFail();
                    }
                })
                .addOnFailureListener(onUserDocumentReadyCallback::onFail);
    }

    @Override
    public ArrayList<UserDocumentAll> getInBoundUsers(GoogleMap map) {
        firestore.collection(TABLE_NAME).get().addOnSuccessListener(queryDocumentSnapshots -> {
            usersInBound.clear();
            if(!queryDocumentSnapshots.getDocuments().isEmpty()) {
                for (int i = 0; i < queryDocumentSnapshots.getDocuments().size(); i++) {
                    UserDocumentAll userDocumentAll = queryDocumentSnapshots.getDocuments().get(i).toObject(UserDocumentAll.class);
                    userDocumentAll.setDocumentid(queryDocumentSnapshots.getDocuments().get(i).getId());
                    if(boundProcessing.isMarkerInsideBound(map, userDocumentAll.getLocation())) {
                        usersInBound.add(userDocumentAll);
                    }
                }
            }
        });
        return usersInBound;
    }

}
