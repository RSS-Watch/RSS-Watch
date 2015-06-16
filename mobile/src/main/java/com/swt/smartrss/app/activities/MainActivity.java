package com.swt.smartrss.app.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.swt.smartrss.app.GlobalApplication;
import com.swt.smartrss.app.R;
import com.swt.smartrss.app.helper.ArticleData;
import com.swt.smartrss.app.helper.StateManager;
import com.swt.smartrss.app.adapters.ListAdapter;
import org.feedlyapi.FeedManager;
import org.feedlyapi.model.Article;
import org.feedlyapi.model.Stream;
import org.feedlyapi.retrofit.FeedlyApiProvider;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {
    private String accountName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StateManager stateManager = ((GlobalApplication) getApplication()).getStateManager();
        final String token = stateManager.getAndroidPreferences().getFeedlyToken();

        if(token == null || token.isEmpty()) {
            requestLogin();
            return;
        }

        final ListView listView = (ListView) findViewById(R.id.listView);

        final ArrayList<ArticleData> list = new ArrayList<ArticleData>();
        ArticleData loadingDummy = new ArticleData();
        loadingDummy.setTitle("loading");
        list.add(loadingDummy);

        final ListAdapter adapter = new ListAdapter(this, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getApplicationContext(), ReaderActivity.class);
                ArticleData current = adapter.getItem(position);
                i.putExtra("title", current.getTitle());
                i.putExtra("text",current.getText());
                i.putExtra("picUrl",current.getPictureUrl());
                startActivity(i);
            }
        });

        final String accessToken = stateManager.getAndroidPreferences().getFeedlyToken();
        FeedlyApiProvider.setAccessToken(accessToken);
        FeedManager feedManager = new FeedManager(FeedlyApiProvider.getApi());
        feedManager.getLatestArticles(10, new Callback<Stream>() {
            @Override
            public void success(Stream stream, Response response) {
                List<Article> articles = stream.getItems();
                list.clear();
                for(Article a : articles) {
                    list.add(new ArticleData(a));
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                list.clear();
                ArticleData errorDummy = new ArticleData();
                errorDummy.setTitle("Error loading articles");
                retrofitError.printStackTrace();
                list.add(errorDummy);
                adapter.notifyDataSetChanged();
            }
        });




    }

    private void requestLogin() {
        Uri uri = Uri.parse("http://sandbox.feedly.com/v3/auth/auth?response_type=code&client_id=sandbox&redirect_uri=http://localhost&scope=https://cloud.feedly.com/subscriptions");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this, PreferencesActivity.class);
            startActivity(intent);
            return true;
        } else if(id == R.id.action_logout) {
            StateManager stateManager = ((GlobalApplication) getApplication()).getStateManager();
            stateManager.getAndroidPreferences().setFeedlyToken(null);
            FeedlyApiProvider.setAccessToken(null);
            requestLogin();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
