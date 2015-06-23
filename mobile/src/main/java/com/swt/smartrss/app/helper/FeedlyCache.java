package com.swt.smartrss.app.helper;

import android.content.Context;
import com.swt.smartrss.app.GlobalApplication;
import com.swt.smartrss.app.interfaces.FeedlyEventInterface;
import org.feedlyapi.FeedManager;
import org.feedlyapi.model.Article;
import org.feedlyapi.model.Stream;
import org.feedlyapi.retrofit.FeedlyApiProvider;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dropsoft on 17.06.2015.
 */
public class FeedlyCache {
    private ArrayList<FeedlyEventInterface> mListeners;
    private Context mContext;
    private String mAccessToken;
    private FeedManager mFeedManager;
    private List<Article> mArticles;

    private String streamContinuation;

    public FeedlyCache(Context context) {
        this.mContext = context;
        this.mListeners = new ArrayList<FeedlyEventInterface>();
        this.mArticles = new ArrayList<Article>();
        this.mFeedManager = new FeedManager(FeedlyApiProvider.getApi());
    }

    private void verifyToken() {
        if (mAccessToken != null && !mAccessToken.isEmpty())
            return;

        StateManager stateManager = ((GlobalApplication) mContext).getStateManager();
        this.mAccessToken = stateManager.getAndroidPreferences().getFeedlyToken();
        FeedlyApiProvider.setAccessToken(this.mAccessToken);
    }

    //TODO implement removeListener
    public void addListener(FeedlyEventInterface listener) {
        mListeners.add(listener);
    }

    public void triggerOnSuccess() {
        for (FeedlyEventInterface feedlyEventInterface : mListeners) {
            feedlyEventInterface.success();
        }
    }

    public void triggerOnFailure() {
        for (FeedlyEventInterface feedlyEventInterface : mListeners) {
            feedlyEventInterface.failure();
        }
    }

    public void addArticles(List<Article> articles) {
        mArticles.addAll(articles);
    }

    public List<Article> getArticles() {
        return mArticles;
    }

    public void setArticles(List<Article> articles) {
        this.mArticles.clear();
        this.mArticles.addAll(articles);
    }

    public void refreshArticles(boolean continueArticles) {
        if (continueArticles == false) {
            refreshArticles();
        } else {
            verifyToken();
            mFeedManager.getLatestArticles(10, streamContinuation, new Callback<Stream>() {
                @Override
                public void success(Stream stream, Response response) {
                    streamContinuation = stream.getContinuation();
                    addArticles(stream.getItems());
                    triggerOnSuccess();
                }

                @Override
                public void failure(RetrofitError retrofitError) {
                    retrofitError.printStackTrace();
                    triggerOnFailure();
                }
            });
        }
    }

    public void refreshArticles() {
        verifyToken();
        streamContinuation = null;

        mFeedManager.getLatestArticles(10, new Callback<Stream>() {
            @Override
            public void success(Stream stream, Response response) {
                streamContinuation = stream.getContinuation();
                setArticles(stream.getItems());
                triggerOnSuccess();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                retrofitError.printStackTrace();
                triggerOnFailure();
            }
        });
    }
}
