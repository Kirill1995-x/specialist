package com.tohelp.specialist.settings;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class MySingleton
{
    private static MySingleton mInstance;
    private RequestQueue requestQueue;

    private MySingleton(Context context)
    {
        requestQueue=getRequestQueue(context);
    }

    public RequestQueue getRequestQueue(Context context)
    {
        if (requestQueue==null)
        {
            requestQueue= Volley.newRequestQueue(context.getApplicationContext());
        }
        return requestQueue;
    }

    public static synchronized MySingleton getInstance(Context context)
    {
        if (mInstance==null)
        {
            mInstance=new MySingleton(context);
        }
        return mInstance;
    }

    public <T> void addToRequestque(Request<T> request)
    {
        requestQueue.add(request);
    }
}
