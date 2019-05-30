package com.example.bandup.match;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bandup.R;
import com.example.bandup.userprofile.FillData2Activity;
import com.example.bandup.userprofile.FillData3Activity;
import com.example.bandup.userprofile.Item;
import com.example.bandup.userprofile.ItemsListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NewMatchActivity extends AppCompatActivity {
    ImageView matchClose;
    private EditText matchTitle;
    private EditText matchDescription;
    //private EditText matchInstruments;
    //private EditText matchGenres;
    private ProgressDialog progressDialog;
    private TextView postMatch;
    String title;
    String description;
    String instruments;
    String genres;
    private String uid;
    private MatchModel match;
    private ListView listInstruments;
    private ListView listGenres;
    private List<Item> listItemsInstruments;
    private List<Item> listItemsGenres;
    private List<String> matchInstruments;
    private List<String> matchGenres;
    private DatabaseReference musicalInstrumentsRef;
    private DatabaseReference musicalGenresRef;
    private ItemsListAdapter adapterInstruments;
    private ItemsListAdapter adapterGenres;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_match_activity);

        listInstruments = findViewById(R.id.match_list_instruments);
        listGenres = findViewById(R.id.match_list_genres);
        matchClose = findViewById(R.id.match_close);
        matchTitle = findViewById(R.id.match_text_title);
        postMatch = findViewById(R.id.match_bar_text);
        matchDescription = findViewById(R.id.match_text_description);
        //matchInstruments = findViewById(R.id.match_text_instruments);
        //matchGenres = findViewById(R.id.match_text_genres);
        progressDialog = new ProgressDialog(this);
        uid = FirebaseAuth.getInstance().getUid();
        match = new MatchModel();
        matchClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        postMatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title = matchTitle.getText().toString();
                description = matchDescription.getText().toString();
                //genres = matchGenres.getText().toString();
                //instruments = matchInstruments.getText().toString();
                String[] selectedItems = adapterGenres.getSelectedItems();
                matchGenres = Arrays.asList(selectedItems);
                selectedItems = adapterInstruments.getSelectedItems();
                matchInstruments = Arrays.asList(selectedItems);
                if (TextUtils.isEmpty(title) || TextUtils.isEmpty(description)) {
                    Toast.makeText(NewMatchActivity.this, "Porfavor, complete todos los campos antes de continuar", Toast.LENGTH_LONG).show();
                    return;
                }
                match.setDescription(description);
                match.setTitle(title);
                match.setInstruments(matchInstruments);
                match.setGenres(matchGenres);
                match.setPublisher(uid);
                match.setMatchId(uid + title.trim());
                progressDialog.setTitle("Guardando");
                progressDialog.setMessage("Porfavor espere mientras se guarda su publicación");
                progressDialog.show();
                final DatabaseReference matchRef = FirebaseDatabase.getInstance().getReference().child("matches").child(match.getMatchId());
                matchRef.child("Title").setValue(match.getTitle());
                matchRef.child("Description").setValue(match.getDescription());
                matchRef.child("uid").setValue(match.getPublisher());
                matchRef.child("Instruments").setValue(match.getInstruments());
                matchRef.child("Genres").setValue(match.getGenres());
                progressDialog.dismiss();
                finish();
            }
        });

        /*



         */
        musicalGenresRef = FirebaseDatabase.getInstance().getReference().child("resources").child("musicalGenres");
        musicalGenresRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long childrenCount = dataSnapshot.getChildrenCount();
                String[] resourceList = new String[(int) childrenCount];
                int i = 0;
                for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                    resourceList[i] = childDataSnapshot.getKey();
                    i++;
                }
                listItemsGenres = new ArrayList<>();
                for (int j = 0; j < childrenCount; j++) {
                    Item item = new Item();
                    item.setItemString(resourceList[j]);
                    item.setChecked(false);
                    listItemsGenres.add(item);
                }
                adapterGenres = new ItemsListAdapter(NewMatchActivity.this, listItemsGenres);
                listGenres.setAdapter(adapterGenres);
                progressDialog.dismiss();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        musicalInstrumentsRef = FirebaseDatabase.getInstance().getReference().child("resources").child("musicalInstruments");
        musicalInstrumentsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long childrenCount = dataSnapshot.getChildrenCount();
                String[] resourceList = new String[(int) childrenCount];
                int i = 0;
                for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                    resourceList[i] = childDataSnapshot.getKey();
                    i++;
                }
                listItemsInstruments = new ArrayList<>();
                for (int j = 0; j < childrenCount; j++) {
                    Item item = new Item();
                    item.setItemString(resourceList[j]);
                    item.setChecked(false);
                    listItemsInstruments.add(item);
                }
                adapterInstruments = new ItemsListAdapter(NewMatchActivity.this, listItemsInstruments);
                listInstruments.setAdapter(adapterInstruments);
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
