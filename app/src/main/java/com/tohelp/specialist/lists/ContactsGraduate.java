package com.tohelp.specialist.lists;

import android.os.Parcel;
import android.os.Parcelable;

import com.tohelp.specialist.R;

public class ContactsGraduate implements Parcelable
{
    private String id;
    private String surname_of_user;
    private String name_of_user;
    private String middlename_of_user;
    private String phone_of_user;
    private String email_of_user;
    private String type_of_request;
    private String message_of_user;
    private String city;
    private String time_sent_user;
    private String date_sent_user;

    public ContactsGraduate(String id, String surname_of_user, String name_of_user, String middlename_of_user,
                            String phone_of_user, String email_of_user, String message_of_user, String type_of_request,
                            String city, String time_sent_user, String date_sent_user)
    {
        this.id=id;
        this.surname_of_user = surname_of_user;
        this.name_of_user = name_of_user;
        this.middlename_of_user = middlename_of_user;
        this.phone_of_user = phone_of_user;
        this.email_of_user = email_of_user;
        this.type_of_request = type_of_request;
        this.message_of_user = message_of_user;
        this.city = city;
        this.time_sent_user = time_sent_user;
        this.date_sent_user = date_sent_user;
    }

    public ContactsGraduate(Parcel in)
    {
        String[] data=new String[11];
        in.readStringArray(data);
        id=data[0];
        surname_of_user=data[1];
        name_of_user=data[2];
        middlename_of_user=data[3];
        phone_of_user=data[4];
        email_of_user=data[5];
        message_of_user=data[6];
        type_of_request=data[7];
        city=data[8];
        time_sent_user=data[9];
        date_sent_user=data[10];
    }

    public String getId()
    {
        return ((!id.isEmpty())?id:"");
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

    public String getFullnameOfUser()
    {
        return getSurnameOfUser()+" "+getNameOfUser()+" "+getMiddlenameOfUser();
    }

    public String getPhoneOfUser() {
        return ((!phone_of_user.isEmpty())?("+7"+phone_of_user):"");
    }

    public int getRequest()
    {
        switch (type_of_request)
        {
            case "1":
                return R.string.psychologist_request;
            case "2":
                return R.string.lawyer_request;
            case "3":
                return R.string.household_request;
            case "4":
                return R.string.health_request;
            case "5":
                return R.string.education_request;
            case "6":
                return R.string.finance_request;
            case "7":
                return R.string.other_request;
            default:
                return R.string.error_request;
        }
    }

    public String getEmailOfUser() {
        return ((!email_of_user.isEmpty())?email_of_user:"");
    }

    public String getMessageOfUser() {
        return ((!message_of_user.isEmpty())?message_of_user:"");
    }

    public String getCity() {
        return ((!city.isEmpty())?city:"");
    }

    public String getTimeSentUser() {
        return ((!time_sent_user.isEmpty())?time_sent_user:"");
    }

    public String getDateSentUser() {
        return ((!date_sent_user.isEmpty())?date_sent_user:"");
    }

    public String getTimeAndDateSentUser()
    {
        return  "Время: "+getTimeSentUser()+" Дата: "+getDateSentUser();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeStringArray(new String[]{id, surname_of_user, name_of_user, middlename_of_user,
                                           phone_of_user, email_of_user,message_of_user, type_of_request,
                                           city, time_sent_user, date_sent_user});
    }

    public static final Creator<ContactsGraduate> CREATOR = new Creator<ContactsGraduate>() {
        @Override
        public ContactsGraduate createFromParcel(Parcel in) {
            return new ContactsGraduate(in);
        }

        @Override
        public ContactsGraduate[] newArray(int size) {
            return new ContactsGraduate[size];
        }
    };
}
