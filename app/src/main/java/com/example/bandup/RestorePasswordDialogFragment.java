package com.example.bandup;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class RestorePasswordDialogFragment extends DialogFragment {
    private String emailAddress;

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("¿Enviar mail de recuperación de contraseña a " + emailAddress + "?").setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                FirebaseAuth.getInstance().sendPasswordResetEmail(emailAddress)
        //                 .addOnCompleteListener(new OnCompleteListener<Void>() {
        //                    @Override
        //                    public void onComplete(@NonNull Task<Void> task) {
        //                        if (task.isSuccessful()) {
        //                            Toast.makeText(getActivity(), "Mail de recuperación enivado", Toast.LENGTH_LONG).show();
        //                        } else {
        //                            Toast.makeText(getActivity(), "Error al enviar el mail de recuperación", Toast.LENGTH_LONG).show();
        //                        }
        //                    }
        //                })
                ;

            }
        }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });
        return builder.create();
    }
}
