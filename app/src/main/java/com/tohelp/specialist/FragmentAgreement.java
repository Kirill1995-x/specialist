package com.tohelp.specialist;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.tohelp.specialist.dialogs.DialogRequestPermission;
import com.tohelp.specialist.dialogs.DialogSimple;
import com.tohelp.specialist.prepare.InformationAboutFile;
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
import static android.app.Activity.RESULT_OK;
import static com.tohelp.specialist.settings.Variable.ATTACH_FILE_CODE;

public class FragmentAgreement extends Fragment implements View.OnClickListener, com.tohelp.specialist.interfaces.displayAlert {

    @BindView(R.id.tilSurnameOfGraduate)
    TextInputLayout tilSurnameOfGraduate;
    @BindView(R.id.tilNameOfGraduate)
    TextInputLayout tilNameOfGraduate;
    @BindView(R.id.tilMiddlenameOfGraduate)
    TextInputLayout tilMiddlenameOfGraduate;
    @BindView(R.id.tietSurnameOfGraduate)
    TextInputEditText tietSurnameOfGraduate;
    @BindView(R.id.tietNameOfGraduate)
    TextInputEditText tietNameOfGraduate;
    @BindView(R.id.tietMiddlenameOfGraduate)
    TextInputEditText tietMiddlenameOfGraduate;
    @BindView(R.id.btnSendAgreement)
    Button btnSendAgreement;
    @BindView(R.id.fabAttachAgreement)
    FloatingActionButton fabAttachAgreement;
    @BindView(R.id.tvAttachAgreement)
    TextView tvAttachAgreement;
    @BindView(R.id.imgAgreement)
    ImageView imgAgreement;
    @BindView(R.id.progressBarAgreement)
    ProgressBar progressBar;
    SharedPreferences sharedPreferences;
    CheckInternetConnection checkInternetConnection;
    private Uri uri;
    private String title="";

