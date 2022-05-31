package com.tohelp.specialist;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.tohelp.specialist.dialogs.DialogPrepareRequest;
import com.tohelp.specialist.dialogs.DialogSimple;
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

public class CheckOrganization extends AppCompatActivity implements View.OnClickListener, com.tohelp.specialist.interfaces.displayAlert {

    @BindView(R.id.tilRegisterEmail)
    TextInputLayout tilEmail;
    @BindView(R.id.tietRegisterEmail)
    TextInputEditText tietEmail;
    @BindView(R.id.btnGoToRegistration)
    Button btnGoToRegistration;
    @BindView(R.id.progressBarCheckEmailOfOrganization)
    ProgressBar progressBar;
    Intent intent;
    CheckInternetConnection checkInternetConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //настройка ButterKnife
        ButterKnife.bind(this);

        //проверка Интернета
        checkInternetConnection=new CheckInternetConnection(this);

        //обработка нажатия на элемент
        btnGoToRegistration.setOnClickListener(this);

        //проверка ProgressBar
        if(savedInstanceState!=null && savedInstanceState.getBoolean("showProgressBar"))
        {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btnGoToRegistration:
                confirmInput();
                break;
            default:
                break;
        }
    }

    private boolean validateEmail()
    {
        String emailInput= Objects.requireNonNull(tilEmail.getEditText()).getText().toString().trim();
        if (emailInput.isEmpty())
        {
            tilEmail.setError("Введите почту");
            return false;
        }
        else
        {
            tilEmail.setError("");
            return true;
        }
    }

    private void confirmInput()
    {
        InputMethodManager inputMethodManager=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        Objects.requireNonNull(inputMethodManager).hideSoftInputFromWindow(btnGoToRegistration.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        if (checkInternetConnection.isNetworkConnected())
        {
            if (!validateEmail())
            {
                displayAlert("input_wrong", getResources().getString(R.string.something_went_wrong), getResources().getString(R.string.check_data));
            }
            else
            {
                //запуск ProgressBar
                progressBar.setVisibility(View.VISIBLE);
                StringRequest stringRequest = new StringRequest(Request.Method.POST, Variable.compare_email_url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try
                                {
                                    JSONArray jsonArray = new JSONArray(response);
                                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                                    String code = jsonObject.getString("code");
                                    progressBar.setVisibility(View.GONE);
                                    if (code.equals("compare_email_successful"))
                                    {
                                        intent = new Intent(CheckOrganization.this, Register.class);
                                        intent.putExtra("email_of_organization", Objects.requireNonNull(tietEmail.getText()).toString().trim());
                                        finish();
                                        startActivity(intent);
                                    }
                                    else
                                    {
                                        displayAlert(code, jsonObject.getString("title"), jsonObject.getString("message"));
                                    }
                                }
                                catch (JSONException e)
                                {
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
                        params.put("email", Objects.requireNonNull(tietEmail.getText()).toString().trim());
                        return params;
                    }
                };
                MySingleton.getInstance(CheckOrganization.this).addToRequestque(stringRequest);
            }
        }
        else
        {
            displayAlert("internet_connection_failed",
                               getResources().getString(R.string.something_went_wrong) ,
                               getResources().getString(R.string.check_connection));
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString("email", Objects.requireNonNull(tietEmail.getText()).toString().trim());
        outState.putBoolean("showProgressBar", progressBar.isShown());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        tietEmail.setText(savedInstanceState.getString("email"));
    }

    @Override
    public void displayAlert (String code, String title, String message)
    {
        if(code.equals("compare_email_failed"))
        {
            DialogPrepareRequest dialog = new DialogPrepareRequest();
            DialogPrepareRequest.title = title;
            DialogPrepareRequest.message = message;
            dialog.show(getSupportFragmentManager(), "main_check_dialog");
        }
        else
        {
            DialogSimple dialog = new DialogSimple();
            DialogSimple.title = title;
            DialogSimple.message = message;
            dialog.show(getSupportFragmentManager(), "main_check_dialog");
        }

    }
}
