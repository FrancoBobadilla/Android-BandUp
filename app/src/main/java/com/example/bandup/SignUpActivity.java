package com.example.bandup;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.bandup.userprofile.UserProfileActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private EditText textEmail;
    private EditText textPassword;
    private Button buttonSignUp;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();

        //Referenciar los views
        textEmail = (EditText) findViewById(R.id.textEmail);
        textPassword = (EditText) findViewById(R.id.textPassword);
        buttonSignUp = (Button) findViewById(R.id.buttonSignUp);

        progressDialog = new ProgressDialog(this);

        //listener del boton
        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAcount();
            }
        });
    }

    private void createAcount(){
        //obtener email y password
        String email = textEmail.getText().toString().trim();
        String password = textPassword.getText().toString().trim();

        //verificar que no esten vacias
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "Se debe ingresar un email ", Toast.LENGTH_LONG).show();
            return;
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Se debe ingresar un password ", Toast.LENGTH_LONG).show();
            return;
        }

        progressDialog.setMessage("Signing Up");
        progressDialog.show();

        //registrar usuario en firebase
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(SignUpActivity.this, "Se ha registrado el usuario ", Toast.LENGTH_LONG).show();
                            FirebaseUser user = mAuth.getCurrentUser();

                            //updateUI(user);
                            Intent moveToProfile = new Intent(SignUpActivity.this, UserProfileActivity.class);
                            moveToProfile.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //esto evita q vuelva al login si vuelve atras
                            startActivity(moveToProfile);
                        } else {
                            //Si ya existe el usuario
                            if(task.getException() instanceof FirebaseAuthUserCollisionException){
                                Toast.makeText(SignUpActivity.this, "El usuario ya existe", Toast.LENGTH_LONG).show();
                            }else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(SignUpActivity.this, "No se pudo registrar el usuario ", Toast.LENGTH_LONG).show();
                                //updateUI(null);
                            }
                        }
                        progressDialog.dismiss();
                    }
                });
    }

}
