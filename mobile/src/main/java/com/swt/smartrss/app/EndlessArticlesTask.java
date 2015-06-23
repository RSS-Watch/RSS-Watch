package com.swt.smartrss.app;

import android.os.AsyncTask;
import android.widget.ListView;
import com.swt.smartrss.app.helper.FeedlyCache;
import org.feedlyapi.model.Feed;

public class EndlessArticlesTask extends AsyncTask<Integer, Void, Void> {

    private FeedlyCache feedlyCache;

    public EndlessArticlesTask(FeedlyCache feedlyCache) {
        this.feedlyCache = feedlyCache;
    }

    @Override
    protected Void doInBackground(Integer... integers) {
        feedlyCache.refreshArticles(true);
        return null;
    }
}