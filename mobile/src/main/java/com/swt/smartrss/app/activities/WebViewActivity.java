package com.swt.smartrss.app.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import com.swt.smartrss.app.R;

/**
 * Created by Florian on 16.06.2015.
 */
public class WebViewActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        Intent i = getIntent();
        String url = i.getStringExtra("url");

        WebView webView = (WebView)findViewById(R.id.webView2);
        webView.loadUrl(url);
    }

}