    ActivityResultLauncher<Intent> getDocument = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Intent data = result.getData();
                    if (result.getResultCode() == RESULT_OK && data!=null) {
                        InformationAboutFile informationAboutFile = new InformationAboutFile(getContext(), data.getData());
                        if(informationAboutFile.checkSizeOfFile())
                        {
                            uri = informationAboutFile.getUri();
                            title = informationAboutFile.getTitle();
                        }
                        else
                        {
                            uri = null;
                            title = "";
                            displayAlert("code", getResources().getString(R.string.something_went_wrong), getResources().getString(R.string.size_of_file));
                        }
                    }
                }
            });

    ActivityResultLauncher<String[]> getPermissions = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(),
            new ActivityResultCallback<Map<String, Boolean>>() 
            {
                @Override
                public void onActivityResult(Map<String, Boolean> result) {
                    boolean write_external_storage = (result.get(Manifest.permission.WRITE_EXTERNAL_STORAGE)!=null)?
                                                      result.get(Manifest.permission.WRITE_EXTERNAL_STORAGE):false;
                    boolean read_external_storage = (result.get(Manifest.permission.READ_EXTERNAL_STORAGE)!=null)?
                                                    result.get(Manifest.permission.READ_EXTERNAL_STORAGE):false;
                    if(write_external_storage && read_external_storage)
                        changeStatus(uri);
                    else
                    {
                        DialogRequestPermission dialogRequestPermission = new DialogRequestPermission();
                        dialogRequestPermission.setCancelable(false);
                        dialogRequestPermission.show(requireActivity().getSupportFragmentManager(), "permission_dialog");
                    }
                }
            });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_agreement, container, false);

        //настройка ButterKnife
        ButterKnife.bind(this, v);

        sharedPreferences = requireActivity().getSharedPreferences(Variable.APP_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("flag", "sixth_fragment");
        editor.putString("city_of_graduate", "");
        editor.putString("organization_of_graduate", "");
        editor.apply();

        //проверка Интернета
        checkInternetConnection = new CheckInternetConnection(getActivity());

        if (savedInstanceState != null) {
            tietSurnameOfGraduate.setText(savedInstanceState.getString("surname"));
            tietNameOfGraduate.setText(savedInstanceState.getString("name"));
            tietMiddlenameOfGraduate.setText(savedInstanceState.getString("middlename"));
            uri = savedInstanceState.getParcelable("uri");
            title = savedInstanceState.getString("title");
            if(savedInstanceState.getBoolean("showProgressBar"))
            {
                progressBar.setVisibility(View.VISIBLE);
            }
        }

        //обработка нажатий на элементы
        btnSendAgreement.setOnClickListener(this);
        fabAttachAgreement.setOnClickListener(this);
        imgAgreement.setOnClickListener(this);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        //проверка документа на пустоту
        showStatus(uri);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.imgAgreement:
                displayAlert("code", getResources().getString(R.string.hint_agreement), getResources().getString(R.string.rule_agreement));
                break;
            case R.id.fabAttachAgreement:
                getPermissionForAgreement();
                break;
            case R.id.btnSendAgreement:
                send_agreement();
                break;
            default:
                break;
        }
    }

    private void getPermissionForAgreement()
    {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M &&
           Build.VERSION.SDK_INT<Build.VERSION_CODES.Q &&
           requireActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_DENIED &&
           requireActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_DENIED)
        {
            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
            getPermissions.launch(permissions);
        }
        else if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q) changeStatus(uri);
        else changeStatus(uri);
    }

    private void changeStatus(Uri uri_of_file)
    {
        if(uri_of_file == null)
        {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            intent = Intent.createChooser(intent, "Выбор файла");
            getDocument.launch(intent);
        }
        else
        {
            uri=null;
            fabAttachAgreement.setImageResource(R.drawable.ic_add);
            tvAttachAgreement.setText(R.string.attach_agreement);
        }
    }

    private void showStatus(Uri uri)
    {
        if(uri==null)
        {
            fabAttachAgreement.setImageResource(R.drawable.ic_add);
            tvAttachAgreement.setText(R.string.attach_agreement);
        }
        else
        {
            fabAttachAgreement.setImageResource(R.drawable.ic_delete);
            tvAttachAgreement.setText(title);
        }
    }

    private void send_agreement()
    {
        InputMethodManager inputMethodManager = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        Objects.requireNonNull(inputMethodManager).hideSoftInputFromWindow(btnSendAgreement.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        if(!checkInternetConnection.isNetworkConnected())
        {
            displayAlert("code",
                         getResources().getString(R.string.error_connection),
                         getResources().getString(R.string.check_connection));
        }
        else {
            Profile profile = new Profile(tilSurnameOfGraduate, tilNameOfGraduate, tilMiddlenameOfGraduate);
            if (profile.CheckFieldsAgreement()) {
                displayAlert("code",
                             getResources().getString(R.string.something_went_wrong),
                             getResources().getString(R.string.check_data));
            }
            else
            {
                if(uri==null || title.equals(""))
                {
                    displayAlert("code",
                                 getResources().getString(R.string.something_went_wrong),
                                 getResources().getString(R.string.agreement_not_attach));
                }
                else {
                    progressBar.setVisibility(View.VISIBLE);
                    //---
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, Variable.request_agreement_url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        JSONArray jsonArray = new JSONArray(response);
                                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                                        progressBar.setVisibility(View.GONE);
                                        displayAlert("code",
                                                     jsonObject.getString("title"),
                                                     jsonObject.getString("message"));
                                        uri = null;
                                        title="";
                                        fabAttachAgreement.setImageResource(R.drawable.ic_add);
                                        tvAttachAgreement.setText(R.string.attach_agreement);
                                        tietSurnameOfGraduate.setText("");
                                        tietNameOfGraduate.setText("");
                                        tietMiddlenameOfGraduate.setText("");
                                    } catch (JSONException e) {
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
                            params.put("surname_of_graduate", Objects.requireNonNull(tietSurnameOfGraduate.getText()).toString().trim());
                            params.put("name_of_graduate", Objects.requireNonNull(tietNameOfGraduate.getText()).toString().trim());
                            params.put("middlename_of_graduate", Objects.requireNonNull(tietMiddlenameOfGraduate.getText()).toString().trim());
                            params.put("surname", sharedPreferences.getString("shared_surname", ""));
                            params.put("name", sharedPreferences.getString("shared_name", ""));
                            params.put("middlename", sharedPreferences.getString("shared_middlename", ""));
                            params.put("email", sharedPreferences.getString("shared_email", ""));
                            params.put("phone", "+7" + sharedPreferences.getString("shared_phone_number", ""));
                            params.put("agreement", Base64.encodeToString(new InformationAboutFile(getContext(), uri).getFile(), Base64.DEFAULT));
                            params.put("title", title);
                            return params;
                        }
                    };
                    MySingleton.getInstance(getActivity()).addToRequestque(stringRequest);
                }
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("surname", Objects.requireNonNull(tietSurnameOfGraduate.getText()).toString().trim());
        outState.putString("name", Objects.requireNonNull(tietNameOfGraduate.getText()).toString().trim());
        outState.putString("middlename", Objects.requireNonNull(tietMiddlenameOfGraduate.getText()).toString().trim());
        outState.putParcelable("uri", uri);
        outState.putString("title", title);
        outState.putBoolean("showProgressBar", progressBar.isShown());
    }

    @Override
    public void displayAlert(String code, String title, String message)
    {
        DialogSimple dialog=new DialogSimple();
        DialogSimple.title=title;
        DialogSimple.message=message;
        dialog.show(requireActivity().getSupportFragmentManager(), "agreement_dialog");
    }
}