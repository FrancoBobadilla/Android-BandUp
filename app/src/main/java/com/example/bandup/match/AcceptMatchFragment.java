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
    private String matchId;

    public String getMatchId() {
        return matchId;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
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
                        Toast.makeText(getActivity(), matchId + ", " + uid, Toast.LENGTH_LONG).show();
                        DatabaseReference refPostulant = FirebaseDatabase.getInstance().getReference().child("matches").child(matchId).child("postulants");
                        refPostulant.child(uid).setValue("hola");
                    }
                }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        return builder.create();
    }
}
