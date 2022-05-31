package com.tohelp.specialist.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import com.tohelp.specialist.R;

public class DialogRequestPermission extends AppCompatDialogFragment
{
    ActivityResultLauncher<Intent> getPermissions = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {@Override public void onActivityResult(ActivityResult result) {}});

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle(R.string.title_permission).setMessage(R.string.message_permission)
                .setPositiveButton(R.string.button_positive_permission, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getPermissions.launch(new Intent()
                                              .setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                              .setData(Uri.parse("package:"+ requireActivity().getPackageName())));

                    }
                })
                .setNegativeButton(R.string.button_negative_permission, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                });
        return builder.create();
    }
}
