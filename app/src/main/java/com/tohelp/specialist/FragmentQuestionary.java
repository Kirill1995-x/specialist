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

public class FragmentQuestionary extends Fragment {

    @BindView(R.id.tvDateOfLastQuestionary)
    TextView tvDateOfLastQuestionary;
    @BindView(R.id.tvMainTargets)
    TextView tvMainTarget;
    @BindView(R.id.tvProblemEducation)
    TextView tvProblemEducation;
    @BindView(R.id.tvProblemFlat)
    TextView tvProblemFlat;
    @BindView(R.id.tvProblemMoney)
    TextView tvProblemMoney;
    @BindView(R.id.tvProblemLaw)
    TextView tvProblemLaw;
    @BindView(R.id.tvOtherProblems)
    TextView tvProblemOther;
    @BindView(R.id.tvNameOfEducationalInstitution)
    TextView tvNameEducationInstitution;
    @BindView(R.id.tvLevelOfEducation)
    TextView tvLevelOfEducation;
    @BindView(R.id.tvMyProfessional)
    TextView tvMyProfessional;
    @BindView(R.id.tvMyInterests)
    TextView tvMyInterests;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v=inflater.inflate(R.layout.fragment_questionary, container, false);

        //настройка ButterKnife
        ButterKnife.bind(this, v);

        ContactsDataAboutGraduates contactsDataAboutGraduates =
                requireActivity().getIntent().getParcelableExtra("data_about_graduates");

        if(contactsDataAboutGraduates!=null)
        {
            tvDateOfLastQuestionary.setText(contactsDataAboutGraduates.getDateOfLastQuestionary());
            tvMainTarget.setText(contactsDataAboutGraduates.getMainTarget());
            tvProblemEducation.setText(contactsDataAboutGraduates.getProblemEducation());
            tvProblemFlat.setText(contactsDataAboutGraduates.getProblemFlat());
            tvProblemMoney.setText(contactsDataAboutGraduates.getProblemMoney());
            tvProblemLaw.setText(contactsDataAboutGraduates.getProblemLaw());
            tvProblemOther.setText(contactsDataAboutGraduates.getProblemOther());
            tvNameEducationInstitution.setText(contactsDataAboutGraduates.getNameEducationInstitution());
            tvLevelOfEducation.setText(contactsDataAboutGraduates.getLevelOfEducation());
            tvMyProfessional.setText(contactsDataAboutGraduates.getMyProfessional());
            tvMyInterests.setText(contactsDataAboutGraduates.getMyInterests());
        }

        return v;
    }
}
