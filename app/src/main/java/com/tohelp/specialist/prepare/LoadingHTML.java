package com.tohelp.specialist.prepare;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.tohelp.specialist.R;
import com.tohelp.specialist.settings.CheckInternetConnection;


public class LoadingHTML
{
    private WebView webView;
    private View failedInternetConnectionView;
    private ProgressBar progressBar;
    private String mobile_url, tablet_url;
    private Context context;
    private CheckInternetConnection checkInternetConnection;

    public LoadingHTML(WebView webView, View failedInternetConnectionView, ProgressBar progressBar, String mobile_url, String tablet_url, Context context)
    {
        this.webView = webView;
        this.failedInternetConnectionView = failedInternetConnectionView;
        this.progressBar = progressBar;
        this.mobile_url = mobile_url;
        this.tablet_url = tablet_url;
        this.context = context;
    }

    public void LoadHTML()
    {
        checkInternetConnection = new CheckInternetConnection(context);
        if(checkInternetConnection.isNetworkConnected())
        {
            webView.setVisibility(View.VISIBLE);
            failedInternetConnectionView.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);

            if (context.getResources().getBoolean(R.bool.isTablet)) {
                webView.loadUrl(tablet_url);
            } else {
                webView.loadUrl(mobile_url);
            }

            webView.setWebViewClient(new WebViewClient(){
                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    progressBar.setVisibility(View.GONE);
                    super.onPageFinished(view, url);
                }
            });
        }
        else
        {
            webView.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            failedInternetConnectionView.setVisibility(View.VISIBLE);
        }
    }

    public void LoadHTMLForTryRequest()
    {
        checkInternetConnection = new CheckInternetConnection(context);

        if(checkInternetConnection.isNetworkConnected())
        {
            webView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            failedInternetConnectionView.setVisibility(View.GONE);

            if (context.getResources().getBoolean(R.bool.isTablet)) {
                webView.loadUrl(tablet_url);
            } else {
                webView.loadUrl(mobile_url);
            }

            webView.setWebViewClient(new WebViewClient(){
                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    progressBar.setVisibility(View.GONE);
                    super.onPageFinished(view, url);
                }
            });
        }
    }


}
