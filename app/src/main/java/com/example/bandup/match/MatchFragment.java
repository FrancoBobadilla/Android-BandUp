package com.example.bandup.match;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.bandup.R;
import com.example.bandup.home.HomeFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MatchFragment extends Fragment {

    private RecyclerView recyclerView;
    private MatchAdapter matchAdapter;
    private ImageView newMatch;
    private List<MatchModel> matchList;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_match, container, false);
        recyclerView = view.findViewById(R.id.match_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        matchList = new ArrayList<>();
        matchAdapter = new MatchAdapter(getContext(), matchList);
        recyclerView.setAdapter(matchAdapter);
        newMatch = view.findViewById(R.id.new_match);
        newMatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent moveToEditActivity = new Intent(getActivity(), NewMatchActivity.class);
                moveToEditActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(moveToEditActivity);
            }
        });
        readMatches();
        return view;
    }

    private void readMatches() {
        FirebaseDatabase.getInstance().getReference().child("matches").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                matchList.clear();
                String uid = FirebaseAuth.getInstance().getUid();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String matchUid = snapshot.child("uid").getValue(String.class);
                    assert matchUid != null;
                    if (!matchUid.equals(uid)) {
                        MatchModel match = new MatchModel();
                        match.setMatchId(snapshot.getKey());
                        match.setPublisher(matchUid);
                        match.setTitle(snapshot.child("Title").getValue(String.class));
                        match.setDescription(snapshot.child("Description").getValue(String.class));
                        List<String> instruments = (List<String>) snapshot.child("Instruments").getValue();
                        match.setInstruments(instruments);
                        List<String> genres = (List<String>) snapshot.child("Genres").getValue();
                        match.setGenres(genres);
                        matchList.add(match);
                    }
                }
                matchAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
