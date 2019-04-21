package com.example.bandup.userprofile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.bandup.LoginActivity;
import com.example.bandup.MainActivity;
import com.example.bandup.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class UserProfileActivity extends AppCompatActivity {

    //constante, request code para abrir galeria
    private static final int PICK_IMAGE = 100;

    //declaracion de auth de firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    //declaracion de referencias
    private DatabaseReference myRef;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUserDatabase; //aca los datos
    private StorageReference mStorageRef; //aca se guardan las imagenes

    //declaracion de las vistas del layout
    private ImageView imageUserProfile;
    private Button buttonSaveProfile;
    private EditText textUserName;
    private Button buttonSignOut;

    //esto guarda la imagen seleccionada
    private Uri imageHoldUri = null;

    //Progress Dialog
    private ProgressDialog mProgress;

    //listener de cambios en el lugar referenciado de la BD
    //private ValueEventListener valueListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        //instancias de firebase
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if( user != null ) {
                    //signed in
                    Toast.makeText(UserProfileActivity.this, "Signed in", Toast.LENGTH_LONG).show();
                } else {
                    //signed out
                    Toast.makeText(UserProfileActivity.this, "Signed out", Toast.LENGTH_LONG).show();
                }
            }
        };

        //inicializacion del progress dialog
        mProgress = new ProgressDialog(this);

        //Firebase database reference (esto dice adonde se esta escribiendo datos Users/ID)
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();

        mUserDatabase = myRef.child("Users").child(mAuth.getCurrentUser().getUid());

        //esto escucha cada vez q se cambia un dato en la BD, lee al principio tambien
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                loadUserInfo(dataSnapshot);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        //Inicializar views
        imageUserProfile = (ImageView) findViewById(R.id.imageUserProfile);
        buttonSaveProfile = (Button) findViewById(R.id.buttonSaveProfile);
        textUserName = (EditText) findViewById(R.id.textUserName);
        buttonSignOut = (Button) findViewById(R.id.buttonSignOut);

        //listeners
        buttonSaveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //GUARDAR CAMBIOS
                saveUserProfile();
            }
        });
        buttonSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //SIGN OUT
                mAuth.signOut();
                Intent moveToLogin = new Intent(UserProfileActivity.this, LoginActivity.class);
                moveToLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //esto evita q vuelva al login si vuelve atras
                startActivity(moveToLogin);
            }
        });

        //user imageview listener
        imageUserProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Elegir imagen
                profilePicSelection();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    /**
     * Busca la informacion del usuario logeado en la base de datos
     */
    private void loadUserInfo(DataSnapshot dataSnapshot) {
        String UserID = mAuth.getCurrentUser().getUid();
        String username = null;
        String imageurl = null;

        for(DataSnapshot ds : dataSnapshot.getChildren()) {
            UserInformation uInfo = new UserInformation();
            if(ds.child(UserID).getValue() != null){
                uInfo.setUsername(ds.child(UserID).getValue(UserInformation.class).getUsername());
                uInfo.setImageurl(ds.child(UserID).getValue(UserInformation.class).getImageurl());
                username = uInfo.getUsername();
                imageurl = uInfo.getImageurl();
            }
        }

        if(username != null && imageurl != null){
            textUserName.setText(username);
            Picasso.get().load(imageurl).into(imageUserProfile);
        }
    }

    private void profilePicSelection() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == PICK_IMAGE){
            imageHoldUri = data.getData();
            imageUserProfile.setImageURI(imageHoldUri);
        }

    }

    private void saveUserProfile() {
        final String userName;

        //Se obtienen los datos de los campos ingresados
        userName = textUserName.getText().toString().trim();

        if(!TextUtils.isEmpty(userName)){
            if(imageHoldUri != null){

                mProgress.setTitle("Saving");
                mProgress.setMessage("Please Wait...");
                mProgress.show();

                //ruta en storage donde se va a guardar la imagen
                final StorageReference mChildStorage = mStorageRef.child("User_Profile").child(imageHoldUri.getLastPathSegment());

                //put file con success listener
                mChildStorage.putFile(imageHoldUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        mChildStorage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                //get download url de la imagen con success listener
                                Uri imageUrl = uri;

                                //se modifican los valores de la base de datos, anteriormente se dio la reference
                                mUserDatabase.child("username").setValue(userName);
                                mUserDatabase.child("userid").setValue(mAuth.getCurrentUser().getUid());
                                mUserDatabase.child("imageurl").setValue(imageUrl.toString());
                            }
                        });
                        mProgress.dismiss();
                    }
                });
            }else{
                Toast.makeText(UserProfileActivity.this, "Seleccione una foto de perfil", Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(UserProfileActivity.this, "Ingrese nombre de usuario", Toast.LENGTH_LONG).show();
        }
    }
}
