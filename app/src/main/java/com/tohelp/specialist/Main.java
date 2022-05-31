package com.tohelp.specialist;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;
import com.tohelp.specialist.dialogs.DialogDeleteProfile;
import com.tohelp.specialist.dialogs.DialogEstimate;
import com.tohelp.specialist.dialogs.DialogQuit;
import com.tohelp.specialist.dialogs.DialogSimple;
import com.tohelp.specialist.dialogs.DialogUpdate;
import com.tohelp.specialist.settings.CheckInternetConnection;
import com.tohelp.specialist.settings.MySingleton;
import com.tohelp.specialist.settings.Variable;
import com.tohelp.specialist.settings.Encryption;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;


public class Main extends AppCompatActivity implements com.tohelp.specialist.interfaces.displayAlert {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    SharedPreferences sharedPreferences;
    String fullname;
    View headerView;
    TextView FullName;
    SwitchCompat switchCompat;
    CheckInternetConnection checkInternetConnection;
    String status_of_busy;
    TextView tvRequestsForAll, tvRequestsPersonal, tvRequestsInProgress;
    CircleImageView imageOfAccount;
    String parameter_for_request=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        //настройка ButterKnife
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(this, drawer,toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(drawerView.getWindowToken(), 0);
            }
        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                displaySelectedScreen(menuItem.getItemId());
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        //проверка Интернета
        checkInternetConnection=new CheckInternetConnection(this);

        //установка количества запросов
        tvRequestsForAll=(TextView)navigationView.getMenu().findItem(R.id.nav_requests_for_all).getActionView();
        tvRequestsPersonal=(TextView)navigationView.getMenu().findItem(R.id.nav_requests_personal).getActionView();
        tvRequestsInProgress=(TextView)navigationView.getMenu().findItem(R.id.nav_requests_in_progress).getActionView();

