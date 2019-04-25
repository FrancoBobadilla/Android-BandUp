package com.example.bandup.userprofile;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.bandup.R;

public class FillData1Activity extends AppCompatActivity {

    private static final int PICK_IMAGE = 100;

    private ImageView imageUserProfile;
    private Button buttonNext;
    private EditText textUserName;
    private EditText textFirstName;
    private EditText textLastName;
    private EditText textAge;
    private Uri imageHoldUri;
    private UserModel user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fill_data_1);

        imageUserProfile = (ImageView) findViewById(R.id.imageUserProfile);
        buttonNext = (Button) findViewById(R.id.buttonNextFill2);
        textUserName = (EditText) findViewById(R.id.textUserName);
        textFirstName = (EditText) findViewById(R.id.textFirstName);
        textLastName = (EditText) findViewById(R.id.textLastName);
        textAge = (EditText) findViewById(R.id.textAge);
        imageHoldUri = null;
        user = new UserModel();

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

    private void nextActivity() {
        String userName, firstName, lastName, age;
        Integer intAge;

        //Se obtienen los datos de los campos ingresados
        userName = textUserName.getText().toString().trim();
        firstName = textFirstName.getText().toString();
        lastName = textLastName.getText().toString();
        age = textAge.getText().toString();
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
            user.setUserName(userName);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setAge(intAge);

            Intent next = new Intent(FillData1Activity.this, FillData2Activity.class);
            next.putExtra("user", user);
            startActivity(next);
        }
    }
}