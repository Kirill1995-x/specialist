package com.tohelp.specialist.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.tohelp.specialist.R;
import com.tohelp.specialist.settings.Variable;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DialogUpdate extends AppCompatDialogFragment implements View.OnClickListener
{

    @BindView(R.id.btnUpdate)
    Button btnUpdate;
    @BindView(R.id.btnLater)
    Button btnLater;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = requireActivity().getLayoutInflater().inflate(R.layout.dialog_update, null);

        //настрока ButterKnife
        ButterKnife.bind(this, view);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);

        btnUpdate.setOnClickListener(this);
        btnLater.setOnClickListener(this);

        return builder.create();
    }

    private void clickSuccess()
    {
        requireActivity().getSharedPreferences(Variable.APP_NOTIFICATIONS, Context.MODE_PRIVATE).edit().putBoolean("compare_versions", true).apply();
        if(Variable.store.equals("play_market")) //выставить оценку в PlayMarket
        {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + requireActivity().getApplicationContext().getPackageName())));
            } catch (ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + requireActivity().getApplicationContext().getPackageName())));
            }
        }
        else
        {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://appgallery.huawei.com/#/app/C102782945"))); //выставить оценку в AppGallery
        }
        Objects.requireNonNull(getDialog()).dismiss();
    }

    private void clickLater()
    {
        requireActivity().getSharedPreferences(Variable.APP_NOTIFICATIONS, Context.MODE_PRIVATE).edit().putBoolean("compare_versions", true).apply();
        Objects.requireNonNull(getDialog()).dismiss();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btnUpdate:
                clickSuccess();
                break;
            case R.id.btnLater:
                clickLater();
                break;
            default:
                break;
        }
    }
}
