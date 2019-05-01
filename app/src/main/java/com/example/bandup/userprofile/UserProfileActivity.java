package com.example.bandup.userprofile;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bandup.LoginActivity;
import com.example.bandup.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

public class UserProfileActivity extends AppCompatActivity {

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
    private DatabaseReference myRef;
    private FirebaseDatabase mFirebaseDatabase;
    private StorageReference mStorageRef;
    private DatabaseReference mUserDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        mAuth = FirebaseAuth.getInstance();

        imageProfilePic = (ImageView) findViewById(R.id.imageProfilePic);
        textProfileUserName = (TextView) findViewById(R.id.textProfileUserName);
        textProfileName = (TextView) findViewById(R.id.textProfileName);
        textProfileLastName = (TextView) findViewById(R.id.textProfileLastName);
        textProfileBirthDate = (TextView) findViewById(R.id.textProfileBirthDate);
        textProfileAge = (TextView) findViewById(R.id.textProfileAge);
        textProfileGenres = (TextView) findViewById(R.id.textProfileGenres);
        textProfileInstruments = (TextView) findViewById(R.id.textProfileInstruments);
        buttonSignOut = (Button) findViewById(R.id.buttonSignOut);
        buttonEdit = (Button) findViewById(R.id.buttonEdit);

        mStorageRef = FirebaseStorage.getInstance().getReference();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        mUserDatabase = myRef.child("users").child(mAuth.getCurrentUser().getUid());

        myRef.addValueEventListener(new ValueEventListener() {
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
                Intent moveToLogin = new Intent(UserProfileActivity.this, LoginActivity.class);
                moveToLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                finish();
                startActivity(moveToLogin);
            }
        });
        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent moveToFillActivity = new Intent(UserProfileActivity.this, FillData1Activity.class);
                moveToFillActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(moveToFillActivity);
            }
        });
    }

    private void loadUserInfo(DataSnapshot dataSnapshot) {
        String UserID = mAuth.getCurrentUser().getUid();
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

        for(DataSnapshot ds : dataSnapshot.getChildren()) {
            UserModel uInfo = new UserModel();
            if(ds.child(UserID).getValue() != null){
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
        if(username != null && imageurl != null && firstname != null && lastname != null
                && birthday != null && birthmonth != null && birthyear != null && age != null){
            String stringAge = Integer.toString(age) + " años";
            String stringInstruments = "";
            String stringGenres = "";
            textProfileUserName.setText(username);
            Picasso.get().load(imageurl).into(imageProfilePic);
            textProfileName.setText(firstname);
            textProfileLastName.setText(lastname);
            textProfileAge.setText(stringAge);
            date = birthday + "/" + birthmonth + "/" + birthyear;
            textProfileBirthDate.setText(date);
            for(String genre : genres){
                if(genre.equals(genres.get(0))){
                    stringGenres = genre;
                }else{
                    stringGenres = stringGenres + ", " + genre;
                }
            }
            for(String instrument : instruments){
                if(instrument.equals(instruments.get(0))){
                    stringInstruments = instrument;
                }else{
                    stringInstruments = stringInstruments + ", " + instrument;
                }
            }
            textProfileGenres.setText(stringGenres);
            textProfileInstruments.setText(stringInstruments);
        }
    }
}
