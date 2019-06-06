package com.example.bandup.message;

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

public class MessageCancelPostulantFragment extends DialogFragment {
    private MessageModel message;

    public MessageModel getMessage() {
        return message;
    }

    public void setMessage(MessageModel message) {
        this.message = message;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("¿Deseas recahzar a " + message.getSender() + " para esta vacante?")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String uid = FirebaseAuth.getInstance().getUid();
                        assert uid != null;
                        FirebaseDatabase.getInstance().getReference().child("matches").child(message.getText()).child("postulants").child(message.getSender()).removeValue();
                        FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("messages").child(message.getMessageId()).removeValue();
                    }
                }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });
        return builder.create();
    }
}
