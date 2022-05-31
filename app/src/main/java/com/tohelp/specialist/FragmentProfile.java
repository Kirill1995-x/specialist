package com.tohelp.specialist;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tohelp.specialist.lists.ContactsDataAboutGraduates;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FragmentProfile extends Fragment {

    @BindView(R.id.tvFullname)
    TextView tvFullname;
    @BindView(R.id.tvDateOfBirth)
    TextView tvDateOfBirth;
    @BindView(R.id.tvSex)
    TextView tvSex;
    @BindView(R.id.tvPhone)
    TextView tvPhone;
    @BindView(R.id.tvEmail)
    TextView tvEmail;
    @BindView(R.id.tvNameOfChildHome)
    TextView tvNameOfChildHome;
    @BindView(R.id.tvSubject)
    TextView tvSubjectOfCountry;
    @BindView(R.id.tvCity)
    TextView tvCity;
    @BindView(R.id.tvRegistrationAddress)
    TextView tvRegistrationAddress;
    @BindView(R.id.tvFactualAddress)
    TextView tvFactualAddress;
    @BindView(R.id.tvTypeOfFlat)
    TextView tvTypeOfFlat;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v=inflater.inflate(R.layout.fragment_profile, container, false);

        //настройка ButterKnife
        ButterKnife.bind(this, v);

        ContactsDataAboutGraduates contactsDataAboutGraduates =
                requireActivity().getIntent().getParcelableExtra("data_about_graduates");

        if(contactsDataAboutGraduates!=null)
        {
            tvFullname.setText(contactsDataAboutGraduates.getFullname());
            tvDateOfBirth.setText(contactsDataAboutGraduates.getFullDateOfBorn());
            tvSex.setText(contactsDataAboutGraduates.getSex());
            tvPhone.setText(contactsDataAboutGraduates.getPhoneNumber());
            tvEmail.setText(contactsDataAboutGraduates.getEmail());
            tvNameOfChildHome.setText(contactsDataAboutGraduates.getChildHome());
            tvSubjectOfCountry.setText(contactsDataAboutGraduates.getSubjectOfCountry());
            tvCity.setText(contactsDataAboutGraduates.getCity());
            tvRegistrationAddress.setText(contactsDataAboutGraduates.getRegistrationAddress());
            tvFactualAddress.setText(contactsDataAboutGraduates.getFactualAddress());
            tvTypeOfFlat.setText(contactsDataAboutGraduates.getTypeOfFlat());
        }

        return v;
    }

}
