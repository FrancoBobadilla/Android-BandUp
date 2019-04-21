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
import android.widget.TextView;
import android.widget.Toast;

import com.example.bandup.userprofile.UserProfileActivity;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private CallbackManager callbackManager;

    //declaracion de vistas del layout
    private Button emailLoginButton;
    private TextView signUp;
    private EditText textEmail;
    private EditText textPassword;
    private LoginButton loginButton;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //firebase instances
        firebaseAuth = FirebaseAuth.getInstance();
        callbackManager = CallbackManager.Factory.create();

        //Referencias a las views
        emailLoginButton = (Button) findViewById(R.id.emailLoginButton);
        textEmail = (EditText) findViewById(R.id.textEmail);
        textPassword = (EditText) findViewById(R.id.textPassword);
        loginButton = (LoginButton) findViewById(R.id.login_button);

        //hipervinculo Texto de registrarse
        signUp = (TextView) findViewById(R.id.buttonRegister);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SignUpActivity.class));
            }
        });

        progressDialog = new ProgressDialog(this);

        //listener del boton login
        emailLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Toast.makeText(MainActivity.this, "Sign in cancelled", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(MainActivity.this, "Something bad happened", Toast.LENGTH_SHORT).show();
            }
        });
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = MainActivity.this.firebaseAuth.getCurrentUser();
                if (AccessToken.getCurrentAccessToken() != null) {
                    Toast.makeText(MainActivity.this, AccessToken.getCurrentAccessToken().getExpires().toString(), Toast.LENGTH_SHORT).show();
                }
                //NOSE SI ES LA MISMA CONDICION Q ARRIBA PERO ESTO VERIFICA SI HAY O NO UN USUARIO LOGUEADO
                if( user != null) {
                    //enviar a perfil porq esta logueado ya
                    Intent moveToProfile = new Intent(MainActivity.this, UserProfileActivity.class);
                    moveToProfile.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //esto evita q vuelva al login si vuelve atras
                    startActivity(moveToProfile);
                }
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            firebaseAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Toast.makeText(MainActivity.this, "Login Exitoso con facebook, Bienvenido", Toast.LENGTH_LONG).show();
                    FirebaseUser user = firebaseAuth.getCurrentUser();

                    //updateUI(user); lo envia a su perfil
                    Intent moveToProfile = new Intent(MainActivity.this, UserProfileActivity.class);
                    moveToProfile.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //esto evita q vuelva al login si vuelve atras
                    startActivity(moveToProfile);
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(MainActivity.this, "Hubo un error en el login", Toast.LENGTH_LONG).show();
                    //updateUI(null);
                }
                progressDialog.dismiss();
            }
        });
    }

    private void signIn() {
        //obtener email y password
        String email = textEmail.getText().toString().trim();
        String password = textPassword.getText().toString().trim();

        //verificar que no esten vacias
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Se debe ingresar un email ", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Se debe ingresar un password ", Toast.LENGTH_LONG).show();
            return;
        }

        progressDialog.setMessage("Ingresando");
        progressDialog.show();

        //Login
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(MainActivity.this, "Login Exitoso, Bienvenido", Toast.LENGTH_LONG).show();
                            FirebaseUser user = firebaseAuth.getCurrentUser();

                            //updateUI(user);   se manda al usuario a su perfil (por ahora)
                            Intent moveToProfile = new Intent(MainActivity.this, UserProfileActivity.class);
                            moveToProfile.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //esto evita q vuelva al login si vuelve atras
                            startActivity(moveToProfile);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

}
