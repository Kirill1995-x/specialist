package com.tohelp.specialist.lists;

import android.os.Parcel;
import android.os.Parcelable;

import com.tohelp.specialist.settings.Variable;

public class ContactsDataAboutGraduates implements Parcelable
{
    private String id_of_user, surname_of_user, name_of_user, middlename_of_user, child_home, email, phone_number, city, subject_of_country,
            registration_address, factual_address, type_of_flat, sex, date_of_born, month_of_born, year_of_born;
    private String main_target, problem_education, problem_flat, problem_money, problem_law, problem_other,
            name_education_institution, level_of_education, my_professional, my_interests, date_of_last_questionary, name_of_photo;

    public ContactsDataAboutGraduates(String id_of_user, String surname_of_user, String name_of_user, String middlename_of_user,
                                      String child_home, String email, String phone_number, String city, String subject_of_country,
                                      String registration_address, String factual_address, String type_of_flat, String sex,
                                      String date_of_born, String month_of_born, String year_of_born,
                                      String main_target, String problem_education, String problem_flat, String problem_money,
                                      String problem_law, String problem_other, String name_education_institution,
                                      String level_of_education, String my_professional, String my_interests,
                                      String date_of_last_questionary, String name_of_photo) {
        this.id_of_user = id_of_user;
        this.surname_of_user = surname_of_user;
        this.name_of_user = name_of_user;
        this.middlename_of_user = middlename_of_user;
        this.child_home = child_home;
        this.email = email;
        this.phone_number=phone_number;
        this.city = city;
        this.subject_of_country = subject_of_country;
        this.registration_address = registration_address;
        this.factual_address = factual_address;
        this.type_of_flat = type_of_flat;
        this.sex = sex;
        this.date_of_born = date_of_born;
        this.month_of_born = month_of_born;
        this.year_of_born = year_of_born;
        this.main_target = main_target;
        this.problem_education = problem_education;
        this.problem_flat = problem_flat;
        this.problem_money = problem_money;
        this.problem_law = problem_law;
        this.problem_other = problem_other;
        this.name_education_institution = name_education_institution;
        this.level_of_education = level_of_education;
        this.my_professional = my_professional;
        this.my_interests = my_interests;
        this.date_of_last_questionary=date_of_last_questionary;
        this.name_of_photo=name_of_photo;
    }

    public ContactsDataAboutGraduates(Parcel in) {
        String[] data=new String[28];
        in.readStringArray(data);
        id_of_user = data[0];
        surname_of_user = data[1];
        name_of_user = data[2];
        middlename_of_user = data[3];
        child_home = data[4];
        email = data[5];
        phone_number = data[6];
        city = data[7];
        subject_of_country = data[8];
        registration_address = data[9];
        factual_address = data[10];
        type_of_flat = data[11];
        sex = data[12];
        date_of_born = data[13];
        month_of_born = data[14];
        year_of_born = data[15];
        main_target = data[16];
        problem_education = data[17];
        problem_flat = data[18];
        problem_money = data[19];
        problem_law = data[20];
        problem_other = data[21];
        name_education_institution = data[22];
        level_of_education = data[23];
        my_professional = data[24];
        my_interests = data[25];
        date_of_last_questionary = data[26];
        name_of_photo = data[27];
    }

    public String getIdOfUser() {
        return id_of_user;
    }

    public String getDateOfLastQuestionary() {
        return (date_of_last_questionary.isEmpty())?"Анкетирование еще не было пройдено":date_of_last_questionary;
    }

    public String getSurnameOfUser() {
        return ((!surname_of_user.isEmpty())?surname_of_user:"");
    }

    public String getNameOfUser() {
        return ((!name_of_user.isEmpty())?name_of_user:"");
    }

    public String getMiddlenameOfUser() {
        return ((!middlename_of_user.isEmpty())?middlename_of_user:"");
    }

    public String getFullname()
    {
        return getSurnameOfUser()+" "+getNameOfUser()+" "+getMiddlenameOfUser();
    }

    public String getChildHome() {
        return ((!child_home.isEmpty())?child_home:"");
    }

    public String getEmail() {
        return ((!email.isEmpty())?email:"");
    }

    public String getPhoneNumber()
    {
        return ((!phone_number.isEmpty())?("+7"+phone_number):"");
    }

    public String getCity() {
        return ((!city.isEmpty())?city:"");
    }

    public String getSubjectOfCountry() {
        return ((!subject_of_country.isEmpty())?subject_of_country:"");
    }

    public String getRegistrationAddress() {
        return ((!registration_address.isEmpty())?registration_address:"");
    }

    public String getFactualAddress() {
        return ((!factual_address.isEmpty())?factual_address:"");
    }

    public String getTypeOfFlat() {
        return ((!type_of_flat.isEmpty())?type_of_flat:"");
    }

    public String getSex() {
        return ((!sex.isEmpty())?sex:"");
    }

    public String getDateOfBorn() {
        return ((!date_of_born.isEmpty())?date_of_born:"");
    }

    public String getMonthOfBorn() {
        return ((!month_of_born.isEmpty())?month_of_born:"");
    }

    public String getYearOfBorn() {
        return ((!year_of_born.isEmpty())?year_of_born:"");
    }

    public String getFullDateOfBorn()
    {
        return getDateOfBorn()+" "+getMonthOfBorn()+" "+getYearOfBorn();
    }

    public String getMainTarget() {
        return ((!main_target.isEmpty())?main_target:"");
    }

    public String getProblemEducation() {
        return ((!problem_education.isEmpty())?problem_education:"");
    }

    public String getProblemFlat() {
        return ((!problem_flat.isEmpty())?problem_flat:"");
    }

    public String getProblemMoney() {
        return ((!problem_money.isEmpty())?problem_money:"");
    }

    public String getProblemLaw() {
        return ((!problem_law.isEmpty())?problem_law:"");
    }

    public String getProblemOther() {
        return ((!problem_other.isEmpty())?problem_other:"");
    }

    public String getNameEducationInstitution() {
        return ((!name_education_institution.isEmpty())?name_education_institution:"");
    }

    public String getLevelOfEducation() {
        return ((!level_of_education.isEmpty())?level_of_education:"");
    }

    public String getMyProfessional() {
        return ((!my_professional.isEmpty())?my_professional:"");
    }

    public String getMyInterests() {
        return ((!my_interests.isEmpty())?my_interests:"");
    }

    public String getNameOfPhoto() {
        return name_of_photo;
    }

    public String getUrlOfPhoto()
    {
        return (getNameOfPhoto().equals("without_photo"))?null: Variable.photo_of_user_url + getIdOfUser() +"/"+getNameOfPhoto();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{id_of_user, surname_of_user, name_of_user, middlename_of_user, child_home, email, phone_number,
                city, subject_of_country, registration_address, factual_address, type_of_flat, sex, date_of_born, month_of_born, year_of_born,
                main_target, problem_education, problem_flat, problem_money, problem_law, problem_other, name_education_institution,
                level_of_education, my_professional, my_interests, date_of_last_questionary, name_of_photo});
    }

    public static final Creator<ContactsDataAboutGraduates> CREATOR = new Creator<ContactsDataAboutGraduates>() {
        @Override
        public ContactsDataAboutGraduates createFromParcel(Parcel in) {
            return new ContactsDataAboutGraduates(in);
        }

        @Override
        public ContactsDataAboutGraduates[] newArray(int size) {
            return new ContactsDataAboutGraduates[size];
        }
    };
}
