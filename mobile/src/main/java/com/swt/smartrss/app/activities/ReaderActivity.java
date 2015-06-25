package com.swt.smartrss.app.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import com.swt.smartrss.app.R;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class ReaderActivity extends Activity {

    private String title;
    private String text;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);
        TextView textViewTitle = (TextView) findViewById(R.id.textViewTitle);
        ImageView imageViewTop = (ImageView) findViewById(R.id.imageViewTop);

        WebView webView = (WebView) findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Intent i = new Intent(getApplicationContext(), WebViewActivity.class);
                i.putExtra("url", url);
                startActivity(i);
                return true;
            }
        });
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);

        Intent i = getIntent();
        title = i.getStringExtra("title");
        url = i.getStringExtra("url");
        text = i.getStringExtra("text");
        String picUrl = i.getStringExtra("picUrl");

        Document doc = Jsoup.parse(text);
        for (Element element : doc.select("img,iframe")) {
            element.remove();
        }

        text = doc.html().toString();

        textViewTitle.setText(title);
        webView.loadDataWithBaseURL("", text, "text/html", "UTF-8", "");
        webView.setScrollContainer(false);

        if (picUrl != "" && picUrl != null) {
            Picasso.with(getApplicationContext()).load(picUrl)
                    .error(android.R.drawable.ic_delete).into(imageViewTop);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_reader, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_share) {
            shareArticle();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void shareArticle() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, title + '\n' + url);
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.share_article)));
    }
}
