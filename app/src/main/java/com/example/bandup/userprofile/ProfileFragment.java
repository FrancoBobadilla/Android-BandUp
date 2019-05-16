package com.example.bandup.userprofile;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bandup.LoginActivity;
import com.example.bandup.NavigationActivity;
import com.example.bandup.R;
import com.example.bandup.post.PostModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class ProfileFragment extends Fragment {

    private ImageView imageProfilePic;
    private TextView textProfileUserName;
    private TextView textProfileName;
    private TextView textProfileLastName;
    private TextView textProfileBirthDate;
    private TextView textProfileAge;
    private TextView textProfileGenres;
    private TextView textProfileInstruments;
    private Button buttonSignOut;
    private Button buttonEdit;
    private FirebaseAuth mAuth;

    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<PostModel> postList;

    private List<String> followingList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        recyclerView = view.findViewById(R.id.profile_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        postList = new ArrayList<>();
        postAdapter = new PostAdapter(getContext(), postList);
        recyclerView.setAdapter(postAdapter);

        mAuth = FirebaseAuth.getInstance();
        readPosts();

        imageProfilePic = view.findViewById(R.id.imageProfilePic);
        textProfileUserName = view.findViewById(R.id.textProfileUserName);
        textProfileName = view.findViewById(R.id.textProfileName);
        textProfileLastName = view.findViewById(R.id.textProfileLastName);
        textProfileBirthDate = view.findViewById(R.id.textProfileBirthDate);
        textProfileAge = view.findViewById(R.id.textProfileAge);
        textProfileGenres = view.findViewById(R.id.textProfileGenres);
        textProfileInstruments = view.findViewById(R.id.textProfileInstruments);
        buttonSignOut = view.findViewById(R.id.buttonSignOut);
        buttonEdit = view.findViewById(R.id.buttonEdit);

        FirebaseDatabase.getInstance().getReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                loadUserInfo(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        buttonSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent moveToLogin = new Intent(getActivity(), LoginActivity.class);
                moveToLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                Objects.requireNonNull(getActivity()).finish();
                startActivity(moveToLogin);
            }
        });
        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserModel user = new UserModel();
                user.setUid(Objects.requireNonNull(mAuth.getCurrentUser()).getUid());
                Intent moveToFillActivity = new Intent(getActivity(), FillData1Activity.class);
                moveToFillActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                moveToFillActivity.putExtra("user", user);
                //getActivity().finish();
                startActivity(moveToFillActivity);
            }
        });


        return view;
    }
//      PONER EN HOME ACTIVITY
//    private void checkFollowing() {
//        followingList = new ArrayList<>();
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("follow").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("following");
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                followingList.clear();
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
//                    followingList.add(snapshot.getKey());
//                }
//                readPosts();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }
//
//    private void readPosts() {
//        FirebaseDatabase.getInstance().getReference("posts").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                postList.clear();
//                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
//                    PostModel post = snapshot.getValue(PostModel.class);
//                    for(String id : followingList){
//                        assert post != null;
//                        if(id.equals(post.getPublisher())){
//                            postList.add(post);
//                        }
//                    }
//                }
//                postAdapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }

    private void readPosts() {
        FirebaseDatabase.getInstance().getReference("posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (mAuth.getCurrentUser().getUid().equals(snapshot.child("uid").getValue(String.class))) {
                        PostModel post = new PostModel();
                        post.setPostId(snapshot.getKey());
                        post.setPublisher(snapshot.child("uid").getValue(String.class));
                        post.setTitle(snapshot.child("Title").getValue(String.class));
                        post.setDescription(snapshot.child("Description").getValue(String.class));
                        //es lo que hay, con esto no crashea despues de postear
                        if(snapshot.child("url").getValue(String.class) != null)
                            post.setPostFile(Uri.parse(snapshot.child("url").getValue(String.class)));
                        postList.add(post);
                    }
                }
                postAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadUserInfo(DataSnapshot dataSnapshot) {
        String UserID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        String username = null;
        String imageurl = null;
        String firstname = null;
        String lastname = null;
        Integer birthday = null;
        Integer birthmonth = null;
        Integer birthyear = null;
        Integer age = null;
        List<String> genres = null;
        List<String> instruments = null;
        String date;
        UserModel uInfo = new UserModel();

        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            if (ds.child(UserID).getValue() != null) {
                uInfo.setUserName(ds.child(UserID).getValue(UserModel.class).getUserName());
                uInfo.setImageUrl(ds.child(UserID).getValue(UserModel.class).getImageUrl());
                uInfo.setFirstName(ds.child(UserID).getValue(UserModel.class).getFirstName());
                uInfo.setLastName(ds.child(UserID).getValue(UserModel.class).getLastName());
                uInfo.setBirthDay(ds.child(UserID).getValue(UserModel.class).getBirthDay());
                uInfo.setBirthMonth(ds.child(UserID).getValue(UserModel.class).getBirthMonth());
                uInfo.setBirthYear(ds.child(UserID).getValue(UserModel.class).getBirthYear());
                uInfo.setAge(ds.child(UserID).getValue(UserModel.class).getAge());
                uInfo.setMusicalGenres(ds.child(UserID).getValue(UserModel.class).getMusicalGenres());
                uInfo.setMusicalInstruments(ds.child(UserID).getValue(UserModel.class).getMusicalInstruments());

                username = uInfo.getUserName();
                imageurl = uInfo.getImageUrl();
                firstname = uInfo.getFirstName();
                lastname = uInfo.getLastName();
                birthday = uInfo.getBirthDay();
                birthmonth = uInfo.getBirthMonth();
                birthyear = uInfo.getBirthYear();
                age = uInfo.getAge();
                genres = uInfo.getMusicalGenres();
                instruments = uInfo.getMusicalInstruments();
            }
        }
        //Hay que ver esta condicion, el problema fue que si un dato es null tira una excepcion al querer cambiar el valor de la vista a null
        if (username != null && imageurl != null && firstname != null && lastname != null
                && birthday != null && birthmonth != null && birthyear != null && age != null) {
            String stringAge = age + " años";
            StringBuilder stringInstruments = new StringBuilder();
            StringBuilder stringGenres = new StringBuilder();
            Picasso.get().load(imageurl).into(imageProfilePic);
            textProfileUserName.setText(username);
            textProfileName.setText(firstname);
            textProfileLastName.setText(lastname);
            textProfileAge.setText(stringAge);
            date = birthday + "/" + birthmonth + "/" + birthyear;
            textProfileBirthDate.setText(date);
            for (String genre : genres) {
                if (genre.equals(genres.get(0))) {
                    stringGenres = new StringBuilder(genre);
                } else {
                    stringGenres.append(", ").append(genre);
                }
            }
            for (String instrument : instruments) {
                if (instrument.equals(instruments.get(0))) {
                    stringInstruments = new StringBuilder(instrument);
                } else {
                    stringInstruments.append(", ").append(instrument);
                }
            }
            textProfileGenres.setText(stringGenres.toString());
            textProfileInstruments.setText(stringInstruments.toString());
        }
    }
}
