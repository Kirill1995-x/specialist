package com.tohelp.specialist;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.tohelp.specialist.dialogs.DialogSearchSettings;
import com.tohelp.specialist.dialogs.DialogSimple;
import com.tohelp.specialist.lists.ContactsDataAboutGraduates;
import com.tohelp.specialist.lists.ContactsDataAboutGraduatesAdapter;
import com.tohelp.specialist.settings.CheckInternetConnection;
import com.tohelp.specialist.settings.MySingleton;
import com.tohelp.specialist.settings.Variable;
import com.tohelp.specialist.settings.Encryption;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;
import butterknife.BindView;
import butterknife.ButterKnife;

public class FragmentDataSearchGraduate extends Fragment implements View.OnClickListener, com.tohelp.specialist.interfaces.displayAlert {

    @BindView(R.id.etSearchGraduate)
    EditText etSearchGraduate;
    @BindView(R.id.imgSettings)
    ImageView imgSettings;
    @BindView(R.id.imgSearch)
    ImageView imgSearch;
    @BindView(R.id.lvGraduates)
    ListView lvGraduates;
    @BindView(R.id.tvNotResults)
    TextView tvNotResults;
    @BindView(R.id.progressBarFragmentSearchGraduate)
    ProgressBar progressBar;
    SharedPreferences sharedPreferences;
    String id_of_user, surname_of_user, name_of_user, middlename_of_user, child_home, email,
            phone_number, city, subject_of_country, registration_address, factual_address,
            type_of_flat, sex, date_of_born, month_of_born, year_of_born, name_of_photo;
    String main_target, problem_education, problem_flat, problem_money, problem_law, problem_other,
            name_education_institution, level_of_education, my_professional, my_interests,
            date_of_last_questionary;
    ContactsDataAboutGraduatesAdapter contactsDataAboutGraduatesAdapter;
    String parameter_for_request=null;
    boolean flag = false;//флаг для исключения ситуации, когда никаких запросов не совершалось, но при этом savedInstanceState не равен null

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v=inflater.inflate(R.layout.fragment_data_search_graduate, container, false);

        //настройка ButterKnife
        ButterKnife.bind(this, v);

