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
    private Uri imageHoldUri;
    private UserModel user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fill_data_1);

        imageUserProfile = (ImageView) findViewById(R.id.imageUserProfile);
        buttonNext = (Button) findViewById(R.id.buttonSaveProfile);
        textUserName = (EditText) findViewById(R.id.textUserName);
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
            imageUserProfile.setImageURI(user.getImageUri());
        }
    }

    private void nextActivity() {
        String userName;

        //Se obtienen los datos de los campos ingresados
        userName = textUserName.getText().toString().trim();

        if (TextUtils.isEmpty(userName)) {
            Toast.makeText(FillData1Activity.this, "Ingrese nombre de usuario", Toast.LENGTH_LONG).show();
            return;
        }

        if (imageUserProfile == null) {
            Toast.makeText(FillData1Activity.this, "Seleccione una foto de perfil", Toast.LENGTH_LONG).show();
            return;
        }

        if (!TextUtils.isEmpty(userName) && imageHoldUri != null) {
            user.setUserName(userName);

            Intent next = new Intent(FillData1Activity.this, FillData2Activity.class);
            next.putExtra("user", user);
            startActivity(next);
        }
    }
}