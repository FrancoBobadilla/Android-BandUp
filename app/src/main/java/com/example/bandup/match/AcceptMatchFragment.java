package com.example.bandup.match;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AcceptMatchFragment extends DialogFragment {
    private MatchModel match;

    public MatchModel getMatch() {
        return match;
    }

    public void setMatch(MatchModel match) {
        this.match = match;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Estas a punto de postularte para esta vacante. ¿Estas seguro que deseas hacerlo?")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String uid = FirebaseAuth.getInstance().getUid();
                        assert uid != null;
                        Toast.makeText(getActivity(), match + ", " + uid, Toast.LENGTH_LONG).show();
                        DatabaseReference refPostulant = FirebaseDatabase.getInstance().getReference().child("matches").child(match.getMatchId()).child("postulants");
                        refPostulant.child(uid).setValue("hola");
                        FirebaseDatabase.getInstance().getReference().child("users").child(match.getPublisher()).child("messages").setValue(match.getMatchId() + uid);
                        DatabaseReference messageToPublisher = FirebaseDatabase.getInstance().getReference().child("users").child(match.getPublisher()).child("messages").child(match.getMatchId() + uid);
                        messageToPublisher.child("type").setValue(1);
                        messageToPublisher.child("sender").setValue(uid);
                        messageToPublisher.child("receiver").setValue(match.getPublisher());
                        messageToPublisher.child("text").setValue(match.getMatchId());
                        messageToPublisher.child("timestamp").setValue("123123");

                    }
                }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        return builder.create();
    }
}
