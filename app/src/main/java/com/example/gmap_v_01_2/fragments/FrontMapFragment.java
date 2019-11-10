package com.example.gmap_v_01_2.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import com.example.gmap_v_01_2.R;


public class FrontMapFragment extends Fragment{

    private static final String ARG_PARAM1 = "param1";

    // TODO: Rename and change types of parameters
    private String mParam1;

    private OnFragmentInteractionListener mListener;
    Button button;

    // TODO: Rename and change types and number of parameters
    public static FrontMapFragment newInstance(String param1) {
        FrontMapFragment fragment = new FrontMapFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_front_map, container, false);

        button = (Button) view.findViewById(R.id.reload);
        final Animation rotation = AnimationUtils.loadAnimation(button.getContext(),R.anim.rotate_once);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonPressed(true);
                view.findViewById(R.id.reload).startAnimation(rotation);
            }
        });
        return view;
    }

    //THIS METHOD SHOULD BE CALLED IN onClick of BUTTON IN FRAGMENT
    public void onButtonPressed(Boolean bool) {
        if (mListener != null) {
            mListener.onFragmentInteraction(bool);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    //THIS INTERFACE INDICATES IN ACTIVITY THAT BUTTON IS PRESSED
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Boolean bool);
    }
}