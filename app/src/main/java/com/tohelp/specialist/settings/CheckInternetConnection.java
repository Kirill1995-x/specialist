package com.tohelp.specialist.settings;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;



public class CheckInternetConnection
{
    Context context;

    public CheckInternetConnection(Context context)
    {
        this.context=context;
    }

    public boolean isNetworkConnected()
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager!=null)
        {
            if(android.os.Build.VERSION.SDK_INT>= Build.VERSION_CODES.Q)
            {
                NetworkCapabilities networkCapabilities=connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
                if (networkCapabilities != null)
                {
                    if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) return true;
                    else if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) return true;
                    else if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) return true;
                }
            }
            else
            {
                NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected())
                {
                    return true;
                }
            }
        }

        return false;
    }
}
