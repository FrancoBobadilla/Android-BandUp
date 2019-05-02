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
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bandup.LoginActivity;
import com.example.bandup.R;

import java.util.Calendar;
import java.util.Objects;

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
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fill_data_1);

        Intent intent = getIntent();
        user = (UserModel) intent.getSerializableExtra("user");
        imageUserProfile = findViewById(R.id.imageUserProfile);
        imageBack1 = findViewById(R.id.imageBack1);
        buttonNext = findViewById(R.id.buttonNextFill2);
        textUserName = findViewById(R.id.textUserName);
        textFirstName = findViewById(R.id.textFirstName);
        textLastName = findViewById(R.id.textLastName);
        textBirth = findViewById(R.id.textBirth);
        imageHoldUri = null;
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
                DatePickerDialog dialog = new DatePickerDialog(FillData1Activity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, mDateSetListener, year, month, day);
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
                String fullDate = dayOfMonth + "/" + month + "/" + year;
                textBirth.setText(fullDate);
                textBirth.setTextColor(Color.parseColor("#020100"));
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

    private void nextActivity() {
        String userName, firstName, lastName;
        userName = textUserName.getText().toString().trim();
        firstName = textFirstName.getText().toString();
        lastName = textLastName.getText().toString();
        Integer age = getAge(user.getBirthYear(), user.getBirthMonth(), user.getBirthDay());
        if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(firstName) || TextUtils.isEmpty(lastName) || age == null) {
            Toast.makeText(FillData1Activity.this, "Porfavor, complete todos los campos antes de continuar", Toast.LENGTH_LONG).show();
            return;
        }
        if (imageHoldUri == null) {
            Toast.makeText(FillData1Activity.this, "Porfavor, seleccione una foto de perfil antes de continuar", Toast.LENGTH_LONG).show();
            return;
        }
        if (!TextUtils.isEmpty(userName) && imageHoldUri != null) {
            user.setImageUri(imageHoldUri);
            user.setUserName(userName);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setAge(age);
            Intent next = new Intent(FillData1Activity.this, FillData2Activity.class);
            next.putExtra("user", user);
            //finish();
            startActivity(next);
        }
    }
}