package com.example.bandup.userprofile;

import android.content.Intent;
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

import com.example.bandup.LoginActivity;
import com.example.bandup.R;
import com.example.bandup.post.PostAdapter;
import com.example.bandup.post.PostModel;
import com.example.bandup.search.SearchFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment {

    private CircleImageView imageProfilePic;
    private TextView textProfileUserName;
    private TextView textProfileName;
    private TextView textProfileLastName;
    private TextView textProfileAge;
    private TextView textProfileGenres;
    private TextView textProfileInstruments;
    private Button buttonSignOut;
    private Button buttonEdit;
    private Button buttonFollow;
    private ImageView imageSearch;
    private FirebaseAuth mAuth;
    private String USERID;

    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<PostModel> postList;

    private List<String> followingList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mAuth = FirebaseAuth.getInstance();

        recyclerView = view.findViewById(R.id.profile_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        postList = new ArrayList<>();
        postAdapter = new PostAdapter(getContext(), postList, mAuth.getCurrentUser().getUid());
        recyclerView.setAdapter(postAdapter);

        buttonSignOut = view.findViewById(R.id.buttonSignOut);
        buttonEdit = view.findViewById(R.id.buttonEdit);
        buttonFollow = view.findViewById(R.id.buttonFollow);

        //esto se encarga de ver si es el perfil del usuario o no, y asigna los botones que corresponden
        Bundle bundle = this.getArguments();
        if(bundle != null){
            USERID = bundle.getString("UserID");
            if(USERID.equals(mAuth.getCurrentUser().getUid())){
                buttonEdit.setVisibility(View.VISIBLE);
                buttonSignOut.setVisibility(View.VISIBLE);
                buttonFollow.setVisibility(View.GONE);
            }else{
                buttonEdit.setVisibility(View.GONE);
                buttonSignOut.setVisibility(View.GONE);
                buttonFollow.setVisibility(View.VISIBLE);
                isFollowing(USERID,buttonFollow);
                buttonFollow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(buttonFollow.getText().toString().equals("Seguir")){
                            FirebaseDatabase.getInstance().getReference().child("follow")
                                    .child(mAuth.getCurrentUser().getUid()).child("following").child(USERID)
                                    .setValue(true);
                            FirebaseDatabase.getInstance().getReference().child("follow")
                                    .child(USERID).child("followers").child(mAuth.getCurrentUser().getUid())
                                    .setValue(true);
                        } else {
                            FirebaseDatabase.getInstance().getReference().child("follow")
                                    .child(mAuth.getCurrentUser().getUid()).child("following").child(USERID)
                                    .removeValue();
                            FirebaseDatabase.getInstance().getReference().child("follow")
                                    .child(USERID).child("followers").child(mAuth.getCurrentUser().getUid())
                                    .removeValue();
                        }
                    }
                });
            }
        } else {
            USERID = mAuth.getCurrentUser().getUid();
            buttonEdit.setVisibility(View.VISIBLE);
            buttonSignOut.setVisibility(View.VISIBLE);
            buttonFollow.setVisibility(View.GONE);
        }

        readPosts();

        imageProfilePic = view.findViewById(R.id.imageProfilePic);
        textProfileUserName = view.findViewById(R.id.textProfileUserName);
        textProfileName = view.findViewById(R.id.textProfileName);
        textProfileLastName = view.findViewById(R.id.textProfileLastName);
        textProfileAge = view.findViewById(R.id.textProfileAge);
        textProfileGenres = view.findViewById(R.id.textProfileGenres);
        textProfileInstruments = view.findViewById(R.id.textProfileInstruments);
        imageSearch = view.findViewById(R.id.profile_search);

        imageSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager()
                        .beginTransaction().replace(R.id.fragment_container, new SearchFragment()).commit();
            }
        });

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
                //UserModel user = new UserModel();
                //user.setUid(Objects.requireNonNull(mAuth.getCurrentUser()).getUid());
                Intent moveToEditActivity = new Intent(getActivity(), EditProfile1Activity.class);
                moveToEditActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                //moveToFillActivity.putExtra("user", user);
                //getActivity().finish();
                startActivity(moveToEditActivity);
            }
        });


        return view;
    }

    private void readPosts() {
        FirebaseDatabase.getInstance().getReference("posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (USERID.equals(snapshot.child("uid").getValue(String.class))) {
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
        String UserID = USERID;
        String username = null;
        String imageurl = null;
        String firstname = null;
        String lastname = null;
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
                age = uInfo.getAge();
                genres = uInfo.getMusicalGenres();
                instruments = uInfo.getMusicalInstruments();
            }
        }
        //Hay que ver esta condicion, el problema fue que si un dato es null tira una excepcion al querer cambiar el valor de la vista a null
        if (username != null && imageurl != null && firstname != null && lastname != null && age != null) {
            String stringAge = age + " años";
            StringBuilder stringInstruments = new StringBuilder();
            StringBuilder stringGenres = new StringBuilder();
            Picasso.get().load(imageurl).into(imageProfilePic);
            textProfileUserName.setText(username);
            textProfileName.setText(firstname);
            textProfileLastName.setText(lastname);
            textProfileAge.setText(stringAge);
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

    private void isFollowing(final String userid, final Button button){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("follow").child(mAuth.getCurrentUser().getUid()).child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(userid).exists()){
                    button.setText("Siguiendo");
                } else{
                    button.setText("Seguir");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
