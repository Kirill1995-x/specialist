package com.tohelp.specialist.prepare;

import android.content.Context;
import android.content.SharedPreferences;

import com.tohelp.specialist.R;
import com.tohelp.specialist.settings.Variable;

public class FindItemInSpinner
{
    Context context;
    String[] array_work_hours;
    String[] array_work_minutes;

    public FindItemInSpinner(Context context)
    {
        this.context = context;
        array_work_hours = context.getResources().getStringArray(R.array.array_hours);
        array_work_minutes = context.getResources().getStringArray(R.array.array_minutes);
    }

    private int getIntegerStartWorkHours(String start_hours)
    {
        for (int i = 0; i<array_work_hours.length; i++)
        {
            if(start_hours.equals(array_work_hours[i]))
            {
                return i;
            }
        }
        return 0;
    }

    private int getIntegerStartWorkMinutes(String start_minutes)
    {
        for (int i = 0; i<array_work_minutes.length; i++)
        {
            if(start_minutes.equals(array_work_minutes[i]))
            {
                return i;
            }
        }
        return 0;
    }

    private int getIntegerEndWorkHours(String end_hours)
    {
        for (int i = 0; i<array_work_hours.length; i++)
        {
            if(end_hours.equals(array_work_hours[i]))
            {
                return i;
            }
        }
        return 0;
    }

    private int getIntegerEndWorkMinutes(String end_minutes)
    {
        for (int i = 0; i<array_work_minutes.length; i++)
        {
            if(end_minutes.equals(array_work_minutes[i]))
            {
                return i;
            }
        }
        return 0;
    }

    //установка начала и конца рабочего дня
    public int[] EstimateWorkHours()
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Variable.APP_PREFERENCES, Context.MODE_PRIVATE);
        String[] main_array=sharedPreferences.getString("shared_call_hours", "").split("-");
        String start_hours = "08";
        String start_minutes = "00";
        String end_hours = "17";
        String end_minutes = "00";

        if(main_array.length==2)
        {
            String[] first_sub_array = main_array[0].split(":");
            String[] second_sub_array = main_array[1].split(":");
            if (first_sub_array.length == 2 && second_sub_array.length == 2) {
                start_hours = first_sub_array[0];
                start_minutes = first_sub_array[1];
                end_hours = second_sub_array[0];
                end_minutes = second_sub_array[1];
            }
        }
        int[] array_work_hours = new int[4];
        array_work_hours[0] = getIntegerStartWorkHours(start_hours);
        array_work_hours[1] = getIntegerStartWorkMinutes(start_minutes);
        array_work_hours[2] = getIntegerEndWorkHours(end_hours);
        array_work_hours[3] = getIntegerEndWorkMinutes(end_minutes);
        return array_work_hours;
    }

}
