package com.example.bandup;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.bandup.userprofile.FillData1Activity;
import com.example.bandup.userprofile.UserModel;
import com.example.bandup.userprofile.UserProfileActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                firebaseTest();
            }
        }, 2000);
    }

    private void firebaseTest() {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            final String uid = firebaseUser.getUid();
            FirebaseDatabase.getInstance().getReference().child("users").child(uid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        startActivity(UserProfileActivity.class, "Se termino de registrar");
                    } else {
                        UserModel user = new UserModel();
                        user.setUid(uid);
                        Toast.makeText(MainActivity.this, "No se termino de registrar", Toast.LENGTH_LONG).show();
                        Intent fillActivity = new Intent(MainActivity.this, FillData1Activity.class);
                        fillActivity.putExtra("user", user);
                        finish();
                        startActivity(fillActivity);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    startActivity(LoginActivity.class, "Error en traer el usuario");
                }
            });
        } else {
            startActivity(LoginActivity.class, "No hay un usuario");
        }
    }

    private void startActivity(Class<?> activityClass, String message) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
        finish();
        startActivity(new Intent(MainActivity.this, activityClass));
    }
}