package com.example.gmap_v_01_2.presenter.recyclerview;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gmap_v_01_2.R;
import com.example.gmap_v_01_2.editor.FollowerProcessing;
import com.example.gmap_v_01_2.editor.ImageProcessing;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private ArrayList<User_Item> mUser_Item;
    private OnItemClickListener mListener;
    private ImageProcessing imageProcessing = new ImageProcessing(new FollowerProcessing());


    public interface OnItemClickListener {
        void onItemClick(int position);
        void onPictureClick(int position);

    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        public ImageView pictureView;
        public TextView userNameView;
        public TextView followersView;

        public UserViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            pictureView = itemView.findViewById(R.id.imageview);
            userNameView = itemView.findViewById(R.id.usernameview);
            followersView = itemView.findViewById(R.id.followerview);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                    }
                }
            });

            pictureView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onPictureClick(position);
                        }
                    }
                }
            });
        }
    }

    public UserAdapter(ArrayList<User_Item> userItems) {
        mUser_Item = userItems;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.userlist_item,parent,false);
        UserViewHolder userViewHolder = new UserViewHolder(v, mListener);
        return userViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User_Item currentItem = mUser_Item.get(position);
        Bitmap bitmap = imageProcessing.stringToBitmap(currentItem.getPicture());
        holder.pictureView.setImageBitmap(bitmap);
        holder.userNameView.setText(currentItem.getUsername());
        holder.followersView.setText(currentItem.getFollowers());
    }

    @Override
    public int getItemCount() {
        return mUser_Item.size();
    }
}
