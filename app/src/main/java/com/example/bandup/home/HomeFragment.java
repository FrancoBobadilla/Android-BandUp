package com.example.bandup.home;

import android.net.Uri;
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
import com.example.bandup.post.PostModel;
import com.example.bandup.search.SearchFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private HomeAdapter homeAdapter;
    private ImageView imageSearch;
    private List<PostModel> postList;
    private List<String> followingList;

    //private FirebaseAuth mAuth;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.home_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        postList = new ArrayList<>();
        homeAdapter = new HomeAdapter(getContext(), postList);
        recyclerView.setAdapter(homeAdapter);

        imageSearch = view.findViewById(R.id.home_search);
        imageSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SearchFragment()).commit();
            }
        });

        //mAuth = FirebaseAuth.getInstance();
        checkFollowing();

        return view;
    }

    private void checkFollowing() {
        followingList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("follow").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                followingList.clear();
                followingList.add(FirebaseAuth.getInstance().getCurrentUser().getUid());
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    followingList.add(snapshot.getKey());
                }
                readPosts();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readPosts() {
        FirebaseDatabase.getInstance().getReference("posts").orderByChild("timestamp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    PostModel post = new PostModel();
                    post.setPublisher(snapshot.child("uid").getValue(String.class));
                    post.setPostId(snapshot.getKey());
                    post.setTitle(snapshot.child("Title").getValue(String.class));
                    post.setDescription(snapshot.child("Description").getValue(String.class));
                    post.setTimestamp(snapshot.child("timestamp").getValue(String.class));
                    //es lo que hay, con esto no crashea despues de postear
                    if (snapshot.child("url").getValue(String.class) != null)
                        post.setPostFile(Uri.parse(snapshot.child("url").getValue(String.class)));
                    for (String id : followingList) {
                        if (post.getPublisher().equals(id)){
                            postList.add(post);
                        }
                    }
                }
                homeAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
