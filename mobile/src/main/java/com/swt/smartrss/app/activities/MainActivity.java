package com.swt.smartrss.app.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.swt.smartrss.app.GlobalApplication;
import com.swt.smartrss.app.R;
import com.swt.smartrss.app.adapters.ListAdapter;
import com.swt.smartrss.app.helper.ArticleData;
import com.swt.smartrss.app.helper.FeedlyCache;
import com.swt.smartrss.app.helper.StateManager;
import com.swt.smartrss.app.interfaces.FeedlyEventInterface;
import org.feedlyapi.model.Article;
import org.feedlyapi.retrofit.FeedlyApiProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is the first activity you see when opening the app.
 * Currently it shows a list of the newest articles on feedly.
 *
 * @author Florian Lüdiger
 * @author Oleg Kriegel
 */
public class MainActivity extends Activity {
    private String accountName;
    private FeedlyCache feedlyCache;

    /**
     * This method gets called when the activity is created.
     * @param savedInstanceState gets passed to the super function
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StateManager stateManager = ((GlobalApplication) getApplication()).getStateManager();
        feedlyCache = stateManager.getFeedlyCache();
        final String token = stateManager.getAndroidPreferences().getFeedlyToken();

        if (token == null || token.isEmpty()) {
            requestLogin();
            return;
        }

        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                feedlyCache.refreshArticles();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        final ListView listView = (ListView) findViewById(R.id.listView);

        //putting a dummy ArticleData object in the list saying "loading"
        final ArrayList<ArticleData> list = new ArrayList<ArticleData>();
        ArticleData loadingDummy = new ArticleData();
        loadingDummy.setTitle("loading");
        list.add(loadingDummy);

        final ListAdapter adapter = new ListAdapter(this, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);

        //handling click events on the ListView items
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ArticleData current = adapter.getItem(position);
                feedlyCache.markArticleAsRead(current.getId());

                Intent i = new Intent(getApplicationContext(), ReaderActivity.class);
                i.putExtra("title", current.getTitle());
                i.putExtra("url", current.getUrl());
                i.putExtra("text", current.getText());
                i.putExtra("picUrl", current.getPictureUrl());
                startActivity(i);
            }
        });

        //fetching articles using the FeedlyCache Class from the Feedly API
        feedlyCache.addListener(new FeedlyEventInterface() {
            @Override
            public void success() {
                List<Article> articles = feedlyCache.getArticles();
                list.clear();
                for (Article a : articles) {
                    list.add(new ArticleData(a));
                }
                adapter.notifyDataSetChanged();
            }

            //putting an error ArticleData dummy into the list
            @Override
            public void failure() {
                list.clear();
                ArticleData errorDummy = new ArticleData();
                errorDummy.setTitle("Error loading articles");
                list.add(errorDummy);
                adapter.notifyDataSetChanged();
            }
        });
        feedlyCache.refreshArticles();
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
        } else if (id == R.id.action_logout) {
            StateManager stateManager = ((GlobalApplication) getApplication()).getStateManager();
            stateManager.getAndroidPreferences().setFeedlyToken(null);
            FeedlyApiProvider.setAccessToken(null);
            requestLogin();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
