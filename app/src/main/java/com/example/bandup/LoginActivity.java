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

import com.example.bandup.userprofile.FillData1Activity;
import com.example.bandup.userprofile.UserModel;
import com.example.bandup.userprofile.UserProfileActivity;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    static final int GOOGLE_SIGN = 123;

    private String authClient;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private CallbackManager callbackManager;
    private OnCompleteListener<AuthResult> authResultOnCompleteListener;
    private Button emailLoginButton;
    private Button googleLoginButton;
    private LoginButton facebookLoginButton;
    private TextView signUp;
    private TextView forgotPassword;
    private EditText textEmail;
    private EditText textPassword;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        textEmail = findViewById(R.id.textEmail);
        textPassword = findViewById(R.id.textPassword);
        facebookLoginButton = findViewById(R.id.facebookLoginButton);
        googleLoginButton = findViewById(R.id.googleLoginButton);
        emailLoginButton = findViewById(R.id.emailLoginButton);
        signUp = findViewById(R.id.buttonRegister);
        forgotPassword = findViewById(R.id.forgotPassword);
        mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();
        callbackManager = CallbackManager.Factory.create();
        progressDialog = new ProgressDialog(this);
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder().requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
        authResultOnCompleteListener = new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    if(authClient.equals("Email")){
                        if(user.isEmailVerified()){
                            Toast.makeText(LoginActivity.this, "Login exitoso con " + authClient, Toast.LENGTH_LONG).show();
                            updateUI(user);
                        } else {
                            Toast.makeText(LoginActivity.this, "Debe verificar el mail", Toast.LENGTH_LONG).show();
                            updateUI(null);
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Login exitoso con " + authClient, Toast.LENGTH_LONG).show();
                        updateUI(user);
                    }

                } else {
                    Toast.makeText(LoginActivity.this, "Hubo un error en el login con " + authClient, Toast.LENGTH_LONG).show();
                    updateUI(null);
                }
                progressDialog.dismiss();
            }
        };
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            }
        });
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RestorePasswordDialogFragment restorePasswordDialogFragment = new RestorePasswordDialogFragment();
                restorePasswordDialogFragment.setEmailAddress(textEmail.getText().toString().trim());
                restorePasswordDialogFragment.show(getSupportFragmentManager(), "recuperación de contraseña");
            }
        });
        emailLoginButton.setOnClickListener(this);
        googleLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signIntend = mGoogleSignInClient.getSignInIntent();
                LoginActivity.this.startActivityForResult(signIntend, GOOGLE_SIGN);
            }
        });
        facebookLoginButton.setReadPermissions("email", "public_profile");
        facebookLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Toast.makeText(LoginActivity.this, "Login de Facebook cancelado", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(LoginActivity.this, "Error en el login con Facebook", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void emailLogin() {
        String email = textEmail.getText().toString().trim();
        String password = textPassword.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Se debe ingresar un Email ", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Se debe ingresar una contraseña ", Toast.LENGTH_LONG).show();
            return;
        }
        progressDialog.setMessage("Ingresando con Email");
        progressDialog.show();
        authClient = "Email";
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, authResultOnCompleteListener);
    }

    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        progressDialog.setMessage("Ingresando con Facebook");
        progressDialog.show();
        authClient = "Facebook";
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, authResultOnCompleteListener);
    }

    private void handleGoogleAccount(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        progressDialog.setMessage("Ingresando con Google");
        progressDialog.show();
        authClient = "Google";
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, authResultOnCompleteListener);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GOOGLE_SIGN) {
            //google
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    handleGoogleAccount(account);
                }
            } catch (ApiException e) {
                e.printStackTrace();
            }
        } else {
            //facebook
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onClick(View v) {
        emailLogin();
    }

    public void updateUI(FirebaseUser firebaseUser) {
        if (firebaseUser != null) {
            final UserModel user = new UserModel();
            user.setUid(firebaseUser.getUid());
            FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Toast.makeText(LoginActivity.this, "Se termino de registrar", Toast.LENGTH_LONG).show();
                        //Intent fillActivity = new Intent(LoginActivity.this, UserProfileActivity.class);
                        Intent fillActivity = new Intent(LoginActivity.this, NavigationActivity.class);
                        fillActivity.putExtra("user", user);
                        finish();
                        startActivity(fillActivity);

                    } else {
                        Toast.makeText(LoginActivity.this, "No se registro", Toast.LENGTH_LONG).show();
                        Intent fillActivity = new Intent(LoginActivity.this, FillData1Activity.class);
                        fillActivity.putExtra("user", user);
                        finish();
                        startActivity(fillActivity);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    //Toast.makeText(LoginActivity.this, "Error en traer el usuario", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            //TODO: administrar error de inicio de sesión
        }
    }
}