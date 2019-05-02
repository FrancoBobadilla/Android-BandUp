package com.example.bandup;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthActionCodeException;
import com.google.firebase.auth.FirebaseAuthEmailException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseAuthWebException;
import com.google.firebase.auth.FirebaseUser;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private EditText textEmail;
    private EditText textPassword1;
    private EditText textPassword2;
    private Button buttonSignUp;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();
        textEmail = findViewById(R.id.textEmail);
        textPassword1 = findViewById(R.id.textPassword1);
        textPassword2 = findViewById(R.id.textPassword2);
        buttonSignUp = findViewById(R.id.buttonSignUp);
        progressDialog = new ProgressDialog(this);
        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAcount();
            }
        });
    }

    private void createAcount() {
        String email = textEmail.getText().toString().trim();
        String password1 = textPassword1.getText().toString().trim();
        String password2 = textPassword2.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            Toast toast = Toast.makeText(this, "Se debe ingresar un email", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 1075);
            toast.show();
            return;
        }
        if (TextUtils.isEmpty(password1)) {
            Toast toast = Toast.makeText(this, "Se debe ingresar una contraseña", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 1075);
            toast.show();
            return;
        }
        if (!password1.equals(password2)) {
            Toast toast = Toast.makeText(this, "Las contraseñas deben coincidir", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 1075);
            toast.show();
            return;
        }
        progressDialog.setMessage("Registrando");
        progressDialog.show();
        mAuth.createUserWithEmailAndPassword(email, password1).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    sendVerificationEmail();
                } else {
                    Toast toast;
                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                        toast = Toast.makeText(SignUpActivity.this, "El usuario ya existe", Toast.LENGTH_LONG);
                    } else if (task.getException() instanceof FirebaseAuthWeakPasswordException) {
                        toast = Toast.makeText(SignUpActivity.this, "Contraseña demasiado débil", Toast.LENGTH_LONG);
                    } else if (task.getException() instanceof FirebaseAuthEmailException) {
                        toast = Toast.makeText(SignUpActivity.this, "Error con el email", Toast.LENGTH_LONG);
                    } else if (task.getException() instanceof FirebaseAuthWebException) {
                        toast = Toast.makeText(SignUpActivity.this, "Error web", Toast.LENGTH_LONG);
                    } else if (task.getException() instanceof FirebaseAuthActionCodeException) {
                        toast = Toast.makeText(SignUpActivity.this, "Error con el action code", Toast.LENGTH_LONG);
                    } else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                        toast = Toast.makeText(SignUpActivity.this, "credencialesCredenciales inválidas", Toast.LENGTH_LONG);
                    } else if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                        toast = Toast.makeText(SignUpActivity.this, "Usuario inválido", Toast.LENGTH_LONG);
                    } else if (task.getException() instanceof FirebaseAuthRecentLoginRequiredException) {
                        toast = Toast.makeText(SignUpActivity.this, "Es necesario un login reciente", Toast.LENGTH_LONG);
                    } else {
                        toast = Toast.makeText(SignUpActivity.this, "Error desconocido", Toast.LENGTH_LONG);
                    }
                    toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 1075);
                    toast.show();
                }
                progressDialog.dismiss();
            }
        });
    }

    private void sendVerificationEmail() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    VerificationMailDialogFragment verificationMailDialogFragment = new VerificationMailDialogFragment();
                    verificationMailDialogFragment.show(getSupportFragmentManager(), "verification mail");
                } else {
                    Toast.makeText(SignUpActivity.this, "No se pudo enviar el mail de verificacion", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
