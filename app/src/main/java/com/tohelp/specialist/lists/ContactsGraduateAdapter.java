package com.tohelp.specialist.lists;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tohelp.specialist.R;

import java.util.ArrayList;
import java.util.Objects;

public class ContactsGraduateAdapter extends ArrayAdapter<ContactsGraduate>
{
    ArrayList<ContactsGraduate> contactsGraduateArrayList;

    public ContactsGraduateAdapter(@NonNull Context context) {
        super(context, R.layout.row_data_about_graduates);
        contactsGraduateArrayList = new ArrayList<ContactsGraduate>();
    }

    @Override
    public void add(@Nullable ContactsGraduate object) {
        super.add(object);
        contactsGraduateArrayList.add(object);
    }

    @Override
    public void clear() {
        super.clear();
        contactsGraduateArrayList.clear();
    }

    @Override
    public int getCount() {
        return contactsGraduateArrayList.size();
    }

    @Nullable
    @Override
    public ContactsGraduate getItem(int position) {
        return contactsGraduateArrayList.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        View row=convertView;
        ContactsGraduateHolder contactsGraduateHolder;
        if(row==null)
        {
            LayoutInflater layoutInflater=(LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row= Objects.requireNonNull(layoutInflater).inflate(R.layout.row_layout, parent,false);
            contactsGraduateHolder=new ContactsGraduateHolder();
            contactsGraduateHolder.tx_fullname=row.findViewById(R.id.tx_fullname);
            contactsGraduateHolder.tx_request=row.findViewById(R.id.tx_request);
            contactsGraduateHolder.tx_phone=row.findViewById(R.id.tx_phone);
            contactsGraduateHolder.tx_email=row.findViewById(R.id.tx_email);
            row.setTag(contactsGraduateHolder);
        }
        else
        {
            contactsGraduateHolder=(ContactsGraduateHolder)row.getTag();
        }
        ContactsGraduate contactsGraduate=(ContactsGraduate)this.getItem(position);
        contactsGraduateHolder.tx_fullname.setText(Objects.requireNonNull(contactsGraduate).getFullnameOfUser());
        contactsGraduateHolder.tx_request.setText(contactsGraduate.getRequest());
        contactsGraduateHolder.tx_phone.setText(contactsGraduate.getPhoneOfUser());
        contactsGraduateHolder.tx_email.setText(contactsGraduate.getEmailOfUser());
        return row;
    }

    static class ContactsGraduateHolder
    {
        TextView tx_fullname, tx_request, tx_phone, tx_email;
    }
}
