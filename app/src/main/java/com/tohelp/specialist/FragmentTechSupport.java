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
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.tohelp.specialist.dialogs.DialogRequestPermission;
import com.tohelp.specialist.dialogs.DialogSimple;
import com.tohelp.specialist.prepare.InformationAboutFile;
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
import static com.tohelp.specialist.settings.Variable.ATTACH_SCREENSHOT_CODE;

public class FragmentTechSupport extends Fragment implements View.OnClickListener, com.tohelp.specialist.interfaces.displayAlert {

    @BindView(R.id.etMessageForTechSupport)
    EditText etMessage;
    @BindView(R.id.btnSendMessageToTechSupport)
    Button btnSendEmail;
    @BindView(R.id.tvAttachScreenshot)
    TextView tvAttachScreenshot;
    @BindView(R.id.fabAttachScreenshot)
    FloatingActionButton fabAttachScreenshot;
    @BindView(R.id.progressBarTechSupport)
    ProgressBar progressBarTechSupport;
    SharedPreferences sharedPreferences;
    CheckInternetConnection checkInternetConnection;
    private Uri uri;
    private String title="";

    ActivityResultLauncher<Intent> getScreenshot = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
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

        View v = inflater.inflate(R.layout.fragment_tech_support, container, false);

        //настройка ButterKnife
        ButterKnife.bind(this, v);

        //флаг для фрагмента Тех.поддержка
        sharedPreferences= requireActivity().getSharedPreferences(Variable.APP_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("flag", "fifth_fragment");
        editor.putString("city_of_graduate", "");
        editor.putString("organization_of_graduate", "");
        editor.apply();

        //обработка нажатия на элемент
        btnSendEmail.setOnClickListener(this);
        fabAttachScreenshot.setOnClickListener(this);

        //проверка Интернета
        checkInternetConnection=new CheckInternetConnection(getActivity());

        //восстановление сообщения при пересоздании фрагмента
        if(savedInstanceState!=null)
        {
            etMessage.setText(savedInstanceState.getString("message_to_tech_support"));
            uri = savedInstanceState.getParcelable("uri");
            title = savedInstanceState.getString("title");
            if(savedInstanceState.getBoolean("showProgressBar"))
            {
                progressBarTechSupport.setVisibility(View.VISIBLE);
            }
        }
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        //проверка документа на пустоту
        showStatus(uri);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btnSendMessageToTechSupport:
                sendMessageToTechSupport();
                break;
            case R.id.fabAttachScreenshot:
                getPermission();
                break;
            default:
                break;
        }
    }

    private void getPermission()
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
            getScreenshot.launch(intent);
        }
        else
        {
            uri=null;
            fabAttachScreenshot.setImageResource(R.drawable.ic_add);
            tvAttachScreenshot.setText(R.string.tech_support_attach_screenshot);
        }
    }

    private void showStatus(Uri uri)
    {
        if(uri==null)
        {
            fabAttachScreenshot.setImageResource(R.drawable.ic_add);
            tvAttachScreenshot.setText(R.string.tech_support_attach_screenshot);
        }
        else
        {
            fabAttachScreenshot.setImageResource(R.drawable.ic_delete);
            tvAttachScreenshot.setText(title);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString("message_to_tech_support", Objects.requireNonNull(etMessage.getText()).toString().trim());
        outState.putBoolean("showProgressBar", progressBarTechSupport.isShown());
        outState.putParcelable("uri", uri);
        outState.putString("title", title);
        super.onSaveInstanceState(outState);
    }

    private void sendMessageToTechSupport() {
        InputMethodManager inputMethodManager = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        Objects.requireNonNull(inputMethodManager).hideSoftInputFromWindow(btnSendEmail.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        if (checkInternetConnection.isNetworkConnected()) {
            if (!etMessage.getText().toString().trim().isEmpty()) {
                //запуск ProgressBar
                progressBarTechSupport.setVisibility(View.VISIBLE);
                //обнуление ошибки
                etMessage.setError(null);
                StringRequest stringRequest = new StringRequest(Request.Method.POST, Variable.request_tech_support_url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONArray jsonArray = new JSONArray(response);
                                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                                    progressBarTechSupport.setVisibility(View.GONE);
                                    displayAlert("code",
                                                 jsonObject.getString("title"),
                                                 jsonObject.getString("message"));
                                    etMessage.setText("");
                                    fabAttachScreenshot.setImageResource(R.drawable.ic_add);
                                    tvAttachScreenshot.setText(R.string.tech_support_attach_screenshot);
                                    uri=null;
                                    title="";
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    progressBarTechSupport.setVisibility(View.GONE);
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                error.printStackTrace();
                                progressBarTechSupport.setVisibility(View.GONE);
                            }
                        }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("surname", sharedPreferences.getString("shared_surname", ""));
                        params.put("name", sharedPreferences.getString("shared_name", ""));
                        params.put("middlename", sharedPreferences.getString("shared_middlename", ""));
                        params.put("email", sharedPreferences.getString("shared_email", ""));
                        params.put("phone", "+7" + sharedPreferences.getString("shared_phone_number", ""));
                        params.put("message", etMessage.getText().toString().trim());
                        params.put("version_sdk", Build.VERSION.SDK_INT+"");
                        params.put("version_os", Build.ID);
                        params.put("device", Build.DEVICE);
                        params.put("manufacturer", Build.MANUFACTURER);
                        params.put("model", Build.MODEL);
                        if(uri!=null) params.put("screenshot", Base64.encodeToString(new InformationAboutFile(getContext(), uri).getFile(), Base64.DEFAULT));
                        else params.put("screenshot","without");
                        params.put("title", title);
                        params.put("app", "specialist");
                        return params;
                    }
                };
                MySingleton.getInstance(getActivity()).addToRequestque(stringRequest);
            } else {
                etMessage.setError("Опишите проблему");
            }
        }
        else
        {
            displayAlert("code",
                         getResources().getString(R.string.error_connection),
                         getResources().getString(R.string.check_connection));
        }
    }

    @Override
    public void displayAlert(String code, String title, String message)
    {
        DialogSimple dialog=new DialogSimple();
        DialogSimple.title=title;
        DialogSimple.message=message;
        dialog.show(requireActivity().getSupportFragmentManager(), "tech_support_dialog");
    }
}