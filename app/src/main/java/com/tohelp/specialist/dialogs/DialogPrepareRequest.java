package com.tohelp.specialist.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDialogFragment;

import com.tohelp.specialist.PrepareRequest;
import com.tohelp.specialist.R;

public class DialogPrepareRequest extends AppCompatDialogFragment
{
    public static String title;
    public static String message;
    AlertDialog.Builder builder;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        builder=new AlertDialog.Builder(getActivity());
        builder.setTitle(title).setMessage(message)
                .setPositiveButton(R.string.button_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getActivity(), PrepareRequest.class);
                        dialog.dismiss();
                        requireActivity().finish();
                        startActivity(intent);
                    }
                })
                .setNegativeButton(R.string.button_later, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        requireActivity().finish();
                    }
                });
        return builder.create();
    }
}
