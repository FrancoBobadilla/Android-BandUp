package com.example.bandup.userprofile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.bandup.MainActivity;
import com.example.bandup.R;
import com.example.bandup.userprofile.Item;
import com.example.bandup.userprofile.ItemsListAdapter;
import com.example.bandup.userprofile.UserModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FillData3Activity extends AppCompatActivity {
    private Button buttonSave;
    private List<Item> items;
    private ListView listView;
    private String[] resourceList;
    private ItemsListAdapter myItemsListAdapter;
    private DatabaseReference musicalGenresRef;
    private UserModel user;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fill_data_3);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Cargando géneros musicales");
        progressDialog.show();
        Intent intent = getIntent();
        user = (UserModel) intent.getSerializableExtra("user");
        listView = (ListView) findViewById(R.id.listGenres);
        buttonSave = (Button) findViewById(R.id.buttonSave);
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
                items = new ArrayList<Item>();
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
                    user.setMusicalGenres(selectedItems);
                    Intent moveToProfile = new Intent(FillData3Activity.this, MainActivity.class);
                    moveToProfile.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(moveToProfile);
                } else {
                    Toast.makeText(FillData3Activity.this, "Elija un género musical", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}