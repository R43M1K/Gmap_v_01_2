package com.example.gmap_v_01_2.repository.services.instagram;


import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.gmap_v_01_2.instagram.api.InstagramPlaceHolderApi;
import com.example.gmap_v_01_2.repository.model.instagram.UserInfo;
import com.example.gmap_v_01_2.repository.model.instagram.UserToken;
import com.example.gmap_v_01_2.repository.model.instagram.UserTokenLong;
import com.example.gmap_v_01_2.repository.model.instagram.personal.ObjectResponse;
import com.example.gmap_v_01_2.repository.services.firestore.OnUserDocumentReady;
import com.example.gmap_v_01_2.repository.services.firestore.UserFirestoreService;
import com.example.gmap_v_01_2.repository.services.firestore.model.UserDocument;
import com.example.gmap_v_01_2.repository.services.preferencies.DefaultPreferencesService;
import com.google.firebase.firestore.GeoPoint;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InstagramApi {

    private final String TAG = getClass().toString();
    private final String SHARED_LONGITUDE = "Longitude";
    private final String SHARED_LATITUDE = "Latitude";

    private MutableLiveData<Boolean> userInfoSuccessLiveData = new MutableLiveData<>();

    private Long userID;
    private String longToken;

    private InstagramPlaceHolderApi instagramPlaceHolderApi;
    private UserFirestoreService userFirestoreService;
    private DefaultPreferencesService defaultPreferencesService;

    public InstagramApi(InstagramPlaceHolderApi instagramPlaceHolderApi, UserFirestoreService userFirestoreService, DefaultPreferencesService defaultPreferencesService) {
        this.instagramPlaceHolderApi = instagramPlaceHolderApi;
        this.userFirestoreService = userFirestoreService;
        this.defaultPreferencesService = defaultPreferencesService;
    }

    public void getProfileInfo(String code) {
        getTokenByCode(code);
    }

    private void getTokenByCode(String code) {
        Call<UserToken> call = instagramPlaceHolderApi.getToken(460485674626498L,
                "0962b86387a8461431728427dfb3a9e6",
                "authorization_code",
                "https://github.com/R43M1K",
                code
        );
        call.enqueue(new Callback<UserToken>() {
            @Override
            public void onResponse(Call<UserToken> call, Response<UserToken> response) {
                if(!response.isSuccessful()) {
                    Log.d(TAG, response.message());
                    return;
                }

                UserToken userToken = response.body();
                String content = ""
                        + "User ID: " + userToken.getUser_id() + "\n"
                        + "Access Token: " + userToken.getAccess_token();
                userID = userToken.getUser_id();
                Log.d(TAG, content);

                getLongToken(userToken.getAccess_token(), userToken.getUser_id());
            }

            @Override
            public void onFailure(Call<UserToken> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }

    private void getLongToken(String shortToken, final Long userId) {
        Call<UserTokenLong> call = instagramPlaceHolderApi.getLongToken("ig_exchange_token",
                "0962b86387a8461431728427dfb3a9e6",
                shortToken);
        call.enqueue(new Callback<UserTokenLong>() {
            @Override
            public void onResponse(Call<UserTokenLong> call, Response<UserTokenLong> response) {
                if(!response.isSuccessful()) {
                    Log.d(TAG, response.message());
                    return;
                }

                UserTokenLong userTokenLong = response.body();
                String content = ""
                        + "Access Token: " + userTokenLong.getAccess_token() + "\n"
                        + "Token Type: " + userTokenLong.getToken_type() + "\n"
                        + "Expires In: " + userTokenLong.getExpires_in();

                longToken = userTokenLong.getAccess_token();
                Log.d(TAG, content);
                getUserInfo(userId, userTokenLong.getAccess_token());
            }

            @Override
            public void onFailure(Call<UserTokenLong> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }

    private void getUserInfo(final Long userId, String accessToken) {
        Call<UserInfo> call = instagramPlaceHolderApi.getUserInfo(userId,
                "account_type,id,media_count,username",accessToken);
        call.enqueue(new Callback<UserInfo>() {
            @Override
            public void onResponse(Call<UserInfo> call, Response<UserInfo> response) {
                if(!response.isSuccessful()) {
                    Log.d(TAG, response.message());
                    return;
                }

                UserInfo userInfo = response.body();
                String content = ""
                        + "Account Type: " + userInfo.getAccount_type() + "\n"
                        + "User Id: " + userInfo.getId() + "\n"
                        + "Media Count: " + userInfo.getMedia_count() + "\n"
                        + "Username: " + userInfo.getUsername();
                Log.d(TAG, content);
                getUserPersonal(userInfo.getUsername());
            }

            @Override
            public void onFailure(Call<UserInfo> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }

    private void getUserPersonal(String username) {
        Call<ObjectResponse> call = instagramPlaceHolderApi.getUserPersonal(username,1);
        call.enqueue(new Callback<ObjectResponse>() {
            @Override
            public void onResponse(Call<ObjectResponse> call, Response<ObjectResponse> response) {
                if(!response.isSuccessful()) {
                    Log.d(TAG, response.message());
                    return;
                }

                ObjectResponse objectResponse = response.body();

                String profilePicUrlHD = objectResponse.getGraphql().getUser().getProfilePicUrlHD();
                Long followersCount = objectResponse.getGraphql().getUser().getEdgeFollowedBy().getFollowersCount();
                Boolean isPrivate = objectResponse.getGraphql().getUser().getPrivate();
                Boolean isVerified = objectResponse.getGraphql().getUser().getVerified();

                String content = ""
                        + "Username: " + username + "\n"
                        + "User ID: " + userID + "\n"
                        + "User Long Token: " + longToken + "\n"
                        + "Profile Picture URL: " + profilePicUrlHD + "\n"
                        + "Followers Count: " + followersCount+ "\n"
                        + "Is Account Private: " + isPrivate + "\n"
                        + "Is Account Verified: " + isVerified;

                Log.d(TAG, content);

                double longitude = Double.valueOf(defaultPreferencesService.get(SHARED_LONGITUDE,""));
                double latitude = Double.valueOf(defaultPreferencesService.get(SHARED_LATITUDE,""));
                GeoPoint geoPoint = new GeoPoint(latitude, longitude);
                UserDocument userDocument = new UserDocument();
                userDocument.setLocation(geoPoint);
                userDocument.setUsername(username);
                userDocument.setFollowers(followersCount);
                userDocument.setPicture(profilePicUrlHD);
                userDocument.setIsprivate(false);
                userDocument.setIsverified(false);
                userDocument.setIsvisible(true);
                userDocument.setUserid(userID);
                userDocument.setToken(longToken);
                userFirestoreService.findUserById(new OnUserDocumentReady() {
                    @Override
                    public void onReady(UserDocument userDocumentChecked) {
                        if(userDocumentChecked == null) {
                            userFirestoreService.addUser(userDocument);
                        }else if(userDocumentChecked.getUserid().equals(userDocument.getUserid())){
                            userFirestoreService.updateUser(userDocument);
                        }
                    }

                    @Override
                    public void onFail() {
                        userFirestoreService.addUser(userDocument);
                    }

                    @Override
                    public void onFail(Throwable cause) {
                        userFirestoreService.addUser(userDocument);
                    }
                });

                defaultPreferencesService.put("user_id",userID);
                defaultPreferencesService.put("access_token", longToken);

                userInfoSuccessLiveData.setValue(true);
            }

            @Override
            public void onFailure(Call<ObjectResponse> call, Throwable t) {
                userInfoSuccessLiveData.setValue(false);
                Log.d(TAG, t.getMessage());
            }
        });
    }

    public MutableLiveData<Boolean> getUserInfoSuccessLiveData() {
        return userInfoSuccessLiveData;
    }

}
