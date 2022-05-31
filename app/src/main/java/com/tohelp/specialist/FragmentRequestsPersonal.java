package com.tohelp.specialist;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.tohelp.specialist.lists.ContactsGraduate;
import com.tohelp.specialist.lists.ContactsGraduateAdapter;
import com.tohelp.specialist.settings.CheckInternetConnection;
import com.tohelp.specialist.settings.MySingleton;
import com.tohelp.specialist.settings.Variable;
import com.tohelp.specialist.settings.Encryption;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;


public class FragmentRequestsPersonal extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.srlRequestForSpecialist)
    SwipeRefreshLayout srlRequestForSpecialist;
    @BindView(R.id.RequestForSpecialist)
    ListView list_of_requests_personal;
    @BindView(R.id.progressBarFragmentRequestsPersonal)
    ProgressBar progressBar;
    @BindView(R.id.tvTryRequest)
    TextView tvTryRequest;
    @BindView(R.id.viewFailedInternetConnection)
    View viewFailedInternetConnection;
    @BindView(R.id.rlRequestsPersonal)
    RelativeLayout rlRequestsPersonal;
    SharedPreferences sharedPreferences;
    ContactsGraduateAdapter contactsGraduateAdapter;
    String parameter_for_request=null;
    String id, surname_of_user, name_of_user, middlename_of_user, phone_of_user,
    email_of_user, city_of_user, message_of_user, type_of_request, time_sent_user, date_sent_user;
    CheckInternetConnection checkInternetConnection;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v=inflater.inflate(R.layout.fragment_requests_personal, container, false);

        //настройка ButterKnife
        ButterKnife.bind(this, v);

        sharedPreferences= requireActivity().getSharedPreferences(Variable.APP_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("flag", "second_fragment");
        editor.putString("city_of_graduate", "");
        editor.putString("organization_of_graduate", "");
        editor.apply();

        //создание адаптера
        contactsGraduateAdapter = new ContactsGraduateAdapter(requireActivity());

        //проверка Интернета
        checkInternetConnection=new CheckInternetConnection(getActivity());

        if(!checkInternetConnection.isNetworkConnected())
        {
            rlRequestsPersonal.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            viewFailedInternetConnection.setVisibility(View.VISIBLE);
        }
        else
        {
            if (savedInstanceState == null)
            {
               request_specialist();
            }
            else
            {
                Parcelable[] request_array = savedInstanceState.getParcelableArray("request");
                if (request_array != null)
                {
                    if (request_array.length == 0)
                    {
                        request_specialist();
                    }
                    else
                    {
                        rlRequestsPersonal.setVisibility(View.VISIBLE);
                        viewFailedInternetConnection.setVisibility(View.GONE);
                        for (int i = 0; i < request_array.length; i++)
                        {
                            contactsGraduateAdapter.add((ContactsGraduate) request_array[i]);
                        }
                    }
                }
            }
        }

        list_of_requests_personal.setAdapter(contactsGraduateAdapter);
        list_of_requests_personal.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ContactsGraduate contactsGraduate=contactsGraduateAdapter.getItem(position);
                Intent intent=new Intent(getActivity(), RequestsPersonal.class);
                intent.putExtra("requests_personal", contactsGraduate);
                startActivity(intent);
            }
        });

        //обработка нажатия на элемент
        tvTryRequest.setOnClickListener(this);

        //обновление списка
        srlRequestForSpecialist.setOnRefreshListener(this);

        return v;
    }

    @Override
    public void onRefresh() {
        if(checkInternetConnection.isNetworkConnected())
        {
            srlRequestForSpecialist.setRefreshing(false);
            request_specialist();
        }
        else
        {
            srlRequestForSpecialist.setRefreshing(false);
            rlRequestsPersonal.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            viewFailedInternetConnection.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.tvTryRequest:
                if(checkInternetConnection.isNetworkConnected()) request_specialist();
                break;
            default:
                break;
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState)
    {
        super.onSaveInstanceState(outState);

        ContactsGraduate[]request_array=new ContactsGraduate[contactsGraduateAdapter.getCount()];
        for(int i=0; i<contactsGraduateAdapter.getCount();i++)
        {
            request_array[i]=contactsGraduateAdapter.getItem(i);
        }
        outState.putParcelableArray("request_for_specialist", request_array);
    }

    private String createURL()
    {
        String id= null;
        try {
            id = Encryption.decrypt(sharedPreferences.getString("shared_id",""));
        } catch (Exception e) {
            e.printStackTrace();
        }
        String access_token = sharedPreferences.getString("shared_access_token","");
        return Variable.requests_personal_url_main+id+Variable.requests_personal_url_add+access_token;
    }

    private void request_specialist()
    {
        contactsGraduateAdapter.clear();
        progressBar.setVisibility(View.VISIBLE);
        rlRequestsPersonal.setVisibility(View.VISIBLE);
        viewFailedInternetConnection.setVisibility(View.GONE);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, createURL(), parameter_for_request,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject JO = jsonArray.getJSONObject(i);
                                id = JO.getString("id");
                                surname_of_user = JO.getString("surname_of_user");
                                name_of_user = JO.getString("name_of_user");
                                middlename_of_user = JO.getString("middlename_of_user");
                                phone_of_user = JO.getString("phone_of_user");
                                email_of_user = JO.getString("email_of_user");
                                city_of_user = JO.getString("city");
                                message_of_user = JO.getString("message_of_user");
                                type_of_request = JO.getString("type_of_request");
                                time_sent_user = JO.getString("time_sent_user");
                                date_sent_user = JO.getString("date_sent_user");
                                ContactsGraduate contactsGraduate = new ContactsGraduate(id, surname_of_user, name_of_user, middlename_of_user,
                                        phone_of_user, email_of_user, message_of_user, type_of_request, city_of_user, time_sent_user, date_sent_user);
                                contactsGraduateAdapter.add(contactsGraduate);
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
