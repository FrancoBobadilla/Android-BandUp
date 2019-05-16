package com.example.bandup.userprofile;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bandup.R;

public class PostViewHolder extends RecyclerView.ViewHolder {

    public TextView postTextUsername;
    public ImageView postUserProfileImage;
    public TextView postTextTitle;
    public ImageView postPlayButton;
    public TextView postTextDescription;

    public PostViewHolder(@NonNull View itemView) {
        super(itemView);

        postTextUsername = itemView.findViewById(R.id.postTextUsername);
        postUserProfileImage = itemView.findViewById(R.id.postUserProfileImage);
        postTextTitle = itemView.findViewById(R.id.postTextTitle);
        postPlayButton = itemView.findViewById(R.id.postPlayButton);
        postTextDescription = itemView.findViewById(R.id.postTextDescription);
    }
}
