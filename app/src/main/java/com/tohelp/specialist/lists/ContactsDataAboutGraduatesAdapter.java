package com.tohelp.specialist.lists;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.squareup.picasso.Picasso;
import com.tohelp.specialist.R;

import java.util.ArrayList;
import java.util.Objects;
import de.hdodenhof.circleimageview.CircleImageView;

public class ContactsDataAboutGraduatesAdapter extends ArrayAdapter<ContactsDataAboutGraduates>
{
    ArrayList<ContactsDataAboutGraduates> contactsDataAboutGraduatesArrayList;

    public ContactsDataAboutGraduatesAdapter(@NonNull Context context)
    {
        super(context, R.layout.row_layout);
        contactsDataAboutGraduatesArrayList = new ArrayList<ContactsDataAboutGraduates>();
    }

    @Override
    public void add(@Nullable ContactsDataAboutGraduates object) {
        super.add(object);
        contactsDataAboutGraduatesArrayList.add(object);
    }

    @Override
    public void clear() {
        super.clear();
        contactsDataAboutGraduatesArrayList.clear();
    }

    @Override
    public int getCount() {
        return contactsDataAboutGraduatesArrayList.size();
    }

    @Nullable
    @Override
    public ContactsDataAboutGraduates getItem(int position) {
        return contactsDataAboutGraduatesArrayList.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View row=convertView;
        ContactsDataAboutGraduatesHolder contactsDataAboutGraduatesHolder;
        if(row==null)
        {
            LayoutInflater layoutInflater=(LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row= Objects.requireNonNull(layoutInflater).inflate(R.layout.row_data_about_graduates, parent,false);
            contactsDataAboutGraduatesHolder=new ContactsDataAboutGraduatesHolder();
            contactsDataAboutGraduatesHolder.tx_fullname=row.findViewById(R.id.fullname_of_graduate);
            contactsDataAboutGraduatesHolder.tx_city=row.findViewById(R.id.city_of_graduate);
            contactsDataAboutGraduatesHolder.img_profile=row.findViewById(R.id.imageOfProfile);
            row.setTag(contactsDataAboutGraduatesHolder);
        }
        else
        {
            contactsDataAboutGraduatesHolder=(ContactsDataAboutGraduatesHolder) row.getTag();
        }
        ContactsDataAboutGraduates contactsDataAboutGraduates=(ContactsDataAboutGraduates) this.getItem(position);
        contactsDataAboutGraduatesHolder.tx_fullname.setText(Objects.requireNonNull(contactsDataAboutGraduates).getFullname());
        contactsDataAboutGraduatesHolder.tx_city.setText(contactsDataAboutGraduates.getCity());
        Picasso.get()
                .load(contactsDataAboutGraduates.getUrlOfPhoto())
                .placeholder(R.drawable.ic_account)
                .error(R.drawable.ic_account)
                .into(contactsDataAboutGraduatesHolder.img_profile);

        return row;
    }

    static class ContactsDataAboutGraduatesHolder
    {
        TextView tx_fullname, tx_city;
        CircleImageView img_profile;
    }
}
