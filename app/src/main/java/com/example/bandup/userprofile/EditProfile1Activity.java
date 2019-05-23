package com.example.bandup.userprofile;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bandup.LoginActivity;
import com.example.bandup.NavigationActivity;
import com.example.bandup.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class EditProfile1Activity extends AppCompatActivity {

    private static final int PICK_IMAGE = 100;

    private ImageView imageUserProfile;
    private ImageView imageBack1;
    private Button buttonNextEdit;
    private Button buttonSave;
    private EditText textUserName;
    private EditText textFirstName;
    private EditText textLastName;
    private TextView textBirth;
    private Uri imageHoldUri;
    private UserModel user;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    private StorageReference mStorageRef;
    private DatabaseReference userRef;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile1);
        mAuth = FirebaseAuth.getInstance();
        user = new UserModel();
        user.setUid(mAuth.getCurrentUser().getUid());
        progressDialog = new ProgressDialog(this);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        userRef = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid());
        imageUserProfile = findViewById(R.id.imageUserEdit);
        imageBack1 = findViewById(R.id.imgBackEdit1);
        buttonNextEdit = findViewById(R.id.buttonNextEdit1);
        buttonSave = findViewById(R.id.buttonSaveEdit1);
        textUserName = findViewById(R.id.textUsernameEdit);
        textFirstName = findViewById(R.id.textNameEdit);
        textLastName = findViewById(R.id.textLastNameEdit);
        textBirth = findViewById(R.id.textBirthEdit);
        imageHoldUri = null;
        imageUserProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profilePicSelection();
            }
        });
        imageBack1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent back = new Intent(EditProfile1Activity.this, NavigationActivity.class);
                finish();
                //startActivity(back);
            }
        });
        buttonNextEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent preferences = new Intent(EditProfile1Activity.this, EditProfile2Activity.class);
                //startActivity(preferences);
            }
        });
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChanges();
            }
        });
        textBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(EditProfile1Activity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, mDateSetListener, year, month, day);
                Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
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
                user.setAge(getAge(user.getBirthYear(), user.getBirthMonth(), user.getBirthDay()));
                String fullDate = dayOfMonth + "/" + month + "/" + year;
                textBirth.setText(fullDate);
                textBirth.setTextColor(Color.parseColor("#020100"));
            }
        };
        FirebaseDatabase.getInstance().getReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                loadUserInfo(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void loadUserInfo(DataSnapshot dataSnapshot) {
        if(TextUtils.isEmpty(textUserName.getText())){
            String UserID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
            String username = null;
            String imageurl = null;
            String firstname = null;
            String lastname = null;
            Integer birthday = null;
            Integer birthmonth = null;
            Integer birthyear = null;
            String date;
            UserModel uInfo = new UserModel();

            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                if (ds.child(UserID).getValue() != null) {
                    uInfo.setUserName(ds.child(UserID).getValue(UserModel.class).getUserName());
                    uInfo.setImageUrl(ds.child(UserID).getValue(UserModel.class).getImageUrl());
                    uInfo.setFirstName(ds.child(UserID).getValue(UserModel.class).getFirstName());
                    uInfo.setLastName(ds.child(UserID).getValue(UserModel.class).getLastName());
                    uInfo.setBirthDay(ds.child(UserID).getValue(UserModel.class).getBirthDay());
                    uInfo.setBirthMonth(ds.child(UserID).getValue(UserModel.class).getBirthMonth());
                    uInfo.setBirthYear(ds.child(UserID).getValue(UserModel.class).getBirthYear());
                    uInfo.setAge(ds.child(UserID).getValue(UserModel.class).getAge());

                    username = uInfo.getUserName();
                    imageurl = uInfo.getImageUrl();
                    firstname = uInfo.getFirstName();
                    lastname = uInfo.getLastName();
                    birthday = uInfo.getBirthDay();
                    birthmonth = uInfo.getBirthMonth();
                    birthyear = uInfo.getBirthYear();
                }
            }
            //Hay que ver esta condicion, el problema fue que si un dato es null tira una excepcion al querer cambiar el valor de la vista a null
            if (username != null && imageurl != null && firstname != null && lastname != null
                    && birthday != null && birthmonth != null && birthyear != null) {
                Picasso.get().load(imageurl).into(imageUserProfile);

                textUserName.setText(username);
                user.setUserName(username);

                textFirstName.setText(firstname);
                user.setFirstName(firstname);

                textLastName.setText(lastname);
                user.setLastName(lastname);

                date = birthday + "/" + birthmonth + "/" + birthyear;
                textBirth.setText(date);
                user.setBirthDay(birthday);
                user.setBirthMonth(birthmonth);
                user.setBirthYear(birthyear);
                user.setAge(getAge(birthyear,birthmonth,birthday));
            }
        }
    }

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

    private Integer getAge(Integer year, Integer month, Integer day) {
        if (year == null || month == null || day == null) {
            return null;
        }
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();
        dob.set(year, month, day);
        Integer age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }
        return age;
    }

    private void saveChanges() {
        progressDialog.setTitle("Guardando");
        progressDialog.setMessage("Porfavor espere mientras se guardan sus datos");
        progressDialog.show();

        if(!TextUtils.isEmpty(user.getUserName()) && !TextUtils.isEmpty(user.getFirstName()) && !TextUtils.isEmpty(user.getLastName()) &&
        !TextUtils.isEmpty(user.getAge().toString()) && !TextUtils.isEmpty(user.getBirthDay().toString()) && !TextUtils.isEmpty(user.getBirthMonth().toString())
        && !TextUtils.isEmpty(user.getBirthYear().toString()) && !TextUtils.isEmpty(user.getUid())){
            user.setUserName(textUserName.getText().toString());
            user.setFirstName(textFirstName.getText().toString());
            user.setLastName(textLastName.getText().toString());
        }

        userRef.child("userName").setValue(user.getUserName());
        userRef.child("firstName").setValue(user.getFirstName());
        userRef.child("lastName").setValue(user.getLastName());
        userRef.child("age").setValue(user.getAge());
        userRef.child("birthDay").setValue(user.getBirthDay());
        userRef.child("birthMonth").setValue(user.getBirthMonth());
        userRef.child("birthYear").setValue(user.getBirthYear());
        userRef.child("uid").setValue(user.getUid());

        if(imageHoldUri != null){
            final StorageReference mChildStorage = mStorageRef.child("User_Profile").child(Objects.requireNonNull(user.getImageUri().getLastPathSegment()));
            mChildStorage.putFile(user.getImageUri()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    mChildStorage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            user.setImageUrl(uri.toString());
                            userRef.child("imageUrl").setValue(user.getImageUrl());

                            progressDialog.dismiss();
                            Toast.makeText(EditProfile1Activity.this, "Los cambios han sido guardados", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }else{
            progressDialog.dismiss();
            Toast.makeText(EditProfile1Activity.this, "Los cambios han sido guardados", Toast.LENGTH_SHORT).show();
        }
    }
}
