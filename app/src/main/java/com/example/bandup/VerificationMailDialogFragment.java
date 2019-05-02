package com.example.bandup;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.Toast;

public class VerificationMailDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Enviamos un mail de verificacion a tu cuenta de correo electrónico. Por favor, abra el correo y siga el link.").setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Toast.makeText(getActivity(), "Mail de verificacion enivado", Toast.LENGTH_LONG).show();
                startActivity(new Intent(getActivity(), LoginActivity.class));
            }
        });
        return builder.create();
    }
}
