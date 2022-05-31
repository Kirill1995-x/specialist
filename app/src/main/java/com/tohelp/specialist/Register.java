package com.tohelp.specialist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.santalu.maskedittext.MaskEditText;
import com.tohelp.specialist.dialogs.DialogEditData;
import com.tohelp.specialist.dialogs.DialogFinish;
import com.tohelp.specialist.dialogs.DialogSimple;
import com.tohelp.specialist.prepare.Profile;
import com.tohelp.specialist.settings.CheckInternetConnection;
import com.tohelp.specialist.settings.MySingleton;
import com.tohelp.specialist.settings.Variable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Register extends AppCompatActivity implements View.OnClickListener, com.tohelp.specialist.interfaces.displayAlert {

    @BindView(R.id.btnRegister)
    Button btnRegister;
    @BindView(R.id.tilRegisterSurname)
    TextInputLayout tilSurname;
    @BindView(R.id.tilRegisterName)
    TextInputLayout tilName;
    @BindView(R.id.tilRegisterMiddlename)
    TextInputLayout tilMiddlename;
    @BindView(R.id.tilRegisterChidHome)
    TextInputLayout tilChildHome;
    @BindView(R.id.tilRegisterEmail)
    TextInputLayout tilEmail;
    @BindView(R.id.tilRegisterPhone)
    TextInputLayout tilPhone;
    @BindView(R.id.tilRegisterPassword)
    TextInputLayout tilPassword;
    @BindView(R.id.tilRegisterConfirmPassword)
    TextInputLayout tilConfirmPassword;
    @BindView(R.id.tilRegisterCity)
    TextInputLayout tilCity;
    @BindView(R.id.tilRegisterSubject)
    TextInputLayout tilSubject;
    @BindView(R.id.tietRegisterSurname)
    TextInputEditText tietSurname;
    @BindView(R.id.tietRegisterName)
    TextInputEditText tietName;
    @BindView(R.id.tietRegisterMiddlename)
    TextInputEditText tietMiddlename;
    @BindView(R.id.tietRegisterEmail)
    TextInputEditText tietEmail;
    @BindView(R.id.tietRegisterPhone)
    MaskEditText tietPhone;
    @BindView(R.id.tietRegisterChildHome)
    TextInputEditText tietChildHome;
    @BindView(R.id.tietRegisterPassword)
    TextInputEditText tietPassword;
    @BindView(R.id.tietRegisterConfirmPassword)
    TextInputEditText tietConfirmPassword;
    @BindView(R.id.spRegisterTypeOfSpecialist)
    Spinner spTypeOfSpecialist;
    @BindView(R.id.spStartWorkDayHours)
    Spinner spStartWorkDayHours;
    @BindView(R.id.spStartWorkDayMinutes)
    Spinner spStartWorkDayMinutes;
    @BindView(R.id.spEndWorkDayHours)
    Spinner spEndWorkDayHours;
    @BindView(R.id.spEndWorkDayMinutes)
    Spinner spEndWorkDayMinutes;
    @BindView(R.id.tvLinkForReadingDocument)
    TextView tvLink;
    @BindView(R.id.tvRegisterTypeOfSpecialist)
    TextView tvTypeOfSpecialist;
    @BindView(R.id.actvRegisterSubject)
    AutoCompleteTextView actvSubject;
    @BindView(R.id.actvRegisterCity)
    AutoCompleteTextView actvCity;
    @BindView(R.id.cbAgreement)
    CheckBox cbAgreement;
    @BindView(R.id.progressBarRegister)
    ProgressBar progressBar;
    CheckInternetConnection checkInternetConnection;
    String [] array_subject_of_Russian_Federation;
    String [] array_city_of_Russian_Federation;
    String [] array_specialist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //настройка ButterKnife
        ButterKnife.bind(this);

        //проверка Интернета
        checkInternetConnection=new CheckInternetConnection(this);

        //проверка ProgressBar
        if(savedInstanceState!=null && savedInstanceState.getBoolean("showProgressBar"))
        {
            progressBar.setVisibility(View.VISIBLE);
        }

        //обработка нажатия на элементы
        btnRegister.setOnClickListener(this);
        tvLink.setOnClickListener(this);

        //Ввод массивов специалистов
        array_specialist=getResources().getStringArray(R.array.array_specialist);
        //Ввод адаптера для работы с массивами: региона и города
        array_subject_of_Russian_Federation=getResources().getStringArray(R.array.array_subjects);
        array_city_of_Russian_Federation=getResources().getStringArray(R.array.array_cities);
        ArrayAdapter<String> adapter_for_subject=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, array_subject_of_Russian_Federation);
        ArrayAdapter<String> adapter_for_city=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, array_city_of_Russian_Federation);
        actvSubject.setAdapter(adapter_for_subject);
        actvCity.setAdapter(adapter_for_city);
        //Изменения стиля Spinner
        ArrayAdapter adapter_type_of_flat=ArrayAdapter.createFromResource(this, R.array.array_specialist, R.layout.spinner_layout);
        ArrayAdapter adapter_start_work_day_hours=ArrayAdapter.createFromResource(this, R.array.array_hours, R.layout.spinner_layout);
        ArrayAdapter adapter_start_work_day_minutes=ArrayAdapter.createFromResource(this, R.array.array_minutes, R.layout.spinner_layout);
        ArrayAdapter adapter_end_work_day_hours=ArrayAdapter.createFromResource(this, R.array.array_hours, R.layout.spinner_layout);
        ArrayAdapter adapter_end_work_day_minutes=ArrayAdapter.createFromResource(this, R.array.array_minutes, R.layout.spinner_layout);
        adapter_type_of_flat.setDropDownViewResource(R.layout.spinner_dropdown_layout);
        adapter_start_work_day_hours.setDropDownViewResource(R.layout.spinner_dropdown_layout);
        adapter_start_work_day_minutes.setDropDownViewResource(R.layout.spinner_dropdown_layout);
        adapter_end_work_day_hours.setDropDownViewResource(R.layout.spinner_dropdown_layout);
        adapter_end_work_day_minutes.setDropDownViewResource(R.layout.spinner_dropdown_layout);
        spTypeOfSpecialist.setAdapter(adapter_type_of_flat);
        spStartWorkDayHours.setAdapter(adapter_start_work_day_hours);
        spStartWorkDayMinutes.setAdapter(adapter_start_work_day_minutes);
        spEndWorkDayHours.setAdapter(adapter_end_work_day_hours);
        spEndWorkDayMinutes.setAdapter(adapter_end_work_day_minutes);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.btnRegister:
                confirmInput();
                break;
            case R.id.tvLinkForReadingDocument:
                Intent intent=new Intent(Register.this, Browser.class);
                intent.putExtra("url_phone", Variable.document_phone_url);
                intent.putExtra("url_tablet", Variable.document_tablet_url);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed()
    {
        if (!Objects.requireNonNull(tietSurname.getText()).toString().trim().equals("") | !Objects.requireNonNull(tietName.getText()).toString().trim().equals("") |
        !Objects.requireNonNull(tietMiddlename.getText()).toString().trim().equals("") | !Objects.requireNonNull(tietChildHome.getText()).toString().trim().equals("") |
        !Objects.requireNonNull(tietEmail.getText()).toString().trim().equals("") | !Objects.requireNonNull(tietPhone.getText()).toString().trim().equals("") |
        !Objects.requireNonNull(tietPassword.getText()).toString().trim().equals("") | !Objects.requireNonNull(tietConfirmPassword.getText()).toString().trim().equals("") |
        !spTypeOfSpecialist.getSelectedItem().toString().equals("-") | !actvSubject.getText().toString().trim().equals("") |
        !actvCity.getText().toString().trim().equals("") | !spStartWorkDayHours.getSelectedItem().toString().trim().equals("08") |
        !spStartWorkDayMinutes.getSelectedItem().toString().trim().equals("00") | !spEndWorkDayHours.getSelectedItem().toString().trim().equals("08") |
        !spEndWorkDayMinutes.getSelectedItem().toString().trim().equals("00") | cbAgreement.isChecked())
        {
            DialogEditData dialogEditData=new DialogEditData();
            dialogEditData.setCancelable(false);
            dialogEditData.show(getSupportFragmentManager(), "register_edit_data_dialog");
        }
        else
        {
            super.onBackPressed();
        }
    }

    public void confirmInput() {
        InputMethodManager inputMethodManager=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        Objects.requireNonNull(inputMethodManager).hideSoftInputFromWindow(btnRegister.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        Profile profile = new Profile(this, tilSurname, tilName, tilMiddlename, tilChildHome, tilEmail, tilPhone, tilPassword, tilConfirmPassword,
                                      tilSubject, tilCity, spTypeOfSpecialist, tvTypeOfSpecialist, cbAgreement);
        if (checkInternetConnection.isNetworkConnected())
        {
            if(profile.CheckFieldsRegister())
            {
                displayAlert("input_failed",
                        getResources().getString(R.string.something_went_wrong),
                        getResources().getString(R.string.check_data));
            }
            else
            {
                if (!Objects.requireNonNull(tilPassword.getEditText()).getText().toString().trim().equals(Objects.requireNonNull(tilConfirmPassword.getEditText()).getText().toString().trim())) {
                    displayAlert("input_error",
                                 getResources().getString(R.string.something_went_wrong),
                                 getResources().getString(R.string.check_passwords));
                } else {
                    //запуск ProgressBar
                    progressBar.setVisibility(View.VISIBLE);
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, Variable.register_url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        //завершение работы ProgressBar
                                        progressBar.setVisibility(View.INVISIBLE);
                                        JSONArray jsonArray = new JSONArray(response);
                                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                                        displayAlert(jsonObject.getString("code"), jsonObject.getString("title"), jsonObject.getString("message"));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        progressBar.setVisibility(View.INVISIBLE);
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    error.printStackTrace();
                                    progressBar.setVisibility(View.INVISIBLE);
                                }
                            }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            String time_of_work_day=spStartWorkDayHours.getSelectedItem().toString().trim()+
                                                    ":"+
                                                    spStartWorkDayMinutes.getSelectedItem().toString().trim()+
                                                    "-"+
                                                    spEndWorkDayHours.getSelectedItem().toString().trim()+
                                                    ":"+
                                                    spEndWorkDayMinutes.getSelectedItem().toString().trim();
                            //Получение email организации
                            String email_of_organization="";
                            Bundle arguments=getIntent().getExtras();
                            if (arguments != null)
                            {
                                email_of_organization= Objects.requireNonNull(arguments.get("email_of_organization")).toString();
                            }
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("surname", Objects.requireNonNull(tilSurname.getEditText()).getText().toString().trim());
                            params.put("name", Objects.requireNonNull(tilName.getEditText()).getText().toString().trim());
                            params.put("middlename", Objects.requireNonNull(tilMiddlename.getEditText()).getText().toString().trim());
                            params.put("type_of_specialist", spTypeOfSpecialist.getSelectedItem().toString().trim());
                            params.put("child_home", Objects.requireNonNull(tilChildHome.getEditText()).getText().toString().trim());
                            params.put("email_of_organization", email_of_organization);
                            params.put("email", Objects.requireNonNull(tilEmail.getEditText()).getText().toString().trim());
                            params.put("password",  Objects.requireNonNull(tilPassword.getEditText()).getText().toString().trim());
                            params.put("phone_number", Objects.requireNonNull(tilPhone.getEditText()).getText().toString().trim());
                            params.put("city", actvCity.getText().toString().trim());
                            params.put("subject_of_country", actvSubject.getText().toString().trim());
                            params.put("call_hours", time_of_work_day);
                            params.put("agreement", "success");
                            return params;
                        }
                    };
                    MySingleton.getInstance(Register.this).addToRequestque(stringRequest);
                }
            }
        }
        else
        {
            displayAlert("internet_connection_failed",
                        getResources().getString(R.string.error_connection),
                        getResources().getString(R.string.check_connection));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        outState.putString("surname", Objects.requireNonNull(tietSurname.getText()).toString().trim());
        outState.putString("name", Objects.requireNonNull(tietName.getText()).toString().trim());
        outState.putString("middlename", Objects.requireNonNull(tietMiddlename.getText()).toString().trim());
        outState.putString("child_home", Objects.requireNonNull(tietChildHome.getText()).toString().trim());
        outState.putString("email", Objects.requireNonNull(tietEmail.getText()).toString().trim());
        outState.putString("password", Objects.requireNonNull(tietPassword.getText()).toString().trim());
        outState.putString("confirm_password", Objects.requireNonNull(tietConfirmPassword.getText()).toString().trim());
        outState.putString("phone", Objects.requireNonNull(tietPhone.getText()).toString().trim());
        outState.putString("subject_of_Russian_Federation", actvSubject.getText().toString().trim());
        outState.putString("city_of_Russian_Federation", actvCity.getText().toString().trim());
        outState.putInt("type_of_specialist", spTypeOfSpecialist.getSelectedItemPosition());
        outState.putInt("start_work_day_hours", spStartWorkDayHours.getSelectedItemPosition());
        outState.putInt("start_work_day_minutes", spStartWorkDayMinutes.getSelectedItemPosition());
        outState.putInt("end_work_day_hours", spEndWorkDayHours.getSelectedItemPosition());
        outState.putInt("end_work_day_minutes", spEndWorkDayMinutes.getSelectedItemPosition());
        outState.putBoolean("agreement", cbAgreement.isChecked());
        outState.putBoolean("showProgressBar", progressBar.isShown());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
        tietSurname.setText(savedInstanceState.getString("surname"));
        tietName.setText(savedInstanceState.getString("name"));
        tietMiddlename.setText(savedInstanceState.getString("middlename"));
        tietChildHome.setText(savedInstanceState.getString("child_home"));
        tietEmail.setText(savedInstanceState.getString("email"));
        tietPassword.setText(savedInstanceState.getString("password"));
        tietConfirmPassword.setText(savedInstanceState.getString("confirm_password"));
        tietPhone.setText(savedInstanceState.getString("phone"));
        actvSubject.setText(savedInstanceState.getString("subject_of_Russian_Federation"));
        actvCity.setText(savedInstanceState.getString("city_of_Russian_Federation"));
        spTypeOfSpecialist.setSelection(savedInstanceState.getInt("type_of_specialist"));
        spStartWorkDayHours.setSelection(savedInstanceState.getInt("start_work_day_hours"));
        spStartWorkDayMinutes.setSelection(savedInstanceState.getInt("start_work_day_minutes"));
        spEndWorkDayHours.setSelection(savedInstanceState.getInt("end_work_day_hours"));
        spEndWorkDayMinutes.setSelection(savedInstanceState.getInt("end_work_day_minutes"));
        cbAgreement.setChecked(savedInstanceState.getBoolean("agreement"));
    }

    @Override
    public void displayAlert (String code, String title, String message)
    {
        if(code.equals("reg_success"))
        {
            DialogFinish dialog=new DialogFinish();
            DialogFinish.title=title;
            DialogFinish.message=message;
            dialog.setCancelable(false);
            dialog.show(getSupportFragmentManager(), "register_dialog_finish");
        }
        else
        {
            DialogSimple dialog=new DialogSimple();
            DialogSimple.title=title;
            DialogSimple.message=message;
            dialog.show(getSupportFragmentManager(), "register_dialog");
        }
    }
}
