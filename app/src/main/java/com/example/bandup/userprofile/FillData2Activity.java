package com.example.bandup.userprofile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.bandup.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FillData2Activity extends AppCompatActivity {

    private ImageView imageBack2;
    private Button buttonNextFill3;
    private List<Item> items;
    private ListView listView;
    private String[] resourceList;
    private ItemsListAdapter myItemsListAdapter;
    private DatabaseReference musicalInstrumentsRef;
    private UserModel user;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fill_data_2);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Cargando instrumentos");
        progressDialog.show();
        user = (UserModel) getIntent().getSerializableExtra("user");
        imageBack2 = findViewById(R.id.imageBack2);
        listView = findViewById(R.id.listInstruments);
        buttonNextFill3 = findViewById(R.id.buttonNextFill3);
        musicalInstrumentsRef = FirebaseDatabase.getInstance().getReference().child("resources").child("musicalInstruments");
        musicalInstrumentsRef.addListenerForSingleValueEvent(new ValueEventListener() {
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
                myItemsListAdapter = new ItemsListAdapter(FillData2Activity.this, items);
                listView.setAdapter(myItemsListAdapter);
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        buttonNextFill3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] selectedItems = myItemsListAdapter.getSelectedItems();
                List<String> lista = Arrays.asList(selectedItems);
                if (selectedItems.length > 0) {
                    user.setMusicalInstruments(lista);
                    Intent next = new Intent(FillData2Activity.this, FillData3Activity.class);
                    next.putExtra("user", user);
                    startActivity(next);
                } else {
                    Toast.makeText(FillData2Activity.this, "Elija un instrumento musical", Toast.LENGTH_SHORT).show();
                }
            }
        });
        imageBack2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}