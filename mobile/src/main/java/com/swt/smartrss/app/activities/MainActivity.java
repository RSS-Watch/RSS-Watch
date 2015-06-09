package com.swt.smartrss.app.activities;

import android.app.Activity;
import android.content.Intent;
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
        accountName = stateManager.getAndroidPreferences().getAccountName();
        Toast.makeText(getApplication(), accountName, Toast.LENGTH_SHORT).show();

        final ListView listView = (ListView) findViewById(R.id.listView);

        final ArrayList<Article> list = new ArrayList<Article>();
        list.add(new Article());

        final ListAdapter adapter = new ListAdapter(this,android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getApplicationContext(), ReaderActivity.class);
                Article current = adapter.getItem(position);
                i.putExtra("title", adapter.getItem(position).getTitle());
                if(current.getContent() != null && !current.getContent().isEmpty())
                    i.putExtra("text",adapter.getItem(position).getContent());
                else
                    i.putExtra("text",adapter.getItem(position).getSummary());
                startActivity(i);
            }
        });

        FeedManager feedManager = new FeedManager(FeedlyApiProvider.getApi(stateManager.getAndroidPreferences().getFeedlyToken()));
        feedManager.getLatestArticles(10, new Callback<Stream>() {
            @Override
            public void success(Stream stream, Response response) {
                List<Article> articles = stream.getItems();
                list.clear();
                list.addAll(articles);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
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
