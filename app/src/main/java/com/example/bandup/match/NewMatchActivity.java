package com.example.bandup.match;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bandup.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NewMatchActivity extends AppCompatActivity {
    ImageView matchClose;
    private EditText matchTitle;
    private EditText matchDescription;
    private EditText matchInstruments;
    private EditText matchGenres;
    private ProgressDialog progressDialog;
    private TextView postMatch;
    String title;
    String description;
    String instruments;
    String genres;
    private String uid;
    private MatchModel match;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_match_activity);

        matchClose = findViewById(R.id.match_close);
        matchTitle = findViewById(R.id.match_text_title);
        postMatch = findViewById(R.id.match_bar_text);
        matchDescription = findViewById(R.id.match_text_description);
        matchInstruments = findViewById(R.id.match_text_instruments);
        matchGenres = findViewById(R.id.match_text_genres);
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
                genres = matchGenres.getText().toString();
                instruments = matchInstruments.getText().toString();
                if (TextUtils.isEmpty(title) || TextUtils.isEmpty(description)) {
                    Toast.makeText(NewMatchActivity.this, "Porfavor, complete todos los campos antes de continuar", Toast.LENGTH_LONG).show();
                    return;
                }
                match.setDescription(description);
                match.setTitle(title);
                match.setInstruments(instruments);
                match.setGenres(genres);
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
        /**/
    }
}
