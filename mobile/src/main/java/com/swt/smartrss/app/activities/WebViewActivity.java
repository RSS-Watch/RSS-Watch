package com.swt.smartrss.app.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import com.swt.smartrss.app.R;

/**
 * This activity consists of only one WebView filling the whole screen.
 * It shows up when clicking a link in the ReaderActivity.
 * This helps improving the performance because opening links in this Activity is much faster than
 * for example opening the default browser.
 *
 * @author Florian Lüdiger
 */
public class WebViewActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        Intent i = getIntent();
        String url = i.getStringExtra("url");

        WebView webView = (WebView) findViewById(R.id.webView2);
        webView.loadUrl(url);
    }

}
