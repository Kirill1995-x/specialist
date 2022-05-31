package com.tohelp.specialist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.tohelp.specialist.lists.ContactsGraduate;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RequestsInProgress extends AppCompatActivity {

    @BindView(R.id.tvFullnameRequestsInProgress)
    TextView tvFullname;
    @BindView(R.id.tvPhoneRequestsInProgress)
    TextView tvPhone;
    @BindView(R.id.tvEmailRequestsInProgress)
    TextView tvEmail;
    @BindView(R.id.tvCityRequestsInProgress)
    TextView tvCity;
    @BindView(R.id.tvGetRequestRequestsInProgress)
    TextView tvGetRequest;
    @BindView(R.id.tvTextOfRequestRequestsInProgress)
    TextView tvMessage;
    @BindView(R.id.tvTimeAndDateSentRequestsInProgress)
    TextView tvTimeAndDateSentRequest;
    String fullname, date_and_time, phone_of_user, email_of_user, message_of_user, city_of_user;
    int id_of_request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests_in_progress);

        //настройка ButterKnife
        ButterKnife.bind(this);

        //добавление стрелки "Вверх"
        ActionBar actionBar=getSupportActionBar();
        Objects.requireNonNull(actionBar).setDisplayHomeAsUpEnabled(true);

        ContactsGraduate contactsGraduate=(ContactsGraduate)getIntent().getParcelableExtra("requests_in_progress");

        if (contactsGraduate != null) {
            fullname=contactsGraduate.getFullnameOfUser();
            date_and_time=contactsGraduate.getTimeAndDateSentUser();
            phone_of_user=contactsGraduate.getPhoneOfUser();
            email_of_user=contactsGraduate.getEmailOfUser();
            id_of_request=contactsGraduate.getRequest();
            message_of_user=contactsGraduate.getMessageOfUser();
            city_of_user=contactsGraduate.getCity();

            tvFullname.setText(fullname);
            tvPhone.setText(phone_of_user);
            tvEmail.setText(email_of_user);
            tvGetRequest.setText(id_of_request);
            tvMessage.setText(message_of_user);
            tvCity.setText(city_of_user);
            tvTimeAndDateSentRequest.setText(date_and_time);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