        //получение status_of_busy и прослушивание SwitchCompat
        sharedPreferences=getSharedPreferences(Variable.APP_PREFERENCES, MODE_PRIVATE);
        status_of_busy=sharedPreferences.getString("shared_status_of_busy","");
        switchCompat=(SwitchCompat)navigationView.getMenu().findItem(R.id.status_of_busy).getActionView();
        //установка начального положения переключателя
        if(status_of_busy.equals("1"))
        {
            switchCompat.setChecked(true);
            switchCompat.setText(R.string.online);
        }
        else
        {
            switchCompat.setChecked(false);
            switchCompat.setText(R.string.offline);
        }
        //изменение положения переключателя
        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    StatusOfBusyRequest("1");
                    switchCompat.setText(R.string.online);
                }
                else
                {
                    StatusOfBusyRequest("0");
                    switchCompat.setText(R.string.offline);
                }
            }
        });

        //проверка первого запуска приложения
        if(sharedPreferences.getBoolean("first_start", true))
        {
            displaySelectedScreen(R.id.nav_requests_in_progress);
            drawer.openDrawer(GravityCompat.START);
            sharedPreferences.edit().putBoolean("first_start", false).apply();
        }
        else
        {
            //проверка через savedInstanceState необходима для того, чтобы фрагменты не дублировались
            //для фрагментов, которые работают с сетью, данная проверка не применяется для сохранения большего количества возможностей для запросов
            String flag = sharedPreferences.getString("flag", "first_fragment");
            if (savedInstanceState == null && flag!=null) {
                if (flag.equals("fourth_fragment")) {
                    displaySelectedScreen(R.id.nav_data_about_graduates);
                } else if(flag.equals("fifth_fragment")){
                    displaySelectedScreen(R.id.nav_tech_support);
                } else if(flag.equals("sixth_fragment")){
                    displaySelectedScreen(R.id.nav_agreement);
                }
            }
        }

        //установка ФИО в headerView и обработка нажатия
        headerView=navigationView.getHeaderView(0);
        FullName=headerView.findViewById(R.id.full_name);
        imageOfAccount=headerView.findViewById(R.id.imageView);
        headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Main.this, MyProfile.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        sharedPreferences=getSharedPreferences(Variable.APP_PREFERENCES, MODE_PRIVATE);
        //установка ФИО и картинки в header
        fullname=sharedPreferences.getString("shared_surname","")+" "+
                 sharedPreferences.getString("shared_name", "")+" "+
                 sharedPreferences.getString("shared_middlename", "");
        FullName.setText(fullname);

        //получение количества запросов
        getCountOfRequest();

        //установка фотографии
        if (!sharedPreferences.getString("shared_name_of_photo", "without_photo").equals("without_photo")) {
            String path = null;
            try {
                path = Encryption.decrypt(sharedPreferences.getString("shared_id",""));
            } catch (Exception e) {
                e.printStackTrace();
            }
            String name_of_file = sharedPreferences.getString("shared_name_of_photo","");
            String url = Variable.place_of_photo_url + path + "/" + name_of_file;
            Picasso.get()
                    .load(url)
                    .placeholder(R.drawable.ic_account)
                    .error(R.drawable.ic_account)
                    .into(imageOfAccount);
        }
        else
        {
            imageOfAccount.setImageResource(R.drawable.ic_account);
        }

        //проверка фрагментов, которые делают постоянные запросы к базе данных
        String flag = sharedPreferences.getString("flag", "first_fragment");
        if(flag!=null) {
            if (flag.equals("first_fragment")) {
                displaySelectedScreen(R.id.nav_requests_in_progress);
            } else if (flag.equals("second_fragment")) {
                displaySelectedScreen(R.id.nav_requests_personal);
            } else if (flag.equals("third_fragment")) {
                displaySelectedScreen(R.id.nav_requests_for_all);
            }
        }

        //проверка количества нажатий на кнопки
        SharedPreferences preferencesNotification = getSharedPreferences(Variable.APP_NOTIFICATIONS, MODE_PRIVATE);
        if(!preferencesNotification.getBoolean("compare_versions",false))
        {
            if(getSupportFragmentManager().findFragmentByTag("update_version_dialog")==null)
            {
                DialogUpdate dialogUpdate = new DialogUpdate();
                dialogUpdate.setCancelable(false);
                dialogUpdate.show(getSupportFragmentManager(), "update_version_dialog");
            }
        }

        int count_of_click = preferencesNotification.getInt("count_of_click", Variable.MIN_COUNT_OF_CLICK);
        if(count_of_click==Variable.COUNT_OF_CLICK)
        {
            if(getSupportFragmentManager().findFragmentByTag("estimate_dialog")==null)
            {
                DialogEstimate dialogEstimate = new DialogEstimate();
                dialogEstimate.setCancelable(false);
                dialogEstimate.show(getSupportFragmentManager(), "estimate_dialog");
            }
        }
        else if(count_of_click>Variable.COUNT_OF_CLICK && count_of_click<Variable.MAX_COUNT_OF_CLICK)
        {
            preferencesNotification.edit().putInt("count_of_click", Variable.MIN_COUNT_OF_CLICK).apply();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent;
        switch (item.getItemId())
        {
            case R.id.menuAboutApp:
                intent = new Intent(Main.this, Browser.class);
                intent.putExtra("url_phone", Variable.about_application_phone_url);
                intent.putExtra("url_tablet", Variable.about_application_tablet_url);
                startActivity(intent);
                break;
            case R.id.menuConfidence:
                intent = new Intent(Main.this, Browser.class);
                intent.putExtra("url_phone", Variable.document_phone_url);
                intent.putExtra("url_tablet", Variable.document_tablet_url);
                startActivity(intent);
                break;
            case R.id.menuDeleteProfile:
                DialogDeleteProfile dialogDeleteProfile=new DialogDeleteProfile();
                dialogDeleteProfile.show(getSupportFragmentManager(), "delete_profile_dialog");
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setCountOfRequests(String countOfRequestsForAll, String countOfRequestsPersonal, String countOfRequestsInProgress)
    {
        tvRequestsForAll.setGravity(Gravity.CENTER);
        tvRequestsForAll.setTypeface(null, Typeface.BOLD);
        tvRequestsForAll.setTextColor(ContextCompat.getColor(this, R.color.color_for_text));
        if(!countOfRequestsForAll.equals("0")) {
            tvRequestsForAll.setText(countOfRequestsForAll);
        }
        else
        {
            tvRequestsForAll.setText("");
        }

        tvRequestsPersonal.setGravity(Gravity.CENTER);
        tvRequestsPersonal.setTypeface(null, Typeface.BOLD);
        tvRequestsPersonal.setTextColor(ContextCompat.getColor(this, R.color.color_for_text));
        if(!countOfRequestsPersonal.equals("0")) {
            tvRequestsPersonal.setText(countOfRequestsPersonal);
        }
        else
        {
            tvRequestsPersonal.setText("");
        }

        tvRequestsInProgress.setGravity(Gravity.CENTER);
        tvRequestsInProgress.setTypeface(null, Typeface.BOLD);
        tvRequestsInProgress.setTextColor(ContextCompat.getColor(this, R.color.color_for_text));
        if(!countOfRequestsInProgress.equals("0")) {
            tvRequestsInProgress.setText(countOfRequestsInProgress);
        }
        else
        {
            tvRequestsInProgress.setText("");
        }
    }

    private String createURL()
    {
        sharedPreferences= getSharedPreferences(Variable.APP_PREFERENCES, Context.MODE_PRIVATE);
        String id_of_specialist= null;
        try {
            id_of_specialist = Encryption.decrypt(sharedPreferences.getString("shared_id",""));
        } catch (Exception e) {
            e.printStackTrace();
        }
        String type_of_specialist=sharedPreferences.getString("shared_type_of_specialist","").replace(" ", "%20");
        String subject_of_country=sharedPreferences.getString("shared_subject", "").replace(" ", "%20");
        String access_token=sharedPreferences.getString("shared_access_token","");
        return Variable.get_requests_main_url+id_of_specialist+
               Variable.get_requests_first_add_url+type_of_specialist+
               Variable.get_requests_second_add_url+subject_of_country+
               Variable.get_requests_third_add_url+access_token;
    }

    private void getCountOfRequest()
    {
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, createURL(), parameter_for_request,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int countOfRequestsForAll=0;
                            int countOfRequestsPersonal=0;
                            int countOfRequestsInProgress=0;
                            String id_of_specialist, status;
                            JSONArray jsonArray=response.getJSONArray("data");
                            for(int i=0;i<jsonArray.length(); i++)
                            {
                                JSONObject JO=jsonArray.getJSONObject(i);
                                id_of_specialist=JO.getString("id_of_specialist");
                                status=JO.getString("status");
                                if(id_of_specialist.equals("0"))
                                {
                                    countOfRequestsForAll++;
                                }
                                else
                                {
                                    if(status.equals("1"))
                                    {
                                        countOfRequestsPersonal++;
                                    }
                                    else
                                    {
                                        countOfRequestsInProgress++;
                                    }
                                }
                            }
                            setCountOfRequests(String.valueOf(countOfRequestsForAll),
                                               String.valueOf(countOfRequestsPersonal),
                                               String.valueOf(countOfRequestsInProgress));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });
        MySingleton.getInstance(Main.this).addToRequestque(jsonObjectRequest);
    }


    public void StatusOfBusyRequest(final String status)
    {
        if (checkInternetConnection.isNetworkConnected())
        {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Variable.status_of_busy_url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONArray jsonArray = new JSONArray(response);
                                JSONObject jsonObject = jsonArray.getJSONObject(0);
                                String code = jsonObject.getString("code");
                                if (code.equals("status_of_busy_success"))
                                {
                                    SharedPreferences.Editor editor=sharedPreferences.edit();
                                    editor.putString("shared_status_of_busy", status);
                                    editor.apply();
                                }
                                else
                                {
                                    displayAlert("code", jsonObject.getString("title"), jsonObject.getString("message"));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    sharedPreferences=getSharedPreferences(Variable.APP_PREFERENCES, MODE_PRIVATE);
                    Map<String, String> params = new HashMap<String, String>();
                    try {
                        params.put("id", Encryption.decrypt(sharedPreferences.getString("shared_id","")));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    params.put("status_of_busy", status);
                    params.put("access_token",sharedPreferences.getString("shared_access_token",""));
                    return params;
                }
            };
            MySingleton.getInstance(Main.this).addToRequestque(stringRequest);
        }
        else
        {
            displayAlert("code", getResources().getString(R.string.error_connection), getResources().getString(R.string.check_connection));
        }
    }


    @Override
    public void onBackPressed()
    {
        if(drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
    }

    private void displaySelectedScreen(int id)
    {
        if(id!=R.id.nav_exit)
        {
            Fragment fragment=getSupportFragmentManager().findFragmentById(id);
            if(fragment==null) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                if (id == R.id.nav_requests_in_progress) {
                    fragmentManager.beginTransaction().replace(R.id.container, new FragmentRequestsInProgress()).commit();
                } else if (id == R.id.nav_requests_personal) {
                    fragmentManager.beginTransaction().replace(R.id.container, new FragmentRequestsPersonal()).commit();
                } else if (id == R.id.nav_requests_for_all) {
                    fragmentManager.beginTransaction().replace(R.id.container, new FragmentRequestsForAll()).commit();
                } else if (id == R.id.nav_data_about_graduates) {
                    fragmentManager.beginTransaction().replace(R.id.container, new FragmentDataSearchGraduate()).commit();
                } else if (id == R.id.nav_tech_support){
                    fragmentManager.beginTransaction().replace(R.id.container, new FragmentTechSupport()).commit();
                } else if (id == R.id.nav_agreement){
                    fragmentManager.beginTransaction().replace(R.id.container, new FragmentAgreement()).commit();
                }
            }
        }
        else
        {
            DialogQuit dialogQuit=new DialogQuit();
            dialogQuit.setCancelable(false);
            dialogQuit.show(getSupportFragmentManager(), "quit_dialog");
        }
    }

    @Override
    public void displayAlert(String code, String title, String message)
    {
        DialogSimple dialog=new DialogSimple();
        DialogSimple.title=title;
        DialogSimple.message=message;
        dialog.show(getSupportFragmentManager(), "status_of_busy_dialog");
    }
}
