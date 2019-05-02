package com.example.bandup.userprofile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.bandup.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class FillData3Activity extends AppCompatActivity {

    private ImageView imageBack3;
    private Button buttonSave;
    private List<Item> items;
    private ListView listView;
    private String[] resourceList;
    private ItemsListAdapter myItemsListAdapter;
    private DatabaseReference musicalGenresRef;
    private DatabaseReference userRef;
    private StorageReference mStorageRef;
    private UserModel user;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fill_data_3);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Cargando géneros musicales");
        progressDialog.show();
        user = (UserModel) getIntent().getSerializableExtra("user");
        imageBack3 = findViewById(R.id.imageBack3);
        listView = findViewById(R.id.listGenres);
        buttonSave = findViewById(R.id.buttonSave);
        userRef = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
        musicalGenresRef = FirebaseDatabase.getInstance().getReference().child("resources").child("musicalGenres");
        musicalGenresRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long childrenCount = dataSnapshot.getChildrenCount();
                resourceList = new String[(int) childrenCount];
                int i = 0;
                for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                    resourceList[i] = childDataSnapshot.getKey();
                    i++;
                }
                items = new ArrayList<>();
                for (int j = 0; j < childrenCount; j++) {
                    Item item = new Item();
                    item.setItemString(resourceList[j]);
                    item.setChecked(false);
                    items.add(item);
                }
                myItemsListAdapter = new ItemsListAdapter(FillData3Activity.this, items);
                listView.setAdapter(myItemsListAdapter);
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] selectedItems = myItemsListAdapter.getSelectedItems();
                if (selectedItems.length > 0) {
                    saveUserProfile();
                } else {
                    Toast.makeText(FillData3Activity.this, "Elija un género musical", Toast.LENGTH_SHORT).show();
                }
            }
        });
        mStorageRef = FirebaseStorage.getInstance().getReference();
        imageBack3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void saveUserProfile() {
        String[] selectedItems = myItemsListAdapter.getSelectedItems();
        List<String> listGenres = Arrays.asList(selectedItems);
        user.setMusicalGenres(listGenres);

        progressDialog.setTitle("Guardando");
        progressDialog.setMessage("Porfavor espere mientras se guardan sus datos");
        progressDialog.show();

        final StorageReference mChildStorage = mStorageRef.child("User_Profile").child(Objects.requireNonNull(user.getImageUri().getLastPathSegment()));
        mChildStorage.putFile(user.getImageUri()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                mChildStorage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        user.setImageUrl(uri.toString());

                        userRef.child("userName").setValue(user.getUserName());
                        userRef.child("imageUrl").setValue(user.getImageUrl());
                        userRef.child("firstName").setValue(user.getFirstName());
                        userRef.child("lastName").setValue(user.getLastName());
                        userRef.child("age").setValue(user.getAge());
                        userRef.child("birthDay").setValue(user.getBirthDay());
                        userRef.child("birthMonth").setValue(user.getBirthMonth());
                        userRef.child("birthYear").setValue(user.getBirthYear());
                        userRef.child("uid").setValue(user.getUid());
                        userRef.child("musicalGenres").setValue(user.getMusicalGenres());
                        userRef.child("musicalInstruments").setValue(user.getMusicalInstruments());

                        progressDialog.dismiss();

                        moveToProfile();
                    }
                });
            }
        });
    }

    private void moveToProfile() {
        Intent moveToProfile = new Intent(FillData3Activity.this, UserProfileActivity.class);
        moveToProfile.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(moveToProfile);
    }
}