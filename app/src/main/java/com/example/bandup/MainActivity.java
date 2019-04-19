package com.example.bandup;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;

    private Button buttonLogin;
    private TextView signUp;
    private EditText textEmail;
    private EditText textPassword;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        //Referencias a las views
        buttonLogin = (Button) findViewById(R.id.buttonLogin);
        textEmail = (EditText) findViewById(R.id.textEmail);
        textPassword = (EditText) findViewById(R.id.textPassword);

        //hipervinculo Texto de registrarse
        signUp = (TextView)findViewById(R.id.buttonRegister);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SignUpActivity.class));
            }
        });

        progressDialog = new ProgressDialog(this);

        //listener del boton login
        buttonLogin.setOnClickListener(this);

    }

    private void signIn(){
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

        progressDialog.setMessage("Ingresando");
        progressDialog.show();

        //Login
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(MainActivity.this, "Login Exitoso, Bienvenido", Toast.LENGTH_LONG).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(MainActivity.this, "Hubo un error en el login", Toast.LENGTH_LONG).show();
                            //updateUI(null);
                        }
                        progressDialog.dismiss();
                    }
                });
    }

    @Override
    public void onClick(View v) {
        signIn();
    }
}
