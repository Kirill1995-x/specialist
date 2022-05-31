package com.tohelp.specialist;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.tohelp.specialist.dialogs.DialogFinish;
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

public class SplashScreen extends AppCompatActivity implements com.tohelp.specialist.interfaces.displayAlert {

    SharedPreferences sharedPreferencesFirst;
    SharedPreferences sharedPreferencesSecond;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        sharedPreferencesFirst=getSharedPreferences(Variable.APP_PREFERENCES, MODE_PRIVATE);
        sharedPreferencesSecond=getSharedPreferences(Variable.APP_NOTIFICATIONS, MODE_PRIVATE);

        updateVersionOfApp();
    }

    private void updateVersionOfApp()
    {
        if(!new CheckInternetConnection(this).isNetworkConnected())
        {
            displayAlert("code",
                         getResources().getString(R.string.error_connection),
                         getResources().getString(R.string.check_connection));
        }
        else
        {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Variable.update_version_of_app,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try
                            {
                                JSONArray jsonArray = new JSONArray(response);
                                JSONObject jsonObject = jsonArray.getJSONObject(0);
                                if(jsonObject.getString("code").equals("success"))
                                {
                                    int version_of_app = Integer.parseInt(BuildConfig.VERSION_NAME.replace(".",""));
                                    int version_from_server = Integer.parseInt(jsonObject.getString("version_of_app").replace(".",""));
                                    boolean compare_versions = (version_of_app>=version_from_server);
                                    sharedPreferencesSecond.edit().putBoolean("compare_versions", compare_versions).apply();
                                }

                                if(sharedPreferencesFirst.contains("shared_id"))
                                {
                                    updateData();
                                }
                                else
                                {
                                    finish();
                                    startActivity(new Intent(SplashScreen.this, Login.class));
                                }
                            }
                            catch (JSONException e)
                            {
                                e.printStackTrace();
                                finish();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                            finish();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("name_of_app", "specialist");
                    return params;
                }
            };
            MySingleton.getInstance(SplashScreen.this).addToRequestque(stringRequest);
        }
    }

    private void updateData()
    {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Variable.get_my_profile_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try
                        {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            if(jsonObject.getString("status_of_profile").equals("1")) {
                                if (jsonObject.getString("code").equals("success")) {
                                    SharedPreferences.Editor editor = sharedPreferencesFirst.edit();
                                    editor.putString("shared_status_of_busy", jsonObject.getString("status_of_busy"));
                                    editor.putString("shared_surname", jsonObject.getString("surname"));
                                    editor.putString("shared_name", jsonObject.getString("name"));
                                    editor.putString("shared_middlename", jsonObject.getString("middlename"));
                                    editor.putString("shared_child_home", jsonObject.getString("child_home"));
                                    editor.putString("shared_email", jsonObject.getString("email"));
                                    editor.putString("shared_phone_number", jsonObject.getString("phone_number"));
                                    editor.putString("shared_call_hours", jsonObject.getString("call_hours"));
                                    editor.putString("shared_city", jsonObject.getString("city"));
                                    editor.putString("shared_subject", jsonObject.getString("subject_of_country"));
                                    editor.putString("shared_name_of_photo", jsonObject.getString("name_of_photo"));
                                    editor.apply();
                                }
                                startActivity(new Intent(SplashScreen.this, Main.class));
                                finish();
                            }
                            else
                            {
                                SharedPreferences.Editor editor = sharedPreferencesFirst.edit();
                                editor.clear().apply();
                                startActivity(new Intent(SplashScreen.this, Login.class));
                                finish();
                            }
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                            finish();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        finish();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                try {
                    params.put("id", Encryption.decrypt(sharedPreferencesFirst.getString("shared_id","")));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                params.put("access_token", sharedPreferencesFirst.getString("shared_access_token",""));
                return params;
            }
        };
        MySingleton.getInstance(SplashScreen.this).addToRequestque(stringRequest);
    }

    @Override
    public void displayAlert(String code, String title, String message)
    {
        DialogFinish dialog=new DialogFinish();
        DialogFinish.title=title;
        DialogFinish.message=message;
        dialog.setCancelable(false);
        dialog.show(getSupportFragmentManager(), "splash_screen_dialog");
    }
}
