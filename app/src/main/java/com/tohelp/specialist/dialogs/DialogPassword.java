package com.tohelp.specialist.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.tohelp.specialist.settings.CheckInternetConnection;
import com.tohelp.specialist.settings.MySingleton;
import com.tohelp.specialist.R;
import com.tohelp.specialist.settings.Variable;
import com.tohelp.specialist.settings.Encryption;
import com.tohelp.specialist.prepare.Profile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DialogPassword extends AppCompatDialogFragment implements View.OnClickListener {

    @BindView(R.id.svChangePassword)
    ScrollView svChangePassword;
    @BindView(R.id.llShowResult)
    LinearLayout llShowResult;
    @BindView(R.id.llFailedInternetConnection)
    LinearLayout llFailedInternetConnection;
    @BindView(R.id.tilOldPassword)
    TextInputLayout tilOldPassword;
    @BindView(R.id.tilNewPassword)
    TextInputLayout tilNewPassword;
    @BindView(R.id.tilConfirmNewPassword)
    TextInputLayout tilConfirmNewPassword;
    @BindView(R.id.tietOldPassword)
    TextInputEditText tietOldPassword;
    @BindView(R.id.tietNewPassword)
    TextInputEditText tietNewPassword;
    @BindView(R.id.tietConfirmNewPassword)
    TextInputEditText tietConfirmNewPassword;
    @BindView(R.id.btnUpdate)
    Button btnUpdate;
    @BindView(R.id.btnCancelRequest)
    Button btnCancelRequest;
    @BindView(R.id.btnCancel)
    Button btnCancel;
    @BindView(R.id.btnOk)
    Button btnOk;
    @BindView(R.id.btnTryRequest)
    Button btnTryRequest;
    @BindView(R.id.tvChangePassword)
    TextView tvChangePassword;
    @BindView(R.id.progressBarChangePassword)
    ProgressBar progressBar;
    AlertDialog.Builder builder;
    boolean showChangePassword=true;
    boolean showFailedInternetConnection=false;
    boolean showResult=false;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_password, null);

        //настрока ButterKnife
        ButterKnife.bind(this, view);

        builder = new AlertDialog.Builder(requireActivity());

        builder.setView(view);

        //обработка нажатий на кнопки
        btnUpdate.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnOk.setOnClickListener(this);
        btnTryRequest.setOnClickListener(this);
        btnCancelRequest.setOnClickListener(this);

        if(savedInstanceState!=null)
        {
            tietOldPassword.setText(savedInstanceState.getString("old_password",""));
            tietNewPassword.setText(savedInstanceState.getString("new_password",""));
            tietConfirmNewPassword.setText(savedInstanceState.getString("confirm_new_password",""));
            tvChangePassword.setText(savedInstanceState.getString("message",""));
            showChangePassword = savedInstanceState.getBoolean("show_change_password", false);
            showFailedInternetConnection = savedInstanceState.getBoolean("show_failed_internet_connection", false);
            showResult = savedInstanceState.getBoolean("show_result", false);
        }

        //показ view в зависимости от флага
        if(showFailedInternetConnection)
        {
            llFailedInternetConnection.setVisibility(View.VISIBLE);
            llShowResult.setVisibility(View.INVISIBLE);
            svChangePassword.setVisibility(View.INVISIBLE);
        }
        else if (showResult)
        {
            llShowResult.setVisibility(View.VISIBLE);
            llFailedInternetConnection.setVisibility(View.INVISIBLE);
            svChangePassword.setVisibility(View.INVISIBLE);
        }
        else
        {
            svChangePassword.setVisibility(View.VISIBLE);
            llFailedInternetConnection.setVisibility(View.INVISIBLE);
            llShowResult.setVisibility(View.INVISIBLE);
        }

        return builder.create();
    }

    private void positiveButton()
    {
        InputMethodManager inputMethodManager=(InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        Objects.requireNonNull(inputMethodManager).hideSoftInputFromWindow(btnUpdate.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        if(new CheckInternetConnection(getActivity()).isNetworkConnected()) {
            CheckPasswords();
        }
        else
        {
            svChangePassword.setVisibility(View.INVISIBLE);
            llFailedInternetConnection.setVisibility(View.VISIBLE);
            showChangePassword=false;
            showFailedInternetConnection=true;
        }
    }

    private void negativeButton()
    {
        InputMethodManager inputMethodManager=(InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        Objects.requireNonNull(inputMethodManager).hideSoftInputFromWindow(btnCancel.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        getDialog().cancel();
    }

    private void negativeButtonCancelRequest()
    {
        InputMethodManager inputMethodManager=(InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        Objects.requireNonNull(inputMethodManager).hideSoftInputFromWindow(btnCancelRequest.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        getDialog().cancel();
    }

    private void okButton()
    {
        InputMethodManager inputMethodManager=(InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        Objects.requireNonNull(inputMethodManager).hideSoftInputFromWindow(btnOk.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        getDialog().cancel();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btnUpdate:
                positiveButton();
                break;
            case R.id.btnCancel:
                negativeButton();
                break;
            case R.id.btnOk:
                okButton();
                break;
            case R.id.btnTryRequest:
                if(new CheckInternetConnection(getActivity()).isNetworkConnected())
                {
                    svChangePassword.setVisibility(View.VISIBLE);
                    llFailedInternetConnection.setVisibility(View.INVISIBLE);
                    showChangePassword = true;
                    showFailedInternetConnection = false;
                }
                break;
            case R.id.btnCancelRequest:
                negativeButtonCancelRequest();
                break;
            default:
                break;
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("old_password", tietOldPassword.getText().toString().trim());
        outState.putString("new_password", tietNewPassword.getText().toString().trim());
        outState.putString("confirm_new_password", tietConfirmNewPassword.getText().toString().trim());
        outState.putString("message", tvChangePassword.getText().toString().trim());
        outState.putBoolean("show_change_password", showChangePassword);
        outState.putBoolean("show_failed_internet_connection", showFailedInternetConnection);
        outState.putBoolean("show_result", showResult);
    }

    private void CheckPasswords()
    {
        //запуск ProgressBar
        progressBar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Variable.compare_password_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try
                        {
                            Profile profile = new Profile(tilNewPassword, tilConfirmNewPassword);
                            String new_password = Objects.requireNonNull(tilNewPassword.getEditText()).getText().toString().trim();
                            String new_confirm_password = Objects.requireNonNull(tilConfirmNewPassword.getEditText()).getText().toString().trim();
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            progressBar.setVisibility(View.GONE);
                            if(jsonObject.getString("code").equals("compare_password_success"))//результат проверки старого пароля
                            {
                                tilOldPassword.setError("");//ошибок при проверке старого пароля нет
                                if(!profile.CheckFieldsChangePassword()) //проверка нового и подтвержденного паролей
                                {
                                    if (!new_password.equals(new_confirm_password)) //проверка совпадения нового и подтвержденного паролей
                                    {
                                        tilConfirmNewPassword.setError("Пароли не совпадают");
                                    }
                                    else
                                    {
                                        updatePassword(new_password);//переход к обновлению нового пароля
                                    }
                                }
                            }
                            else
                            {
                                tilOldPassword.setError(jsonObject.getString("message"));//проверка старого пароля не прошла
                                if(!profile.CheckFieldsChangePassword())//проверка нового и подтвержденного паролей
                                {
                                    if (!new_password.equals(new_confirm_password)) //проверка совпадения нового и подтвержденного паролей
                                    {
                                        tilConfirmNewPassword.setError("Пароли не совпадают");
                                    }
                                }

                            }
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                            progressBar.setVisibility(View.GONE);
                            tilOldPassword.setError("Неизвестная ошибка при проверке старого пароля. Сообщите Тех.поддержке");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        progressBar.setVisibility(View.GONE);
                        tilOldPassword.setError("Неизвестная ошибка при проверке старого пароля. Сообщите Тех.поддержке");
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                try {
                    params.put("id", Encryption.decrypt(requireActivity().getSharedPreferences(Variable.APP_PREFERENCES, Context.MODE_PRIVATE)
                                                                     .getString("shared_id","")));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                params.put("old_password", Objects.requireNonNull(tietOldPassword.getText()).toString().trim());
                params.put("access_token", requireActivity().getSharedPreferences(Variable.APP_PREFERENCES, Context.MODE_PRIVATE)
                                                            .getString("shared_access_token",""));
                return params;
            }
        };
        MySingleton.getInstance(getActivity()).addToRequestque(stringRequest);
    }

    private void updatePassword(String new_password)
    {
        //запуск ProgressBar
        progressBar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Variable.send_password_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try
                        {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            progressBar.setVisibility(View.GONE);
                            svChangePassword.setVisibility(View.GONE);
                            llShowResult.setVisibility(View.VISIBLE);
                            showChangePassword = false;
                            showResult = true;
                            tvChangePassword.setText(jsonObject.getString("message"));
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
                try {
                    params.put("id", Encryption.decrypt(getActivity().getSharedPreferences(Variable.APP_PREFERENCES, Context.MODE_PRIVATE)
                            .getString("shared_id","")));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                params.put("new_password", new_password);
                params.put("access_token", requireActivity().getSharedPreferences(Variable.APP_PREFERENCES, Context.MODE_PRIVATE)
                        .getString("shared_access_token",""));
                return params;
            }
        };
        MySingleton.getInstance(getActivity()).addToRequestque(stringRequest);
    }
}