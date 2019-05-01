package com.example.bandup.userprofile;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bandup.LoginActivity;
import com.example.bandup.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.Month;
import java.util.Calendar;

public class FillData1Activity extends AppCompatActivity {

    private static final int PICK_IMAGE = 100;

    private ImageView imageUserProfile;
    private ImageView imageBack1;
    private Button buttonNext;
    private EditText textUserName;
    private EditText textFirstName;
    private EditText textLastName;
    private TextView textBirth;
    private Uri imageHoldUri;
    private UserModel user;
    private FirebaseAuth mAuth;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private DatabaseReference myRef;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUserDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fill_data_1);

        //Para que es esto? este es el primer intent, el objeto no tiene nada
        //Intent intent = getIntent();
        //user = (UserModel) intent.getSerializableExtra("user");
        user = new UserModel();
        imageUserProfile = (ImageView) findViewById(R.id.imageUserProfile);
        imageBack1 = (ImageView) findViewById(R.id.imageBack1);
        buttonNext = (Button) findViewById(R.id.buttonNextFill2);
        textUserName = (EditText) findViewById(R.id.textUserName);
        textFirstName = (EditText) findViewById(R.id.textFirstName);
        textLastName = (EditText) findViewById(R.id.textLastName);
        textBirth = (TextView) findViewById(R.id.textBirth);
        imageHoldUri = null;
        mAuth = FirebaseAuth.getInstance();
        imageBack1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent back = new Intent(FillData1Activity.this, LoginActivity.class);
                finish();
                startActivity(back);
            }
        });
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextActivity();
            }
        });
        imageUserProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profilePicSelection();
            }
        });
        textBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(FillData1Activity.this,
                       android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener, year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                month = month + 1;

                user.setBirthYear(year);
                user.setBirthMonth(month);
                user.setBirthDay(dayOfMonth);

                String date = dayOfMonth + "/" + month + "/" + year;
                textBirth.setText(date);
            }
        };
/*
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        mUserDatabase = myRef.child("users").child(mAuth.getCurrentUser().getUid());
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                loadUserInfo(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
*/
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
    private void profilePicSelection() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            user.setImageUri(data.getData());
            imageHoldUri = data.getData();
            imageUserProfile.setImageURI(user.getImageUri());
        }
    }

    //metodo que recibe un año, mes y dia y devuelve una edad
    private String getAge(int year, int month, int day){
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.set(year, month, day);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)){
            age--;
        }

        Integer ageInt = new Integer(age);
        String ageS = ageInt.toString();

        //Toast.makeText(FillData1Activity.this, "Edad: " + ageS, Toast.LENGTH_SHORT).show();

        return ageS;
    }

    private void nextActivity() {
        String userName, firstName, lastName, age;
        Integer intAge;
        userName = textUserName.getText().toString().trim();
        firstName = textFirstName.getText().toString();
        lastName = textLastName.getText().toString();
        age = getAge(user.getBirthYear(), user.getBirthMonth(), user.getBirthDay());
        intAge = Integer.parseInt(age);
        if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(firstName) || TextUtils.isEmpty(lastName) || TextUtils.isEmpty(age)) {
            Toast.makeText(FillData1Activity.this, "Porfavor, complete todos los campos antes de continuar", Toast.LENGTH_LONG).show();
            return;
        }
        if (imageHoldUri == null) {
            Toast.makeText(FillData1Activity.this, "Porfavor, seleccione una foto de perfil antes de continuar", Toast.LENGTH_LONG).show();
            return;
        }
        if (!TextUtils.isEmpty(userName) && imageHoldUri != null) {
            user.setUid(mAuth.getCurrentUser().getUid());
            user.setImageUri(imageHoldUri);
            user.setUserName(userName);
            user.setFirstName(firstName);
            user.setLastName(lastName);

            user.setAge(intAge);
            Intent next = new Intent(FillData1Activity.this, FillData2Activity.class);
            next.putExtra("user", user);
            //finish();
            startActivity(next);
        }
    }
}