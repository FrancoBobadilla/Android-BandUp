package com.example.bandup;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FillDataActivity extends AppCompatActivity {

    //dautenticacion
    private FirebaseAuth mAuth;
    private String uid;

    //base de datos
    private DatabaseReference myRef;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUserDatabase;
    private DatabaseReference musicalInstrumentsRef;
    private DatabaseReference musicalGenresRef;

    //Progress Dialog
    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fill_data);

        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        mUserDatabase = myRef.child("Users").child(uid);
        musicalInstrumentsRef = myRef.child("resources").child("musicalInstruments");
        musicalGenresRef = myRef.child("resources").child("musicalGenres");

        mProgress = new ProgressDialog(this);
    }
}
