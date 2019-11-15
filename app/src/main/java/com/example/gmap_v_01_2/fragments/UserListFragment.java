package com.example.gmap_v_01_2.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gmap_v_01_2.OnSwipeTouchListener;
import com.example.gmap_v_01_2.R;
import com.example.gmap_v_01_2.recyclerview.UserAdapter;
import com.example.gmap_v_01_2.recyclerview.User_Item;

import java.util.ArrayList;
import java.util.List;

public class UserListFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private GestureDetector gestureDetector;
    private ItemTouchHelper itemTouchHelper;
    private String action = "";

    //RecyclerView classes
    private RecyclerView mRecyclerView;
    private UserAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ArrayList<User_Item> useritemsList = new ArrayList<>();

    private OnFragmentInteractionListener mListener;
    public UserListFragment() {
        // Required empty public constructor
    }

    public static UserListFragment newInstance(String param1, String param2) {
        UserListFragment fragment = new UserListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_user_list, container, false);
        useritemsList.clear();
        //Get Parameters from Activity
        Bundle arguments = getArguments();

        if (arguments != null) {
            List<String> username = arguments.getStringArrayList("username");
            List<String> userpic = arguments.getStringArrayList("userpicture");
            List<String> userfols = arguments.getStringArrayList("userfollowers");

            if(userfols != null && userpic != null && username != null) {
                for(int i = 0; i < username.size(); i++)
                    useritemsList.add(new User_Item(userpic.get(i), username.get(i), userfols.get(i)));
            }

        }

        //Initialize RecyclerView
        mRecyclerView = view.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mAdapter = new UserAdapter(useritemsList);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new UserAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                //This is if user clicked on whole line of RecyclerViews line in position "position"
                Log.d("recycler item click: ", "item clicked by position: " + position);
            }

            @Override
            public void onPictureClick(int position) {
                //This is if user clicked on picture of user in line of RecyclerView line in position "position"
                action = "";
                mListener.onFragmentInteraction(false,true,position);
            }
        });

        mRecyclerView.setOnTouchListener(new OnSwipeTouchListener(this.getContext()) {
            @Override
            public void onSwipeRight() {
                Log.d("touch event: ", "swiped to right");
            }

            @Override
            public void onSwipeLeft() {
                Log.d("touch event: ", "swiped to left");
            }
        });

        return view;
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

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Boolean bool,Boolean openPhotoFragment, int pos);
    }

    //ADD GESTURE LISTENER
    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        final int SWIPE_MIN_DISTANCE = 120;
        final int SWIPE_MAX_OFF_PATH = 250;
        final int SWIPE_THRESHOLD_VELOCITY = 200;

        @Override
        public void onLongPress(MotionEvent e) {
            super.onLongPress(e);
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return super.onDown(e);
        }

        //ADD METHOD FOR DOUBLE TAP INDICATION
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            action = "doubleTap";
            return true;
        }

        //ADD SWIPE INDICATION
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if(Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH){
                return false;
            }
            if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY){
                //Right to Left
                action = "rightToLeft";
            }else if(e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY){
                //Left to Right
                action = "leftToRight";
            }
            return true;
        }


    }
}