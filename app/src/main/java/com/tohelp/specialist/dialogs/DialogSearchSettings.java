package com.tohelp.specialist.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.android.material.textfield.TextInputEditText;
import com.tohelp.specialist.R;
import com.tohelp.specialist.settings.Variable;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DialogSearchSettings extends AppCompatDialogFragment implements View.OnClickListener {

    @BindView(R.id.actvCity)
    AutoCompleteTextView actvCity;
    @BindView(R.id.tietOrganization)
    TextInputEditText tietOrganization;
    @BindView(R.id.btnApply)
    Button btnApply;
    @BindView(R.id.btnCancel)
    Button btnCancel;
    AlertDialog.Builder builder;
    SharedPreferences sharedPreferences;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_search_settings, null);

        //настрока ButterKnife
        ButterKnife.bind(this, view);

        sharedPreferences = getActivity().getSharedPreferences(Variable.APP_PREFERENCES, Context.MODE_PRIVATE);

        //создание адаптера для работы с городами России
        String[] array_city = getResources().getStringArray(R.array.array_cities);
        ArrayAdapter<String> adapter_for_city=new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, array_city);
        actvCity.setAdapter(adapter_for_city);

        builder = new AlertDialog.Builder(requireActivity());
        builder.setView(view);

        //обработка нажатий на кнопки
        btnApply.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

        if(savedInstanceState!=null)
        {
            actvCity.setText(savedInstanceState.getString("city"," "));
            tietOrganization.setText(savedInstanceState.getString("organization"," "));
        }
        else
        {
            actvCity.setText(sharedPreferences.getString("city_of_graduate",""));
            tietOrganization.setText(sharedPreferences.getString("organization_of_graduate",""));
        }

        return builder.create();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btnApply:
                apply();
                break;
            case R.id.btnCancel:
                cancel();
                break;
            default:
                break;
        }
    }

    private void apply() {
        InputMethodManager inputMethodManager=(InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        Objects.requireNonNull(inputMethodManager).hideSoftInputFromWindow(btnApply.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("city_of_graduate", Objects.requireNonNull(actvCity.getText()).toString().trim());
        editor.putString("organization_of_graduate", Objects.requireNonNull(tietOrganization.getText()).toString().trim());
        editor.apply();
        getDialog().dismiss();
    }

    private void cancel() {
        InputMethodManager inputMethodManager=(InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        Objects.requireNonNull(inputMethodManager).hideSoftInputFromWindow(btnCancel.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        getDialog().cancel();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("city", Objects.requireNonNull(actvCity.getText()).toString().trim());
        outState.putString("organization", Objects.requireNonNull(tietOrganization.getText()).toString().trim());
    }
}
