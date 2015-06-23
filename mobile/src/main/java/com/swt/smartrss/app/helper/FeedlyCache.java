package com.swt.smartrss.app.helper;

import android.content.Context;
import com.swt.smartrss.app.GlobalApplication;
import com.swt.smartrss.app.interfaces.FeedlyEventInterface;
import org.feedlyapi.FeedManager;
import org.feedlyapi.model.Article;
import org.feedlyapi.model.Stream;
import org.feedlyapi.model.requests.MarkArticlesAsReadRequest;
import org.feedlyapi.retrofit.FeedlyApiProvider;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Dropsoft on 17.06.2015.
 */
public class FeedlyCache {
    private ArrayList<FeedlyEventInterface> mListeners;
    private Context mContext;
    private List<Article> mArticles;

    public FeedlyCache(Context context) {
        this.mContext = context;
        this.mListeners = new ArrayList<FeedlyEventInterface>();
        this.mArticles = new ArrayList<Article>();
    }

    public void markArticleAsRead(String id) {
        final Article article = getArticleById(id);
        if (article != null) {
            StateManager stateManager = ((GlobalApplication) mContext).getStateManager();
            String accessToken = stateManager.getAndroidPreferences().getFeedlyToken();
            FeedlyApiProvider.setAccessToken(accessToken);

            FeedlyApiProvider.getApi().markArticlesAsReadAsync(new MarkArticlesAsReadRequest(Arrays.asList(article.getId())), new Callback<Response>() {
                @Override
                public void success(Response response, Response response2) {
                    article.setUnread(false);
                    triggerOnSuccess();
                }

                @Override
                public void failure(RetrofitError retrofitError) {
                    //TODO handle callback
                }
            });
        }
    }

    public Article getArticleById(String id) {
        for (Article article : mArticles) {
            if (article.getId().equals(id))
                return article;
        }
        return null;
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

    public List<Article> getArticles() {
        return mArticles;
    }

    public void setArticles(List<Article> articles) {
        this.mArticles.clear();
        this.mArticles.addAll(articles);
    }

    public void getNewArticles() {
        //TODO improve
        StateManager stateManager = ((GlobalApplication) mContext).getStateManager();
        String accessToken = stateManager.getAndroidPreferences().getFeedlyToken();
        FeedlyApiProvider.setAccessToken(accessToken);
        FeedManager feedManager = new FeedManager(FeedlyApiProvider.getApi());
        feedManager.getLatestArticles(10, new Callback<Stream>() {
            @Override
            public void success(Stream stream, Response response) {
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
