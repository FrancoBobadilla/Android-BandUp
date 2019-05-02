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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        mAuth = FirebaseAuth.getInstance();

        imageProfilePic = findViewById(R.id.imageProfilePic);
        textProfileUserName = findViewById(R.id.textProfileUserName);
        textProfileName = findViewById(R.id.textProfileName);
        textProfileLastName = findViewById(R.id.textProfileLastName);
        textProfileBirthDate = findViewById(R.id.textProfileBirthDate);
        textProfileAge = findViewById(R.id.textProfileAge);
        textProfileGenres = findViewById(R.id.textProfileGenres);
        textProfileInstruments = findViewById(R.id.textProfileInstruments);
        buttonSignOut = findViewById(R.id.buttonSignOut);
        buttonEdit = findViewById(R.id.buttonEdit);

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
                Intent moveToLogin = new Intent(UserProfileActivity.this, LoginActivity.class);
                moveToLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                finish();
                startActivity(moveToLogin);
            }
        });
        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserModel user = new UserModel();
                user.setUid(Objects.requireNonNull(mAuth.getCurrentUser()).getUid());
                Intent moveToFillActivity = new Intent(UserProfileActivity.this, FillData1Activity.class);
                moveToFillActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                moveToFillActivity.putExtra("user", user);
                finish();
                startActivity(moveToFillActivity);
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



/*
    //CARGA LA INFORMACION DEL USUARIO
    //PODRIA HACERSE DE OTRA FORMA?
    //podria pasarse un objeto con informacion desde la actividad de profile
    //de todas formas esto no va a cambiar nada para usuarios que se estan registrando
    //permite editar el perfil
    private void loadUserInfo(DataSnapshot dataSnapshot) {
        String UserID = mAuth.getCurrentUser().getUid();
        String username = null;
        String firstname = null;
        String lastname = null;
        Integer birthday = null;
        Integer birthmonth = null;
        Integer birthyear = null;
        String imageurl = null;
        for(DataSnapshot ds : dataSnapshot.getChildren()){
            UserModel uInfo = new UserModel();
            if(ds.child(UserID).getValue() != null){
                uInfo.setUserName(ds.child(UserID).getValue(UserModel.class).getUserName());
                uInfo.setImageUrl(ds.child(UserID).getValue(UserModel.class).getImageUrl());
                uInfo.setFirstName(ds.child(UserID).getValue(UserModel.class).getFirstName());
                uInfo.setLastName(ds.child(UserID).getValue(UserModel.class).getLastName());
                uInfo.setBirthDay(ds.child(UserID).getValue(UserModel.class).getBirthDay());
                uInfo.setBirthMonth(ds.child(UserID).getValue(UserModel.class).getBirthMonth());
                uInfo.setBirthYear(ds.child(UserID).getValue(UserModel.class).getBirthYear());

                username = uInfo.getUserName();
                imageurl = uInfo.getImageUrl();
                firstname = uInfo.getFirstName();
                lastname = uInfo.getLastName();
                birthday = uInfo.getBirthDay();
                birthmonth = uInfo.getBirthMonth();
                birthyear = uInfo.getBirthYear();
            }
        }
        if(username != null && imageurl != null && firstname != null && lastname != null
                && birthday != null && birthmonth != null && birthyear != null){
            Picasso.get().load(imageurl).into(imageUserProfile);
            textUserName.setText(username);
            textFirstName.setText(firstname);
            textLastName.setText(lastname);
            String date = birthday + "/" + birthmonth + "/" + birthyear;
            textBirth.setText(date);

            user.setBirthDay(birthday);
            user.setBirthMonth(birthmonth);
            user.setBirthYear(birthyear);
            try{
                URL url = new URL(imageurl);
                imageHoldUri = Uri.parse(url.toURI().toString());
            } catch (MalformedURLException e1) {
                e1.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }
*/


}
