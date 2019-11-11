package com.example.gmap_v_01_2.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.gmap_v_01_2.R;
import com.example.gmap_v_01_2.editor.FollowerProcessing;
import com.example.gmap_v_01_2.editor.ImageProcessing;


public class UserPhotoViewerFragment extends Fragment {

    private OnPhotoFragmentInteractionListener mListener;

    String userfullpicture;
    Bitmap bitmap;

    public UserPhotoViewerFragment() {
        //Require Empty Constructor
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnPhotoFragmentInteractionListener) {
            mListener = (OnPhotoFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_fullscrean_photo,container,false);
        userfullpicture = getArguments().getString("userfullpicture");
        ImageView imageView = view.findViewById(R.id.fullphoto);
        ImageProcessing imageProcessing = new ImageProcessing(new FollowerProcessing());
        bitmap = imageProcessing.stringToBitmap(userfullpicture);
        imageView.setImageBitmap(bitmap);
        clickedOnPhoto(imageView);
        return view;
    }

    private void clickedOnPhoto(ImageView imgview) {
        imgview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener != null){
                    mListener.onPhotoFragmentInteraction(true);
                }
            }
        });
    }

    public interface OnPhotoFragmentInteractionListener {
        void onPhotoFragmentInteraction(Boolean bool);
    }
}
