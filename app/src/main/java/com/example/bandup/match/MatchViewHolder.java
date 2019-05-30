package com.example.bandup.match;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bandup.R;

public class MatchViewHolder extends RecyclerView.ViewHolder {

    public TextView matchTextUsername;
    public ImageView matchUserProfileImage;
    public TextView matchTextTitle;
    public TextView matchTextDescription;
    public TextView matchInstruments;
    public TextView matchGenres;
    public ImageView matchAccept;
    public AcceptMatchFragment acceptMatchFragment;

    public MatchViewHolder(@NonNull View itemView) {
        super(itemView);
        matchTextUsername = itemView.findViewById(R.id.matchTextUsername);
        matchUserProfileImage = itemView.findViewById(R.id.matchUserProfileImage);
        matchTextTitle = itemView.findViewById(R.id.matchTextTitle);
        matchTextDescription = itemView.findViewById(R.id.matchTextDescription);
        matchInstruments = itemView.findViewById(R.id.matchInstruments);
        matchGenres = itemView.findViewById(R.id.matchGenres);
        matchAccept = itemView.findViewById(R.id.matchAccept);

        acceptMatchFragment = new AcceptMatchFragment();
    }
}
