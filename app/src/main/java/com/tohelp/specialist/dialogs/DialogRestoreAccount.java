package com.tohelp.specialist.dialogs;

import android.app.Dialog;
import android.content.Context;
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
import com.tohelp.specialist.settings.CheckInternetConnection;
import com.tohelp.specialist.settings.MySingleton;
import com.tohelp.specialist.R;
import com.tohelp.specialist.settings.Variable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;


public class DialogRestoreAccount extends AppCompatDialogFragment implements View.OnClickListener
{
    @BindView(R.id.llRestoreProfile)
    LinearLayout llRestoreProfile;
    @BindView(R.id.llFailedInternetConnection)
    LinearLayout llFailedInternetConnection;
    @BindView(R.id.llShowResult)
    LinearLayout llShowResult;
    @BindView(R.id.btnRestoreProfile)
    Button btnRestoreProfile;
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
    @BindView(R.id.progressBarRestoreProfile)
    ProgressBar progressBar;
    AlertDialog.Builder builder;
    boolean showRestoreProfile=true;
    boolean showFailedInternetConnection=false;
    boolean showResult=false;
    public static String email;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View view = requireActivity().getLayoutInflater().inflate(R.layout.dialog_restore_profile, null);

        //настрока ButterKnife
        ButterKnife.bind(this, view);

        builder = new AlertDialog.Builder(requireActivity());

        builder.setView(view);

        //обработка нажатий на кнопки
        btnRestoreProfile.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnTryRequest.setOnClickListener(this);
        btnCancelRequest.setOnClickListener(this);
        btnOk.setOnClickListener(this);

        if(savedInstanceState!=null)
        {
            tvShowResult.setText(savedInstanceState.getString("message",""));
            showRestoreProfile = savedInstanceState.getBoolean("show_restore_profile", false);
            showFailedInternetConnection = savedInstanceState.getBoolean("show_failed_internet_connection", false);
            showResult = savedInstanceState.getBoolean("show_result", false);
        }

        //показ view в зависимости от флага
        if(showFailedInternetConnection)
        {
            llFailedInternetConnection.setVisibility(View.VISIBLE);
            llShowResult.setVisibility(View.INVISIBLE);
            llRestoreProfile.setVisibility(View.INVISIBLE);
        }
        else if (showResult)
        {
            llShowResult.setVisibility(View.VISIBLE);
            llFailedInternetConnection.setVisibility(View.INVISIBLE);
            llRestoreProfile.setVisibility(View.INVISIBLE);
        }
        else
        {
            llRestoreProfile.setVisibility(View.VISIBLE);
            llFailedInternetConnection.setVisibility(View.INVISIBLE);
            llShowResult.setVisibility(View.INVISIBLE);
        }

        return builder.create();
    }

    private void restoreProfileButton()
    {
        InputMethodManager inputMethodManager=(InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        Objects.requireNonNull(inputMethodManager).hideSoftInputFromWindow(btnRestoreProfile.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        if(new CheckInternetConnection(getActivity()).isNetworkConnected())
        {
            updateProfile();
        }
        else
        {
            llRestoreProfile.setVisibility(View.GONE);
            llFailedInternetConnection.setVisibility(View.VISIBLE);
            showRestoreProfile=false;
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
            case R.id.btnRestoreProfile:
                restoreProfileButton();
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
                    llRestoreProfile.setVisibility(View.VISIBLE);
                    llFailedInternetConnection.setVisibility(View.INVISIBLE);
                    showRestoreProfile = true;
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
        outState.putBoolean("show_restore_profile", showRestoreProfile);
        outState.putBoolean("show_failed_internet_connection", showFailedInternetConnection);
        outState.putBoolean("show_result", showResult);
    }

    private void updateProfile()
    {
        progressBar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Variable.account_restore_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try
                        {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            progressBar.setVisibility(View.GONE);
                            llRestoreProfile.setVisibility(View.GONE);
                            llShowResult.setVisibility(View.VISIBLE);
                            showRestoreProfile = false;
                            showResult = true;
                            tvShowResult.setText(jsonObject.getString("message"));
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
                params.put("email", email);
                return params;
            }
        };
        MySingleton.getInstance(getActivity()).addToRequestque(stringRequest);
    }
}
