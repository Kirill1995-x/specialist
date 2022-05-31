package com.tohelp.specialist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.tohelp.specialist.dialogs.DialogSimple;
import com.tohelp.specialist.lists.ContactsGraduate;
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

public class RequestsPersonal extends AppCompatActivity implements View.OnClickListener, com.tohelp.specialist.interfaces.displayAlert {

    @BindView(R.id.tvFullnameRequestsPersonal)
    TextView tvFullname;
    @BindView(R.id.tvPhoneRequestsPersonal)
    TextView tvPhone;
    @BindView(R.id.tvEmailRequestsPersonal)
    TextView tvEmail;
    @BindView(R.id.tvCityRequestsPersonal)
    TextView tvCity;
    @BindView(R.id.tvGetRequestsPersonal)
    TextView tvGetRequest;
    @BindView(R.id.tvTextOfRequestsPersonal)
    TextView tvMessage;
    @BindView(R.id.tvTimeAndDateSentRequestsPersonal)
    TextView tvTimeAndDateSentRequest;
    @BindView(R.id.btnRequestsPersonal)
    Button btnRequestsPersonal;
    @BindView(R.id.progressBarActivityRequestsPersonal)
    ProgressBar progressBar;
    SharedPreferences sharedPreferences;
    CheckInternetConnection checkInternetConnection;
    String fullname, date_and_time, phone_of_user, email_of_user, message_of_user, city_of_user;
    int id_of_request;
    String id=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests_personal);

        //настройка ButterKnife
        ButterKnife.bind(this);

        //добавление стрелки "Вверх"
        ActionBar actionBar=getSupportActionBar();
        Objects.requireNonNull(actionBar).setDisplayHomeAsUpEnabled(true);

        //проверка Интернета
        checkInternetConnection=new CheckInternetConnection(this);

        //обработка нажатия на элемент
        btnRequestsPersonal.setOnClickListener(this);

        sharedPreferences = getSharedPreferences(Variable.APP_PREFERENCES, MODE_PRIVATE);

        //проверка ProgressBar
        if(savedInstanceState!=null && savedInstanceState.getBoolean("showProgressBar"))
        {
            progressBar.setVisibility(View.VISIBLE);
        }

        ContactsGraduate contactsGraduate=(ContactsGraduate)getIntent().getParcelableExtra("requests_personal");

        if (contactsGraduate != null) {
            id=contactsGraduate.getId();
            fullname=contactsGraduate.getFullnameOfUser();
            phone_of_user=contactsGraduate.getPhoneOfUser();
            email_of_user=contactsGraduate.getEmailOfUser();
            id_of_request=contactsGraduate.getRequest();
            message_of_user=contactsGraduate.getMessageOfUser();
            city_of_user=contactsGraduate.getCity();
            date_and_time=contactsGraduate.getTimeAndDateSentUser();

            tvFullname.setText(fullname);
            tvPhone.setText(phone_of_user);
            tvEmail.setText(email_of_user);
            tvGetRequest.setText(id_of_request);
            tvMessage.setText(message_of_user);
            tvCity.setText(city_of_user);
            tvTimeAndDateSentRequest.setText(date_and_time);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btnRequestsPersonal:
                confirmInput();
                break;
            default:
                break;
        }
    }

    public void confirmInput()
    {
        if (checkInternetConnection.isNetworkConnected())
        {
            //запуск ProgressBar
            progressBar.setVisibility(View.VISIBLE);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Variable.requests_personal_confirm_url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONArray jsonArray = new JSONArray(response);
                                JSONObject jsonObject = jsonArray.getJSONObject(0);
                                progressBar.setVisibility(View.GONE);
                                if (jsonObject.getString("code").equals("request_confirm_success")) {
                                    finish();
                                    //подсчет количества нажатий на кнопку
                                    SharedPreferences preferencesNotification = getSharedPreferences(Variable.APP_NOTIFICATIONS, MODE_PRIVATE);
                                    int count_of_click = preferencesNotification.getInt("count_of_click", Variable.MIN_COUNT_OF_CLICK);
                                    if(count_of_click<Variable.COUNT_OF_CLICK)
                                    {
                                        preferencesNotification.edit().putInt("count_of_click", count_of_click+1).apply();
                                    }
                                }
                                else {
                                    displayAlert("code", jsonObject.getString("title"), jsonObject.getString("message"));
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
                    sharedPreferences=getSharedPreferences(Variable.APP_PREFERENCES, MODE_PRIVATE);
                    Map<String, String> params = new HashMap<String, String>();
                    try {
                        params.put("id_of_specialist", Encryption.decrypt(sharedPreferences.getString("shared_id","")));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    params.put("access_token", sharedPreferences.getString("shared_access_token",""));
                    params.put("id_of_request", id);
                    return params;
                }
            };
            MySingleton.getInstance(RequestsPersonal.this).addToRequestque(stringRequest);
        }
        else
        {
            displayAlert("code", getResources().getString(R.string.error_connection), getResources().getString(R.string.check_connection));
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putBoolean("showProgressBar", progressBar.isShown());
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void displayAlert(String code, String title, String message)
    {
        DialogSimple dialog=new DialogSimple();
        DialogSimple.title=title;
        DialogSimple.message=message;
        dialog.show(getSupportFragmentManager(), "requests_personal_dialog");
    }
}
