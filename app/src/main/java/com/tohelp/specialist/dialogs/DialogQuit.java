package com.tohelp.specialist.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.tohelp.specialist.R;
import com.tohelp.specialist.settings.Variable;
import com.tohelp.specialist.Login;

import static android.content.Context.MODE_PRIVATE;

public class DialogQuit extends AppCompatDialogFragment
{
    SharedPreferences preferences;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(requireActivity());
        preferences = requireActivity().getSharedPreferences(Variable.APP_PREFERENCES, MODE_PRIVATE);
        builder.setTitle(getResources().getString(R.string.title_quit)).setMessage(getResources().getString(R.string.message_quit));
        builder.setPositiveButton(getResources().getString(R.string.button_quit), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(getActivity(), Login.class);
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear().apply();
                dialog.dismiss();
                requireActivity().finish();
                startActivity(intent);
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.button_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        return builder.create();
    }
}
