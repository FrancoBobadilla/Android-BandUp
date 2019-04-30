package com.example.bandup.userprofile;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.MediaStore;
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

import com.example.bandup.R;
import com.google.firebase.auth.FirebaseAuth;

import java.time.Month;
import java.util.Calendar;

public class FillData1Activity extends AppCompatActivity {

    private static final int PICK_IMAGE = 100;

    private ImageView imageUserProfile;
    private Button buttonNext;
    private EditText textUserName;
    private EditText textFirstName;
    private EditText textLastName;
    private TextView textBirth;
    private Uri imageHoldUri;
    private UserModel user;
    private FirebaseAuth mAuth;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fill_data_1);

        Intent intent = getIntent();
        user = (UserModel) intent.getSerializableExtra("user");
        imageUserProfile = (ImageView) findViewById(R.id.imageUserProfile);
        buttonNext = (Button) findViewById(R.id.buttonNextFill2);
        textUserName = (EditText) findViewById(R.id.textUserName);
        textFirstName = (EditText) findViewById(R.id.textFirstName);
        textLastName = (EditText) findViewById(R.id.textLastName);
        textBirth = (TextView) findViewById(R.id.textBirth);
        imageHoldUri = null;
        mAuth = FirebaseAuth.getInstance();
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
            startActivity(next);
        }
    }
}