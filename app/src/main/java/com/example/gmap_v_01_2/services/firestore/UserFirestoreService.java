package com.example.gmap_v_01_2.services.firestore;

import android.content.Context;
import android.util.Log;

import com.example.gmap_v_01_2.editor.BoundProcessing;
import com.example.gmap_v_01_2.model.users.UserDocument;
import com.example.gmap_v_01_2.model.users.UserDocumentAll;
import com.example.gmap_v_01_2.services.preferencies.DefaultPreferencesService;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
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
        user.put("visibility", userDocument.getVisible());
        firestore.collection(TABLE_NAME).add(user).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                preferences.put(SHARED_DOCUMENT_ID, documentReference.getId());
                Log.d(TAG, "User added to Firebase");
            }
        });
    }

    @Override
    public void updateUser(UserDocument userDocument) {
        Map<String, Object> user = new HashMap<>();
        user.put("username", userDocument.getUsername());
        user.put("location", userDocument.getLocation());
        user.put("picture", userDocument.getPicture());
        user.put("followers", userDocument.getFollowers());
        user.put("visibility", userDocument.getVisible());
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
                        userDocumentAll.setDocumentid(queryDocumentSnapshots.getDocuments().get(i).getId());
                        if(boundProcessing.isMarkerInsideBound(map, userDocumentAll.getLocation())) {
                            usersInBound.add(userDocumentAll);
                        }
                    }
                }
            }
        });
        return usersInBound;
    }

    public void findUserByDocumentId(final FirestoreReader listener) {
        firestore.collection(TABLE_NAME).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                document = null;
                if(!queryDocumentSnapshots.isEmpty()) {
                    for (int i = 0; i < queryDocumentSnapshots.getDocuments().size(); i++) {
                        if (queryDocumentSnapshots.getDocuments().get(i).getId().equals(preferences.get(SHARED_DOCUMENT_ID, ""))) {
                            document = queryDocumentSnapshots.getDocuments().get(i).toObject(UserDocument.class);
                            Log.d(TAG, "Found user document id, and updated UserDocument class");
                            break;
                        }else if(queryDocumentSnapshots.getDocuments().get(i).toObject(UserDocument.class).getUsername().equals(preferences.get(SHARED_USERNAME,""))) {
                            document = queryDocumentSnapshots.getDocuments().get(i).toObject(UserDocument.class);
                            preferences.put(SHARED_DOCUMENT_ID, queryDocumentSnapshots.getDocuments().get(i).getId());
                            break;
                        }
                    }
                    listener.getUser(document);
                }else{
                    listener.getUser(null);
                }
            }
        });
    }

}
