package com.swt.smartrss.app;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import com.swt.smartrss.app.helper.StateManager;
import com.swt.smartrss.app.adapters.ListAdapter;
import org.feedlyapi.FeedManager;
import org.feedlyapi.model.Article;
import org.feedlyapi.model.Stream;
import org.feedlyapi.retrofit.FeedlyApiProvider;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {
    private String accountName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StateManager stateManager = ((GlobalApplication) getApplication()).getStateManager();
        accountName = stateManager.getAndroidPreferences().getAccountName();
        Toast.makeText(getApplication(), accountName, Toast.LENGTH_SHORT).show();

        final ListView listView = (ListView) findViewById(R.id.listView);

        //final ArrayList<String> list = new ArrayList<String>();
        //list.add("loading");

        final ArrayList<Article> list = new ArrayList<Article>();
        list.add(new Article());

        //final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, list);
        final ListAdapter adapter = new ListAdapter(this,android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);

        FeedManager feedManager = new FeedManager(FeedlyApiProvider.getApi(stateManager.getAndroidPreferences().getFeedlyToken()));
        feedManager.getLatestArticles(10, new Callback<Stream>() {
            @Override
            public void success(Stream stream, Response response) {
                List<Article> articles = stream.getItems();
                list.clear();
                //for (Article a: articles) {
                //    list.add(a.getTitle());
                //}
                list.addAll(articles);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                //list.clear();
                //list.add("error");
                //adapter.notifyDataSetChanged();
                retrofitError.printStackTrace();
            }
        });




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
        }

        return super.onOptionsItemSelected(item);
    }
}
