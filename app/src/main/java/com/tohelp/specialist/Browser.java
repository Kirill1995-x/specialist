package com.tohelp.specialist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tohelp.specialist.prepare.LoadingHTML;

import java.util.Objects;
import butterknife.BindView;
import butterknife.ButterKnife;

public class Browser extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.webViewBrowser)
    WebView webView;
    @BindView(R.id.tvTryRequest)
    TextView tvTryRequest;
    @BindView(R.id.progressBarBrowser)
    ProgressBar progressBar;
    @BindView(R.id.viewFailedInternetConnection)
    View viewFailedInternetConnection;
    LoadingHTML loadingHTML;
    Bundle url_to_document;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);

        //настройка ButterKnife
        ButterKnife.bind(this);

        //получение ссылок
        url_to_document  = getIntent().getExtras();
        if(url_to_document!=null) {
            loadingHTML = new LoadingHTML(webView, viewFailedInternetConnection, progressBar,
                    url_to_document.getString("url_phone"), url_to_document.getString("url_tablet"), this);
        }

        //добавление стрелки "Вверх"
        ActionBar actionBar=getSupportActionBar();
        Objects.requireNonNull(actionBar).setDisplayHomeAsUpEnabled(true);

        //загрузка html страницы
        loadingHTML.LoadHTML();

        //обработка нажатия на элемент
        tvTryRequest.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.tvTryRequest:
                loadingHTML.LoadHTMLForTryRequest();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}