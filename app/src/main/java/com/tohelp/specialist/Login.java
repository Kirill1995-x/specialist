package com.tohelp.specialist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.tohelp.specialist.dialogs.DialogRestoreAccount;
import com.tohelp.specialist.dialogs.DialogSimple;
import com.tohelp.specialist.settings.CheckInternetConnection;
import com.tohelp.specialist.settings.MySingleton;
import com.tohelp.specialist.settings.Variable;
import com.tohelp.specialist.settings.Encryption;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Login extends AppCompatActivity implements View.OnClickListener, com.tohelp.specialist.interfaces.displayAlert {

    @BindView(R.id.tilLogin)
    TextInputLayout tilLogin;
    @BindView(R.id.tilPassword)
    TextInputLayout tilPassword;
    @BindView(R.id.tietLogin)
    TextInputEditText tietLogin;
    @BindView(R.id.tietPassword)
    TextInputEditText tietPassword;
    @BindView(R.id.btnLogin)
    Button btnLogin;
    @BindView(R.id.tvForgotPassword)
    TextView tvforgotpassword;
    @BindView(R.id.tvRegisterLink)
    TextView tvRegisterLink;
    @BindView(R.id.progressBarLogin)
    ProgressBar progressBar;
    CheckInternetConnection checkInternetConnection;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //настройка ButterKnife
        ButterKnife.bind(this);

        //проверка Интернета
        checkInternetConnection=new CheckInternetConnection(this);

        //получение файла для записи информации о специалисте
        sharedPreferences = getSharedPreferences(Variable.APP_PREFERENCES, MODE_PRIVATE);

        //обработка нажатия на элементы
        tvRegisterLink.setOnClickListener(this);
        tvforgotpassword.setOnClickListener(this);
        btnLogin.setOnClickListener(this);

        //проверка ProgressBar
        if(savedInstanceState!=null && savedInstanceState.getBoolean("showProgressBar"))
        {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v)
    {
        Intent intent;
        switch (v.getId())
        {
            case R.id.tvForgotPassword:
                intent = new Intent(Login.this, ForgotPassword.class);
                startActivity(intent);
                break;
            case R.id.tvRegisterLink:
                intent = new Intent(Login.this, CheckOrganization.class);
                startActivity(intent);
                break;
            case R.id.btnLogin:
                confirmInput();
                break;
            default:
                break;
        }
    }

    private boolean validateLogin()
    {
        String emailInput= Objects.requireNonNull(tilLogin.getEditText()).getText().toString().trim();
        if (emailInput.isEmpty())
        {
            tilLogin.setError("Введите почту");
            return false;
        }
        else
        {
            tilLogin.setError(null);
            return true;
        }
    }

    private boolean validatePassword()
    {
        String passwordInput= Objects.requireNonNull(tilPassword.getEditText()).getText().toString().trim();
        if (passwordInput.isEmpty())
        {
            tilPassword.setError("Введите пароль");
            return false;
        }
        else
        {
            tilPassword.setError(null);
            return true;
        }
    }


    public void confirmInput() {
        InputMethodManager inputMethodManager=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        Objects.requireNonNull(inputMethodManager).hideSoftInputFromWindow(btnLogin.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        if (checkInternetConnection.isNetworkConnected()) {
            if (!validateLogin() | !validatePassword()) {
                try {
                    displayAlert("check_data",
                                 getResources().getString(R.string.something_went_wrong),
                                 getResources().getString(R.string.check_data));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                //запуск ProgressBar
                progressBar.setVisibility(View.VISIBLE);
                StringRequest stringRequest = new StringRequest(Request.Method.POST, Variable.login_url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONArray jsonArray = new JSONArray(response);
                                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                                    String code = jsonObject.getString("code");
                                    if (code.equals("account_deleted"))
                                    {
                                        progressBar.setVisibility(View.GONE);
                                        DialogRestoreAccount dialog = new DialogRestoreAccount();
                                        DialogRestoreAccount.email = jsonObject.getString("email");
                                        dialog.show(getSupportFragmentManager(), "login_dialog");
                                    }
                                    else if(code.equals("login_success"))
                                    {
                                        Intent intent = new Intent(Login.this, Main.class);
                                        editor = sharedPreferences.edit();
                                        try {
                                            editor.putString("shared_id", Encryption.encrypt(jsonObject.getString("id")));
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            progressBar.setVisibility(View.GONE);
                                        }
                                        editor.putString("shared_type_of_specialist", jsonObject.getString("type_of_specialist"));
                                        editor.putString("shared_status_of_busy", jsonObject.getString("status_of_busy"));
                                        editor.putString("shared_surname", jsonObject.getString("surname"));
                                        editor.putString("shared_name", jsonObject.getString("name"));
                                        editor.putString("shared_middlename", jsonObject.getString("middlename"));
                                        editor.putString("shared_child_home", jsonObject.getString("child_home"));
                                        editor.putString("shared_email", jsonObject.getString("email"));
                                        editor.putString("shared_phone_number", jsonObject.getString("phone_number"));
                                        editor.putString("shared_city", jsonObject.getString("city"));
                                        editor.putString("shared_subject", jsonObject.getString("subject_of_country"));
                                        editor.putString("shared_call_hours", jsonObject.getString("call_hours"));
                                        editor.putString("shared_name_of_photo", jsonObject.getString("name_of_photo"));
                                        editor.putString("shared_access_token", jsonObject.getString("access_token"));
                                        editor.putString("flag", "first_fragment");
                                        editor.apply();
                                        progressBar.setVisibility(View.GONE);
                                        finish();
                                        startActivity(intent);
                                    }
                                    else
                                    {
                                        progressBar.setVisibility(View.GONE);
                                        displayAlert(code,
                                                     jsonObject.getString("title"),
                                                     jsonObject.getString("message"));
                                    }
                                }
                                catch (JSONException e) {
                                    e.printStackTrace();
                                    progressBar.setVisibility(View.GONE);
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                error.printStackTrace();
                                progressBar.setVisibility(View.GONE);
                            }
                        }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("email", Objects.requireNonNull(tietLogin.getText()).toString());
                        params.put("password", Objects.requireNonNull(tietPassword.getText()).toString());
                        return params;
                    }
                };
                MySingleton.getInstance(Login.this).addToRequestque(stringRequest);
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
    protected void onSaveInstanceState(@NonNull Bundle outState)
    {
        outState.putString("login", Objects.requireNonNull(tietLogin.getText()).toString().trim());
        outState.putString("password", Objects.requireNonNull(tietPassword.getText()).toString().trim());
        outState.putBoolean("showProgressBar", progressBar.isShown());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
        tietLogin.setText(savedInstanceState.getString("login"));
        tietPassword.setText(savedInstanceState.getString("password"));
    }

    @Override
    public void displayAlert(String code, String title, String message)
    {
        DialogSimple dialog = new DialogSimple();
        DialogSimple.title = title;
        DialogSimple.message = message;
        dialog.show(getSupportFragmentManager(), "login_dialog");
    }
}
