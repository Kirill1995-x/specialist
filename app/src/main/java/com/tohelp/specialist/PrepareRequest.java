package com.tohelp.specialist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
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

public class PrepareRequest extends AppCompatActivity implements View.OnClickListener, com.tohelp.specialist.interfaces.displayAlert {

    @BindView(R.id.tilOrganization)
    TextInputLayout tilOrganization;
    @BindView(R.id.tilEmailOfOrganization)
    TextInputLayout tilEmailOfOrganization;
    @BindView(R.id.tilCityOfOrganization)
    TextInputLayout tilCityOfOrganization;
    @BindView(R.id.tietOrganization)
    TextInputEditText tietOrganization;
    @BindView(R.id.tietEmailOfOrganization)
    TextInputEditText tietEmailOfOrganization;
    @BindView(R.id.actvCityOfOrganization)
    AutoCompleteTextView actvCityOfOrganization;
    @BindView(R.id.btnPrepareRequest)
    Button btnPrepareRequest;
    @BindView(R.id.progressBarPrepareRequest)
    ProgressBar progressBar;
    CheckInternetConnection checkInternetConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prepare_request);

        //настройка ButterKnife
        ButterKnife.bind(this);

        //создание адаптера для работы с городами России
        String[] array_city = getResources().getStringArray(R.array.array_cities);
        ArrayAdapter<String> adapter_for_city=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, array_city);
        actvCityOfOrganization.setAdapter(adapter_for_city);

        //проверка Интернета
        checkInternetConnection=new CheckInternetConnection(this);

        //обработка нажатия на элемент
        btnPrepareRequest.setOnClickListener(this);
        
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
            case R.id.btnPrepareRequest:
                confirmInput();
                break;
            default:
                break;
        }
    }

    private void confirmInput() {
        InputMethodManager inputMethodManager=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        Objects.requireNonNull(inputMethodManager).hideSoftInputFromWindow(btnPrepareRequest.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        Profile profile = new Profile(tilOrganization, tilEmailOfOrganization, tilCityOfOrganization, false);
        if (checkInternetConnection.isNetworkConnected())
        {
            if (profile.CheckFieldsPrepareRequest())
            {
                displayAlert("input_wrong", getResources().getString(R.string.something_went_wrong), getResources().getString(R.string.check_data));
            }
            else
            {
                //запуск ProgressBar
                progressBar.setVisibility(View.VISIBLE);
                StringRequest stringRequest = new StringRequest(Request.Method.POST, Variable.prepare_request_url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try
                                {
                                    JSONArray jsonArray = new JSONArray(response);
                                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                                    progressBar.setVisibility(View.GONE);
                                    displayAlert(jsonObject.getString("code"),
                                                 jsonObject.getString("title"),
                                                 jsonObject.getString("message"));
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
                        params.put("organization", Objects.requireNonNull(tietOrganization.getText()).toString().trim());
                        params.put("email", Objects.requireNonNull(tietEmailOfOrganization.getText()).toString().trim());
                        params.put("city", actvCityOfOrganization.getText().toString().trim());
                        return params;
                    }
                };
                MySingleton.getInstance(PrepareRequest.this).addToRequestque(stringRequest);
            }
        }
        else
        {
            displayAlert("internet_connection_failed",
                    getResources().getString(R.string.something_went_wrong),
                    getResources().getString(R.string.check_connection));
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString("organization", Objects.requireNonNull(tietOrganization.getText()).toString().trim());
        outState.putString("email", Objects.requireNonNull(tietEmailOfOrganization.getText()).toString().trim());
        outState.putString("city", actvCityOfOrganization.getText().toString().trim());
        outState.putBoolean("showProgressBar", progressBar.isShown());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        tietOrganization.setText(savedInstanceState.getString("organization"));
        tietEmailOfOrganization.setText(savedInstanceState.getString("email"));
        actvCityOfOrganization.setText(savedInstanceState.getString("city"));
    }

    @Override
    public void displayAlert (String code, String title, String message)
    {
        if(code.equals("success"))
        {
            DialogFinish dialog = new DialogFinish();
            DialogFinish.title = title;
            DialogFinish.message = message;
            dialog.show(getSupportFragmentManager(), "prepare_request_dialog");
        }
        else
        {
            DialogSimple dialog = new DialogSimple();
            DialogSimple.title = title;
            DialogSimple.message = message;
            dialog.show(getSupportFragmentManager(), "prepare_request_dialog");
        }
    }
}