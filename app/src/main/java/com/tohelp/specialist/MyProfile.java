package com.tohelp.specialist;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.santalu.maskedittext.MaskEditText;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.tohelp.specialist.dialogs.DialogEditData;
import com.tohelp.specialist.dialogs.DialogFinish;
import com.tohelp.specialist.dialogs.DialogPassword;
import com.tohelp.specialist.dialogs.DialogRequestPermission;
import com.tohelp.specialist.dialogs.DialogSimple;
import com.tohelp.specialist.prepare.FindItemInSpinner;
import com.tohelp.specialist.prepare.InformationAboutFile;
import com.tohelp.specialist.prepare.PhotoDelete;
import com.tohelp.specialist.prepare.PhotoUpload;
import com.tohelp.specialist.prepare.Profile;
import com.tohelp.specialist.settings.CheckInternetConnection;
import com.tohelp.specialist.settings.MySingleton;
import com.tohelp.specialist.settings.Variable;
import com.tohelp.specialist.settings.Encryption;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class MyProfile extends AppCompatActivity implements View.OnClickListener, com.tohelp.specialist.interfaces.displayAlert
{
    @BindView(R.id.tilMyProfileSurname)
    TextInputLayout tilSurname;
    @BindView(R.id.tilMyProfileName)
    TextInputLayout tilName;
    @BindView(R.id.tilMyProfileMiddlename)
    TextInputLayout tilMiddlename;
    @BindView(R.id.tilMyProfileChildHome)
    TextInputLayout tilChildHome;
    @BindView(R.id.tilMyProfileEmail)
    TextInputLayout tilEmail;
    @BindView(R.id.tilMyProfilePhone)
    TextInputLayout tilPhone;
    @BindView(R.id.tilMyProfileSubject)
    TextInputLayout tilSubject;
    @BindView(R.id.tilMyProfileCity)
    TextInputLayout tilCity;
    @BindView(R.id.tietMyProfileSurname)
    TextInputEditText tietSurname;
    @BindView(R.id.tietMyProfileName)
    TextInputEditText tietName;
    @BindView(R.id.tietMyProfileMiddlename)
    TextInputEditText tietMiddlename;
    @BindView(R.id.tietMyProfileEmail)
    TextInputEditText tietEmail;
    @BindView(R.id.metMyProfilePhone)
    MaskEditText metPhone;
    @BindView(R.id.tietMyProfileChildHome)
    TextInputEditText tietChildHome;
    @BindView(R.id.actvMyProfileSubject)
    AutoCompleteTextView actvSubject;
    @BindView(R.id.actvMyProfileCity)
    AutoCompleteTextView actvCity;
    @BindView(R.id.spStartWorkDayHoursMyProfile)
    Spinner spStartWorkDayHours;
    @BindView(R.id.spStartWorkDayMinutesMyProfile)
    Spinner spStartWorkDayMinutes;
    @BindView(R.id.spEndWorkDayHoursMyProfile)
    Spinner spEndWorkDayHours;
    @BindView(R.id.spEndWorkDayMinutesMyProfile)
    Spinner spEndWorkDayMinutes;
    @BindView(R.id.btnRefreshData)
    Button btnRefreshData;
    @BindView(R.id.btnChangePassword)
    Button btnChangingPassword;
    @BindView(R.id.btnRefreshPhoto)
    Button btnRefreshPhoto;
    @BindView(R.id.btnRemovePhoto)
    Button btnRemovePhoto;
    @BindView(R.id.imgMyProfile)
    CircleImageView imgMyProfile;
    @BindView(R.id.progressBarMyProfile)
    ProgressBar progressBar;
    Bitmap bitmap;
    String start_hours, start_minutes, end_hours, end_minutes;
    SharedPreferences sharedPreferences;
    CheckInternetConnection checkInternetConnection;
    String[] array_subject_of_Russian_Federation;
    String[] array_city_of_Russian_Federation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        //настройка ButterKnife
        ButterKnife.bind(this);

        //получение файла с информацией о специалисте
        sharedPreferences=getSharedPreferences(Variable.APP_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("flag", "first_fragment");
        editor.putString("city_of_graduate", "");
        editor.putString("organization_of_graduate", "");
        editor.apply();

        //проверка Интернета
        checkInternetConnection=new CheckInternetConnection(this);

        //проверка ProgressBar
        if(savedInstanceState!=null && savedInstanceState.getBoolean("showProgressBar"))
        {
            progressBar.setVisibility(View.VISIBLE);
        }

        //загрузка фотографии
        if (!sharedPreferences.getString("shared_name_of_photo", "without_photo").equals("without_photo")) {
            String path=null;
            try {
                path = Encryption.decrypt(sharedPreferences.getString("shared_id", ""));
            } catch (Exception e) {
                e.printStackTrace();
            }
            String name_of_file = sharedPreferences.getString("shared_name_of_photo","");
            String url = Variable.place_of_photo_url+path+"/"+name_of_file;
            Picasso.get()
                    .load(url)
                    .placeholder(R.drawable.ic_account)
                    .error(R.drawable.ic_account)
                    .into(imgMyProfile);
        }

        //добавление стрелки "Вверх"
        ActionBar actionBar=getSupportActionBar();
        Objects.requireNonNull(actionBar).setDisplayHomeAsUpEnabled(true);
        //Связь массивов со списками из strings.xml
        array_subject_of_Russian_Federation=getResources().getStringArray(R.array.array_subjects);
        array_city_of_Russian_Federation=getResources().getStringArray(R.array.array_cities);
        //Ввод адаптера для работы с массивами: региона и города
        ArrayAdapter<String> adapter_for_subject=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, array_subject_of_Russian_Federation);
        ArrayAdapter<String> adapter_for_city=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, array_city_of_Russian_Federation);
        actvSubject.setAdapter(adapter_for_subject);
        actvCity.setAdapter(adapter_for_city);
        //Изменение стиля Spinner
        ArrayAdapter adapter_start_work_day_hours=ArrayAdapter.createFromResource(this, R.array.array_hours, R.layout.spinner_layout);
        ArrayAdapter adapter_start_work_day_minutes=ArrayAdapter.createFromResource(this, R.array.array_minutes, R.layout.spinner_layout);
        ArrayAdapter adapter_end_work_day_hours=ArrayAdapter.createFromResource(this, R.array.array_hours, R.layout.spinner_layout);
        ArrayAdapter adapter_end_work_day_minutes=ArrayAdapter.createFromResource(this, R.array.array_minutes, R.layout.spinner_layout);
        adapter_start_work_day_hours.setDropDownViewResource(R.layout.spinner_dropdown_layout);
        adapter_start_work_day_minutes.setDropDownViewResource(R.layout.spinner_dropdown_layout);
        adapter_end_work_day_hours.setDropDownViewResource(R.layout.spinner_dropdown_layout);
        adapter_end_work_day_minutes.setDropDownViewResource(R.layout.spinner_dropdown_layout);
        spStartWorkDayHours.setAdapter(adapter_start_work_day_hours);
        spStartWorkDayMinutes.setAdapter(adapter_start_work_day_minutes);
        spEndWorkDayHours.setAdapter(adapter_end_work_day_hours);
        spEndWorkDayMinutes.setAdapter(adapter_end_work_day_minutes);

        if(savedInstanceState==null)
        {
            //установка информации о специалисте
            tietSurname.setText(sharedPreferences.getString("shared_surname", ""));
            tietName.setText(sharedPreferences.getString("shared_name", ""));
            tietMiddlename.setText(sharedPreferences.getString("shared_middlename", ""));
            tietChildHome.setText(sharedPreferences.getString("shared_child_home",""));
            tietEmail.setText(sharedPreferences.getString("shared_email",""));
            metPhone.setText(sharedPreferences.getString("shared_phone_number",""));
            actvCity.setText(sharedPreferences.getString("shared_city",""));
            actvSubject.setText(sharedPreferences.getString("shared_subject", ""));
            //установка начала и конца рабочего дня
            int[] array_work_hours = new FindItemInSpinner(this).EstimateWorkHours();
            spStartWorkDayHours.setSelection(array_work_hours[0]);
            spStartWorkDayMinutes.setSelection(array_work_hours[1]);
            spEndWorkDayHours.setSelection(array_work_hours[2]);
            spEndWorkDayMinutes.setSelection(array_work_hours[3]);
            start_hours = spStartWorkDayHours.getSelectedItem().toString().trim();
            start_minutes = spStartWorkDayMinutes.getSelectedItem().toString().trim();
            end_hours = spEndWorkDayHours.getSelectedItem().toString().trim();
            end_minutes = spEndWorkDayMinutes.getSelectedItem().toString().trim();
        }

        //обработка нажатия на элементы
        btnRefreshData.setOnClickListener(this);
        btnRefreshPhoto.setOnClickListener(this);
        btnRemovePhoto.setOnClickListener(this);
        btnChangingPassword.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btnRefreshData:
                confirmInput();
                break;
            case R.id.btnRefreshPhoto:
                getPermissionForImage(Variable.REFRESH_PHOTO_CODE);
                break;
            case R.id.btnRemovePhoto:
                getPermissionForImage(Variable.DELETE_PHOTO_CODE);
                break;
            case R.id.btnChangePassword:
                DialogPassword dialogPassword=new DialogPassword();
                dialogPassword.show(getSupportFragmentManager(), "password_dialog");
                break;
            default:
                break;
        }
    }

    private void getPermissionForImage(int code)
    {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M &&
           Build.VERSION.SDK_INT<Build.VERSION_CODES.Q &&
           checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_DENIED &&
           checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_DENIED)
        {
            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
            requestPermissions(permissions, code);
        }
        else if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q)
        {
            switch (code) {
                case Variable.REFRESH_PHOTO_CODE:
                    CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1, 1).start(MyProfile.this);
                    break;
                case Variable.DELETE_PHOTO_CODE:
                    deleteImage();
                    break;
                default:
                    break;
            }
        }
        else
        {
            switch (code) {
                case Variable.REFRESH_PHOTO_CODE:
                    CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1, 1).start(MyProfile.this);
                    break;
                case Variable.DELETE_PHOTO_CODE:
                    deleteImage();
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
            case Variable.REFRESH_PHOTO_CODE:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED)
                {
                    CropImage.activity().setGuidelines(CropImageView.Guidelines.ON)
                            .setAspectRatio(1,1).start(MyProfile.this);
                }
                else
                {
                    DialogRequestPermission dialogRequestPermission = new DialogRequestPermission();
                    dialogRequestPermission.setCancelable(false);
                    dialogRequestPermission.show(getSupportFragmentManager(), "permission_dialog");
                }
                break;
            case Variable.DELETE_PHOTO_CODE:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED)
                {
                    deleteImage();
                }
                else
                {
                    DialogRequestPermission dialogRequestPermission = new DialogRequestPermission();
                    dialogRequestPermission.setCancelable(false);
                    dialogRequestPermission.show(getSupportFragmentManager(), "permission_dialog");
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK && result!=null)
            {
                Uri selectedImage = result.getUri();
                try {
                    if (Build.VERSION.SDK_INT < 28) {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                    } else {
                        ImageDecoder.Source resources = ImageDecoder.createSource(getContentResolver(), selectedImage);
                        bitmap = ImageDecoder.decodeBitmap(resources);
                    }

                    uploadImage(bitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void uploadImage(Bitmap bitmap_from_profile)
    {
        //запуск ProgressBar
        progressBar.setVisibility(View.VISIBLE);

        final PhotoUpload photoUpload = new PhotoUpload(bitmap_from_profile, MyProfile.this);
        imgMyProfile.setImageBitmap(photoUpload.getResizeBitmap());

        StringRequest stringRequest=new StringRequest(Request.Method.POST, Variable.update_image_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response)
                    {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            String code = jsonObject.getString("code");
                            String title = jsonObject.getString("title");
                            String message = jsonObject.getString("message");
                            progressBar.setVisibility(View.GONE);
                            if(!code.equals("send_photo_success"))
                            {
                                displayAlert(code, title, message);
                            }
                            else
                            {
                                sharedPreferences.edit().putString("shared_name_of_photo", jsonObject.getString("new_name_of_photo")).apply();
                            }
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
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                Map<String, String> params=new HashMap<String,String>();
                params.put("id", photoUpload.getId());
                params.put("access_token", photoUpload.getAccessToken());
                params.put("old_name_of_photo", photoUpload.getOldNameOfPhoto());
                params.put("image", photoUpload.imageToString());
                return params;
            }
        };
        MySingleton.getInstance(MyProfile.this).addToRequestque(stringRequest);
    }

    private void deleteImage()
    {
        if(!sharedPreferences.getString("shared_name_of_photo","without_photo").equals("without_photo"))
        {
            //запуск ProgressBar
            progressBar.setVisibility(View.VISIBLE);

            final PhotoDelete photoDelete = new PhotoDelete(MyProfile.this);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Variable.remove_image_url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONArray jsonArray = new JSONArray(response);
                                JSONObject jsonObject = jsonArray.getJSONObject(0);
                                String code = jsonObject.getString("code");
                                progressBar.setVisibility(View.GONE);
                                if (!code.equals("delete_photo_success")) {
                                    displayAlert(code, jsonObject.getString("title"), jsonObject.getString("message"));
                                } else {
                                    bitmap = null;
                                    imgMyProfile.setImageResource(R.drawable.ic_account);
                                    photoDelete.setWithoutPhoto();
                                }
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
                    Map<String, String> params = new HashMap<>();
                    params.put("id", photoDelete.getId());
                    params.put("access_token", photoDelete.getAccessToken());
                    params.put("name_of_photo", photoDelete.getNameOfPhoto());
                    return params;
                }
            };
            MySingleton.getInstance(MyProfile.this).addToRequestque(stringRequest);
        }
    }

    public void confirmInput()
    {
        InputMethodManager inputMethodManager=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        Objects.requireNonNull(inputMethodManager).hideSoftInputFromWindow(btnRefreshData.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        Profile profile = new Profile(this, tilSurname, tilName, tilMiddlename, tilChildHome, tilEmail, tilPhone, tilSubject, tilCity);
        if (checkInternetConnection.isNetworkConnected())
        {
            if(profile.CheckFieldsMyProfile())
            {
                displayAlert("input_failed",
                        getResources().getString(R.string.something_went_wrong),
                        getResources().getString(R.string.check_data));
            }
            else
            {
                //запуск ProgressBar
                progressBar.setVisibility(View.VISIBLE);
                StringRequest stringRequest = new StringRequest(Request.Method.POST, Variable.send_my_profile_url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONArray jsonArray = new JSONArray(response);
                                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                                    String code = jsonObject.getString("code");
                                    String title = jsonObject.getString("title");
                                    String message = jsonObject.getString("message");
                                    progressBar.setVisibility(View.GONE);
                                    displayAlert(code, title, message);
                                    if (code.equals("my_profile_email_failed")) {
                                        tietEmail.setText(jsonObject.getString("email_for_my_profile"));
                                    }
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
                        String time_of_work_day=spStartWorkDayHours.getSelectedItem().toString().trim()+
                                ":"+
                                spStartWorkDayMinutes.getSelectedItem().toString().trim()+
                                "-"+
                                spEndWorkDayHours.getSelectedItem().toString().trim()+
                                ":"+
                                spEndWorkDayMinutes.getSelectedItem().toString().trim();
                        try {
                            params.put("id", Encryption.decrypt(sharedPreferences.getString("shared_id","")));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        params.put("surname", Objects.requireNonNull(tilSurname.getEditText()).getText().toString().trim());
                        params.put("name", Objects.requireNonNull(tilName.getEditText()).getText().toString().trim());
                        params.put("middlename", Objects.requireNonNull(tilMiddlename.getEditText()).getText().toString().trim());
                        params.put("child_home", Objects.requireNonNull(tilChildHome.getEditText()).getText().toString().trim());
                        params.put("email", Objects.requireNonNull(tilEmail.getEditText()).getText().toString().trim());
                        params.put("phone_number", Objects.requireNonNull(tilPhone.getEditText()).getText().toString().trim());
                        params.put("city", actvCity.getText().toString().trim());
                        params.put("subject_of_country", actvSubject.getText().toString().trim());
                        params.put("call_hours", time_of_work_day);
                        params.put("access_token", sharedPreferences.getString("shared_access_token",""));
                        return params;
                    }
                };
                MySingleton.getInstance(MyProfile.this).addToRequestque(stringRequest);
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
        outState.putString("surname", Objects.requireNonNull(tietSurname.getText()).toString().trim());
        outState.putString("name", Objects.requireNonNull(tietName.getText()).toString().trim());
        outState.putString("middlename", Objects.requireNonNull(tietMiddlename.getText()).toString().trim());
        outState.putString("child_home", Objects.requireNonNull(tietChildHome.getText()).toString().trim());
        outState.putString("email", Objects.requireNonNull(tietEmail.getText()).toString().trim());
        outState.putString("phone", Objects.requireNonNull(metPhone.getText()).toString().trim());
        outState.putString("subject_of_Russian_Federation", actvSubject.getText().toString().trim());
        outState.putString("city_of_Russian_Federation", actvCity.getText().toString().trim());
        outState.putInt("start_work_day_hours", spStartWorkDayHours.getSelectedItemPosition());
        outState.putInt("start_work_day_minutes", spStartWorkDayMinutes.getSelectedItemPosition());
        outState.putInt("end_work_day_hours", spEndWorkDayHours.getSelectedItemPosition());
        outState.putInt("end_work_day_minutes", spEndWorkDayMinutes.getSelectedItemPosition());
        outState.putString("start_hours", start_hours);
        outState.putString("start_minutes", start_minutes);
        outState.putString("end_hours", end_hours);
        outState.putString("end_minutes", end_minutes);
        outState.putBoolean("showProgressBar", progressBar.isShown());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
        tietSurname.setText(savedInstanceState.getString("surname"));
        tietName.setText(savedInstanceState.getString("name"));
        tietMiddlename.setText(savedInstanceState.getString("middlename"));
        tietChildHome.setText(savedInstanceState.getString("child_home"));
        tietEmail.setText(savedInstanceState.getString("email"));
        metPhone.setText(savedInstanceState.getString("phone"));
        actvSubject.setText(savedInstanceState.getString("subject_of_Russian_Federation"));
        actvCity.setText(savedInstanceState.getString("city_of_Russian_Federation"));
        spStartWorkDayHours.setSelection(savedInstanceState.getInt("start_work_day_hours"));
        spStartWorkDayMinutes.setSelection(savedInstanceState.getInt("start_work_day_minutes"));
        spEndWorkDayHours.setSelection(savedInstanceState.getInt("end_work_day_hours"));
        spEndWorkDayMinutes.setSelection(savedInstanceState.getInt("end_work_day_minutes"));
        start_hours = savedInstanceState.getString("start_hours");
        start_minutes = savedInstanceState.getString("start_minutes");
        end_hours = savedInstanceState.getString("end_hours");
        end_minutes = savedInstanceState.getString("end_minutes");
    }

    @Override
    public void onBackPressed()
    {
        SharedPreferences preferences=getSharedPreferences(Variable.APP_PREFERENCES, MODE_PRIVATE);
        if (!Objects.requireNonNull(tietSurname.getText()).toString().trim().equals(preferences.getString("shared_surname","")) |
            !Objects.requireNonNull(tietName.getText()).toString().trim().equals(preferences.getString("shared_name", "")) |
            !Objects.requireNonNull(tietMiddlename.getText()).toString().trim().equals(preferences.getString("shared_middlename","")) |
            !Objects.requireNonNull(tietChildHome.getText()).toString().trim().equals(preferences.getString("shared_child_home","")) |
            !Objects.requireNonNull(tietEmail.getText()).toString().trim().equals(preferences.getString("shared_email", "")) |
            !Objects.requireNonNull(metPhone.getText()).toString().trim().equals(preferences.getString("shared_phone_number","")) |
            !actvCity.getText().toString().trim().equals(preferences.getString("shared_city","")) |
            !actvSubject.getText().toString().trim().equals(preferences.getString("shared_subject",""))|
            !spStartWorkDayHours.getSelectedItem().toString().trim().equals(start_hours)|
            !spStartWorkDayMinutes.getSelectedItem().toString().trim().equals(start_minutes)|
            !spEndWorkDayHours.getSelectedItem().toString().trim().equals(end_hours)|
            !spEndWorkDayMinutes.getSelectedItem().toString().trim().equals(end_minutes))
        {
            DialogEditData dialogEditData=new DialogEditData();
            dialogEditData.setCancelable(false);
            dialogEditData.show(getSupportFragmentManager(), "my_profile_edit_data_dialog");
        }
        else
        {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                if(this.getCurrentFocus()!=null)
                {
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void displayAlert(String code, String title, String message)
    {
        if (code.equals("my_profile_get_success"))
        {
            start_hours = spStartWorkDayHours.getSelectedItem().toString().trim();
            start_minutes = spStartWorkDayMinutes.getSelectedItem().toString().trim();
            end_hours = spEndWorkDayHours.getSelectedItem().toString().trim();
            end_minutes = spEndWorkDayMinutes.getSelectedItem().toString().trim();
            String time_of_work_day=start_hours+":"+start_minutes+"-"+end_hours+":"+end_minutes;
            DialogFinish dialog=new DialogFinish();
            DialogFinish.title=title;
            DialogFinish.message=message;
            dialog.setCancelable(false);
            dialog.show(getSupportFragmentManager(), "my_profile_dialog_finish");
            SharedPreferences.Editor editorMyProfile=sharedPreferences.edit();
            editorMyProfile.putString("shared_surname", Objects.requireNonNull(tilSurname.getEditText()).getText().toString().trim());
            editorMyProfile.putString("shared_name", Objects.requireNonNull(tilName.getEditText()).getText().toString().trim());
            editorMyProfile.putString("shared_middlename", Objects.requireNonNull(tilMiddlename.getEditText()).getText().toString().trim());
            editorMyProfile.putString("shared_child_home", Objects.requireNonNull(tilChildHome.getEditText()).getText().toString().trim());
            editorMyProfile.putString("shared_email", Objects.requireNonNull(tilEmail.getEditText()).getText().toString().trim());
            editorMyProfile.putString("shared_phone_number", Objects.requireNonNull(tilPhone.getEditText()).getText().toString().trim());
            editorMyProfile.putString("shared_city", actvCity.getText().toString().trim());
            editorMyProfile.putString("shared_subject", actvSubject.getText().toString().trim());
            editorMyProfile.putString("shared_call_hours", time_of_work_day);
            editorMyProfile.apply();
        }
        else
        {
            DialogSimple dialog=new DialogSimple();
            DialogSimple.title=title;
            DialogSimple.message=message;
            dialog.show(getSupportFragmentManager(), "my_profile_dialog");
        }
    }
}
