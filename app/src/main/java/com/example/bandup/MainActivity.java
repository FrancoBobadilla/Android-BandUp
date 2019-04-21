package com.example.bandup;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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
        }, 3000);

    }

    public void firebaseTest() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            Toast.makeText(MainActivity.this, "Sí hay un usuario Firebase", Toast.LENGTH_LONG).show();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));  //debería ir a otra actividad enrealidad
        } else {
            Toast.makeText(MainActivity.this, "No hay un usuario Firebase", Toast.LENGTH_LONG).show();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }
    }
}