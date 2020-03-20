package com.example.gmap_v_01_2.presenter.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.gmap_v_01_2.R;
import com.example.gmap_v_01_2.instagram.api.InstagramPlaceHolderApi;
import com.example.gmap_v_01_2.repository.services.firestore.UserFirestoreService;
import com.example.gmap_v_01_2.repository.services.instagram.InstagramApi;
import com.example.gmap_v_01_2.repository.services.preferencies.DefaultPreferencesService;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class InstagramLoaderFragment extends Fragment {

    private final String TAG = getClass().toString();

    private final String authorizeUrl = "https://api.instagram.com/oauth/authorize?app_id=460485674626498&redirect_uri=https://github.com/R43M1K&scope=user_profile,user_media&response_type=code";
    private final String redirectFullBeforeCode = "https://github.com/R43M1K?code=";
    private final String redirectBeforeCode = "R43M1K?code=";
    private final String redirectAfterCode = "#_";

    private String code;

    private WebView webView;

    private InstagramPlaceHolderApi instagramPlaceHolderApi;
    private InstagramApi instagramApi;
    private UserFirestoreService firestoreService;
    private DefaultPreferencesService defaultPreferencesService;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.instagram_loader, container, false);

        webView = view.findViewById(R.id.web_view);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.instagram.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        instagramPlaceHolderApi = retrofit.create(InstagramPlaceHolderApi.class);
        firestoreService = UserFirestoreService.getInstance(getContext());
        defaultPreferencesService = DefaultPreferencesService.getInstance(getContext());
        instagramApi = new InstagramApi(instagramPlaceHolderApi, firestoreService, defaultPreferencesService);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if(url.contains(redirectFullBeforeCode)) {
                    Log.d(TAG, url);
                    int index = url.lastIndexOf(redirectBeforeCode) + redirectBeforeCode.length();
                    int hashTagIndex = url.lastIndexOf(redirectAfterCode);
                    code = url.substring(index, hashTagIndex);
                    Log.d(TAG, code);
                    instagramApi.getProfileInfo(code);
                }
                return false; //Allow WebView to load url
            }
        });

        webView.loadUrl(authorizeUrl);

        return view;
    }
}