        sharedPreferences= requireActivity().getSharedPreferences(Variable.APP_PREFERENCES, Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("flag", "fourth_fragment").apply();

        //создание адаптера
        contactsDataAboutGraduatesAdapter = new ContactsDataAboutGraduatesAdapter(requireActivity());

        if(savedInstanceState!=null)
        {
           etSearchGraduate.setText(savedInstanceState.getString("graduate"));
            //проверка ProgressBar
            if(savedInstanceState.getBoolean("showProgressBar"))
            {
                progressBar.setVisibility(View.VISIBLE);
            }
            //---
            Parcelable[] data_about_graduates_array = savedInstanceState.getParcelableArray("data_about_graduates");
            flag = savedInstanceState.getBoolean("flag",false);
            if (data_about_graduates_array != null && flag)
            {
                if(data_about_graduates_array.length==0)
                {
                    lvGraduates.setVisibility(View.GONE);
                    tvNotResults.setVisibility(View.VISIBLE);
                }
                else {
                    for (int i = 0; i < data_about_graduates_array.length; i++) {
                        contactsDataAboutGraduatesAdapter.add((ContactsDataAboutGraduates) data_about_graduates_array[i]);
                    }
                    lvGraduates.setVisibility(View.VISIBLE);
                    tvNotResults.setVisibility(View.GONE);
                }
            }
        }

        lvGraduates.setAdapter(contactsDataAboutGraduatesAdapter);
        lvGraduates.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ContactsDataAboutGraduates contactsDataAboutGraduates=contactsDataAboutGraduatesAdapter.getItem(position);
                Intent intent=new Intent(getActivity(), RequestDataAboutGraduates.class);
                intent.putExtra("data_about_graduates", contactsDataAboutGraduates);
                startActivity(intent);
            }
        });

        //обработка нажатия на элемент
        imgSearch.setOnClickListener(this);
        imgSettings.setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.imgSearch:
                searchGraduate();
                break;
            case R.id.imgSettings:
                DialogSearchSettings dialogSearchSettings = new DialogSearchSettings();
                dialogSearchSettings.show(requireActivity().getSupportFragmentManager(), "settings_dialog");
                break;
            default:
                break;
        }
    }

    private String createURL()
    {
        String id = null;
        try {
            id = Encryption.decrypt(sharedPreferences.getString("shared_id",""));
        } catch (Exception e) {
            e.printStackTrace();
        }
        String access_token = sharedPreferences.getString("shared_access_token","");
        String graduate = Objects.requireNonNull(etSearchGraduate.getText()).toString().trim()
                                    .replace(" ","%20");

        String subject_of_country = Objects.requireNonNull(sharedPreferences.getString("shared_subject", ""))
                                    .replace(" ", "%20");
        String city = Objects.requireNonNull(sharedPreferences.getString("city_of_graduate", ""))
                                    .replace(" ", "%20");
        String child_home = Objects.requireNonNull(sharedPreferences.getString("organization_of_graduate", ""))
                                    .replace(" ", "%20");
        return Variable.list_of_graduates_url_main+id+
                Variable.list_of_graduates_url_first_add+access_token+
                Variable.list_of_graduates_url_second_add+graduate+
                Variable.list_of_graduates_url_third_add+subject_of_country+
                Variable.list_of_graduates_url_fourth_add+city+
                Variable.list_of_graduates_url_fifth_add+child_home;
    }

    private void searchGraduate()
    {
        flag=true;
        InputMethodManager inputMethodManager=(InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        Objects.requireNonNull(inputMethodManager).hideSoftInputFromWindow(imgSearch.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        if(!new CheckInternetConnection(getActivity()).isNetworkConnected())
        {
            displayAlert("internet_connection_failed",
                    getResources().getString(R.string.error_connection),
                    getResources().getString(R.string.check_connection));
        }
        else
        {
            contactsDataAboutGraduatesAdapter.clear();
            progressBar.setVisibility(View.VISIBLE);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, createURL(), parameter_for_request,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONArray jsonArray = response.getJSONArray("data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject JO = jsonArray.getJSONObject(i);
                                    id_of_user = JO.getString("id_of_user");
                                    surname_of_user = JO.getString("surname");
                                    name_of_user = JO.getString("name");
                                    middlename_of_user = JO.getString("middlename");
                                    child_home = JO.getString("child_home");
                                    email = JO.getString("email");
                                    phone_number = JO.getString("phone_number");
                                    city = JO.getString("city");
                                    subject_of_country = JO.getString("subject_of_country");
                                    registration_address = JO.getString("registration_address");
                                    factual_address = JO.getString("factual_address");
                                    type_of_flat = JO.getString("type_of_flat");
                                    sex = JO.getString("sex");
                                    date_of_born = JO.getString("date_of_born");
                                    month_of_born = JO.getString("month_of_born");
                                    year_of_born = JO.getString("year_of_born");
                                    main_target = JO.getString("main_target");
                                    problem_education = JO.getString("problem_education");
                                    problem_flat = JO.getString("problem_flat");
                                    problem_money = JO.getString("problem_money");
                                    problem_law = JO.getString("problem_law");
                                    problem_other = JO.getString("problem_other");
                                    name_education_institution = JO.getString("name_education_institution");
                                    level_of_education = JO.getString("level_of_education");
                                    my_professional = JO.getString("my_professional");
                                    my_interests = JO.getString("my_interests");
                                    date_of_last_questionary = JO.getString("date_of_last_questionary");
                                    name_of_photo = JO.getString("name_of_photo");
                                    ContactsDataAboutGraduates contactsDataAboutGraduates = new ContactsDataAboutGraduates(id_of_user, surname_of_user, name_of_user, middlename_of_user,
                                            child_home, email, phone_number, city, subject_of_country, registration_address, factual_address, type_of_flat, sex, date_of_born, month_of_born, year_of_born,
                                            main_target, problem_education, problem_flat, problem_money, problem_law, problem_other, name_education_institution, level_of_education,
                                            my_professional, my_interests, date_of_last_questionary, name_of_photo);
                                    contactsDataAboutGraduatesAdapter.add(contactsDataAboutGraduates);
                                }
                                if(jsonArray.length()==0)
                                {
                                    lvGraduates.setVisibility(View.GONE);
                                    tvNotResults.setVisibility(View.VISIBLE);
                                }
                                else
                                {
                                    lvGraduates.setVisibility(View.VISIBLE);
                                    tvNotResults.setVisibility(View.GONE);
                                }
                                progressBar.setVisibility(View.GONE);
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
                    });
            MySingleton.getInstance(getActivity()).addToRequestque(jsonObjectRequest);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState)
    {
        outState.putString("graduate", Objects.requireNonNull(etSearchGraduate.getText()).toString().trim());
        ContactsDataAboutGraduates[]contacts_data_about_graduates=
                new ContactsDataAboutGraduates[contactsDataAboutGraduatesAdapter.getCount()];
        for(int i=0; i<contactsDataAboutGraduatesAdapter.getCount();i++)
        {
            contacts_data_about_graduates[i]=contactsDataAboutGraduatesAdapter.getItem(i);
        }
        outState.putParcelableArray("data_about_graduates", contacts_data_about_graduates);
        outState.putBoolean("showProgressBar", progressBar.isShown());
        outState.putBoolean("flag", flag);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void displayAlert (String code, String title, String message)
    {
        DialogSimple dialog=new DialogSimple();
        DialogSimple.title=title;
        DialogSimple.message=message;
        dialog.show(requireActivity().getSupportFragmentManager(), "searching_dialog");
    }

}
