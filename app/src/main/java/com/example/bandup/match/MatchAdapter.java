package com.example.bandup.match;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bandup.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

public class MatchAdapter extends RecyclerView.Adapter<MatchViewHolder> {

    private Context mContext;
    private List<MatchModel> mMatch;

    public MatchAdapter(Context mContext, List<MatchModel> mMatch) {
        this.mContext = mContext;
        this.mMatch = mMatch;
    }

    @NonNull
    @Override
    public MatchViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.match_item, viewGroup, false);
        return new MatchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MatchViewHolder viewHolder, int i) {

        final MatchModel match = mMatch.get(i);
        if (match.getDescription().equals("")) {
            viewHolder.matchTextDescription.setVisibility(View.GONE);
        } else {
            viewHolder.acceptMatchFragment.setMatchId(match.getMatchId());
            viewHolder.matchTextDescription.setVisibility(View.VISIBLE);
            viewHolder.matchTextDescription.setText(match.getDescription());
            viewHolder.matchTextTitle.setText(match.getTitle());
            String tmp = "Instrumentos: ";
            if (match.getInstruments() != null){
                for(String s : match.getInstruments()){
                    tmp = tmp + s + ", ";
                }
            }
            viewHolder.matchInstruments.setText(tmp);
            tmp = "Generos: ";
            if (match.getGenres() != null){
                for(String s : match.getGenres()){
                    tmp = tmp + s + ", ";
                }
            }
            viewHolder.matchGenres.setText(tmp);
            viewHolder.matchAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewHolder.acceptMatchFragment.show(((FragmentActivity)mContext).getSupportFragmentManager(), "aceptar match");
                }
            });
        }
        publisherInfo(viewHolder.matchUserProfileImage, viewHolder.matchTextUsername, match.getPublisher());

    }

    @Override
    public int getItemCount() {
        return mMatch.size();
    }

    private void publisherInfo(final ImageView matchUserProfileImage, final TextView matchTextUsername, String userid) {
        FirebaseDatabase.getInstance().getReference("users").child(userid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Picasso.get().load(Objects.requireNonNull(dataSnapshot.child("imageUrl").getValue()).toString()).into(matchUserProfileImage);
                matchTextUsername.setText(Objects.requireNonNull(dataSnapshot.child("firstName").getValue()).toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
