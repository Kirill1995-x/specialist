package com.tohelp.specialist.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.tohelp.specialist.Login;
import com.tohelp.specialist.settings.CheckInternetConnection;
import com.tohelp.specialist.settings.MySingleton;
import com.tohelp.specialist.R;
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

import static android.content.Context.MODE_PRIVATE;

public class DialogDeleteProfile extends AppCompatDialogFragment implements View.OnClickListener {

    @BindView(R.id.llDeleteProfile)
    LinearLayout llDeleteProfile;
    @BindView(R.id.llFailedInternetConnection)
    LinearLayout llFailedInternetConnection;
    @BindView(R.id.llShowResult)
    LinearLayout llShowResult;
    @BindView(R.id.btnDeleteProfile)
    Button btnDeleteProfile;
    @BindView(R.id.btnCancel)
    Button btnCancel;
    @BindView(R.id.btnTryRequest)
    Button btnTryRequest;
    @BindView(R.id.btnCancelRequest)
    Button btnCancelRequest;
    @BindView(R.id.btnOk)
    Button btnOk;
    @BindView(R.id.tvShowResult)
    TextView tvShowResult;
    @BindView(R.id.progressBarDeleteProfile)
    ProgressBar progressBar;
    AlertDialog.Builder builder;
    boolean showDeleteProfile=true;
    boolean showFailedInternetConnection=false;
    boolean showResult=false;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View view = requireActivity().getLayoutInflater().inflate(R.layout.dialog_delete_profile, null);

        //настрока ButterKnife
        ButterKnife.bind(this, view);

        builder = new AlertDialog.Builder(requireActivity());

        builder.setView(view);

        //обработка нажатий на кнопки
        btnDeleteProfile.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnTryRequest.setOnClickListener(this);
        btnCancelRequest.setOnClickListener(this);
        btnOk.setOnClickListener(this);

        if(savedInstanceState!=null)
        {
            tvShowResult.setText(savedInstanceState.getString("message",""));
            showDeleteProfile = savedInstanceState.getBoolean("show_delete_profile", false);
            showFailedInternetConnection = savedInstanceState.getBoolean("show_failed_internet_connection", false);
            showResult = savedInstanceState.getBoolean("show_result", false);
        }

        //показ view в зависимости от флага
        if(showFailedInternetConnection)
        {
            llFailedInternetConnection.setVisibility(View.VISIBLE);
            llShowResult.setVisibility(View.INVISIBLE);
            llDeleteProfile.setVisibility(View.INVISIBLE);
        }
        else if (showResult)
        {
            llShowResult.setVisibility(View.VISIBLE);
            llFailedInternetConnection.setVisibility(View.INVISIBLE);
            llDeleteProfile.setVisibility(View.INVISIBLE);
        }
        else
        {
            llDeleteProfile.setVisibility(View.VISIBLE);
            llFailedInternetConnection.setVisibility(View.INVISIBLE);
            llShowResult.setVisibility(View.INVISIBLE);
        }

        return builder.create();
    }

    private void deleteProfileButton()
    {
        InputMethodManager inputMethodManager=(InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        Objects.requireNonNull(inputMethodManager).hideSoftInputFromWindow(btnDeleteProfile.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        if(new CheckInternetConnection(getActivity()).isNetworkConnected())
        {
            deleteRequests();
        }
        else
        {
            llDeleteProfile.setVisibility(View.GONE);
            llFailedInternetConnection.setVisibility(View.VISIBLE);
            showDeleteProfile=false;
            showFailedInternetConnection=true;
        }
    }

    private void cancelButton()
    {
        InputMethodManager inputMethodManager=(InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        Objects.requireNonNull(inputMethodManager).hideSoftInputFromWindow(btnCancel.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        getDialog().cancel();
    }

    private void okButton()
    {
        InputMethodManager inputMethodManager=(InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        Objects.requireNonNull(inputMethodManager).hideSoftInputFromWindow(btnOk.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        getDialog().cancel();
    }

    private void cancelRequestButton()
    {
        InputMethodManager inputMethodManager=(InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        Objects.requireNonNull(inputMethodManager).hideSoftInputFromWindow(btnCancelRequest.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        getDialog().cancel();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btnDeleteProfile:
                deleteProfileButton();
                break;
            case R.id.btnCancel:
                cancelButton();
                break;
            case R.id.btnOk:
                okButton();
                break;
            case R.id.btnTryRequest:
                if(new CheckInternetConnection(getActivity()).isNetworkConnected())
                {
                    llDeleteProfile.setVisibility(View.VISIBLE);
                    llFailedInternetConnection.setVisibility(View.INVISIBLE);
                    showDeleteProfile = true;
                    showFailedInternetConnection = false;
                }
                break;
            case R.id.btnCancelRequest:
                cancelRequestButton();
                break;
            default:
                break;
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("message", tvShowResult.getText().toString().trim());
        outState.putBoolean("show_delete_profile", showDeleteProfile);
        outState.putBoolean("show_failed_internet_connection", showFailedInternetConnection);
        outState.putBoolean("show_result", showResult);
    }

    private void deleteRequests()
    {
        progressBar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Variable.account_requests_reset_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try
                        {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            if(jsonObject.getString("code").equals("success"))
                            {
                                updateProfile();
                            }
                            else
                            {
                                progressBar.setVisibility(View.GONE);
                                llDeleteProfile.setVisibility(View.GONE);
                                llShowResult.setVisibility(View.VISIBLE);
                                showDeleteProfile = false;
                                showResult = true;
                                tvShowResult.setText(jsonObject.getString("message"));
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
                try {
                    params.put("id", Encryption.decrypt(getActivity().getSharedPreferences(Variable.APP_PREFERENCES, MODE_PRIVATE)
                            .getString("shared_id","")));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                params.put("access_token", getActivity().getSharedPreferences(Variable.APP_PREFERENCES, MODE_PRIVATE)
                        .getString("shared_access_token",""));
                return params;
            }
        };
        MySingleton.getInstance(getActivity()).addToRequestque(stringRequest);
    }

    private void updateProfile()
    {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Variable.account_delete_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try
                        {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            progressBar.setVisibility(View.GONE);
                            if(jsonObject.getString("code").equals("success"))
                            {
                                Intent intent = new Intent(getActivity(), Login.class);
                                requireActivity().getSharedPreferences(Variable.APP_PREFERENCES, MODE_PRIVATE).edit().clear().apply();
                                getDialog().dismiss();
                                requireActivity().finish();
                                startActivity(intent);
                            }
                            else {
                                llDeleteProfile.setVisibility(View.GONE);
                                llShowResult.setVisibility(View.VISIBLE);
                                showDeleteProfile = false;
                                showResult = true;
                                tvShowResult.setText(jsonObject.getString("message"));
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
                try {
                    params.put("id", Encryption.decrypt(getActivity().getSharedPreferences(Variable.APP_PREFERENCES, MODE_PRIVATE)
                            .getString("shared_id","")));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                params.put("access_token", getActivity().getSharedPreferences(Variable.APP_PREFERENCES, MODE_PRIVATE)
                        .getString("shared_access_token",""));
                return params;
            }
        };
        MySingleton.getInstance(getActivity()).addToRequestque(stringRequest);
    }
}
