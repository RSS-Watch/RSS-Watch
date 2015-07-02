package com.swt.smartrss.app.services;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.*;
import com.swt.smartrss.app.GlobalApplication;
import com.swt.smartrss.app.activities.ReaderActivity;
import com.swt.smartrss.app.helper.ArticleData;
import com.swt.smartrss.app.helper.FeedlyCache;
import com.swt.smartrss.app.helper.StateManager;
import com.swt.smartrss.app.interfaces.FeedlyEventInterface;
import com.swt.smartrss.core.Shared;
import com.swt.smartrss.core.helper.ObjectSerializer;
import com.swt.smartrss.core.models.ArticleDataModel;
import com.swt.smartrss.core.models.ArticleRequestModel;
import com.swt.smartrss.core.models.ConfigurationModel;
import org.feedlyapi.model.Article;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This class receives events from other nodes, such as data changes, messages or connectivity events
 *
 * This service will be bound to and events delivered as a result of changes through the DataApi, MessageApi as well
 * as events indicating a device has connected or disconnected from the Android Wear network.
 */
public class DataListenerService extends WearableListenerService implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = DataListenerService.class.getName();
    private GoogleApiClient mGoogleApiClient;
    private FeedlyCache feedlyCache;

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d(TAG, "onMessageReceived " + messageEvent.getPath());
        //handle data by uri type
        if (messageEvent.getPath().equals(Shared.URI_READ_ARTICLE)) {
            try {
                ArticleRequestModel requestModel = (ArticleRequestModel) ObjectSerializer.deserialize(messageEvent.getData());
                if (requestModel != null) {
                    feedlyCache.markArticleAsRead(requestModel.id);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else if (messageEvent.getPath().equals(Shared.URI_REQUEST_ARTICLE_LIST)) {
            syncConfig();
            //TODO implement listener for live update
            syncArticles();
        } else if (messageEvent.getPath().equals(Shared.URI_OPEN_ARTICLE)) {
            try {
                ArticleRequestModel requestModel = (ArticleRequestModel) ObjectSerializer.deserialize(messageEvent.getData());
                if (requestModel != null) {
                    Log.d(TAG, "requestModel " + requestModel.id);

                    Article reqArticle = feedlyCache.getArticleById(requestModel.id);
                    if (reqArticle != null) {
                        ArticleData articleData = new ArticleData(reqArticle);

                        //start reader activity
                        Intent intent = new Intent(this, ReaderActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("title", articleData.getTitle());
                        intent.putExtra("text", articleData.getText());
                        intent.putExtra("picUrl", articleData.getPictureUrl());
                        startActivity(intent);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        else if (messageEvent.getPath().equals(Shared.URI_REQUEST_ARTICLE_UPDATE)) {
            feedlyCache.refreshArticles(true);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //start a connection
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Wearable.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }

        StateManager stateManager = ((GlobalApplication) getApplication()).getStateManager();
        feedlyCache = stateManager.getFeedlyCache();
        feedlyCache.addListener(new FeedlyEventInterface() {
            @Override
            public void success() {
                syncArticles();
            }

            @Override
            public void failure() {
            }
        });
    }

    private void syncItemMessage(String path, Object object) {
        Log.d(TAG, "syncItemMessage " + path);
        PutDataMapRequest putDataMapRequest = PutDataMapRequest.create(path);
        try {
            DataMap dataMap = putDataMapRequest.getDataMap();
            dataMap.putByteArray(Shared.KEY_DATA, ObjectSerializer.serialize(object));
            dataMap.putLong(Shared.KEY_TIME, new Date().getTime());
        } catch (IOException e) {
            e.printStackTrace();
        }
        PutDataRequest putDataRequest = putDataMapRequest.asPutDataRequest();
        Wearable.DataApi.putDataItem(mGoogleApiClient, putDataRequest);
    }

    private void syncConfig() {
        StateManager stateManager = ((GlobalApplication) getApplication()).getStateManager();
        Integer spritzWPM = stateManager.getAndroidPreferences().getSpritzWPM();
        Log.d(TAG, "syncConfig " + spritzWPM);
        ConfigurationModel configurationModel = new ConfigurationModel(spritzWPM);
        syncItemMessage(Shared.URI_SYNC_CONFIG, configurationModel);
    }

    private void syncArticles() {
        Log.d(TAG, "syncArticles");
        List<ArticleDataModel> articleDataModel = new ArrayList<ArticleDataModel>();
        List<Article> articles = feedlyCache.getArticles();
        if (articles.size() > 0) {
            for (Article article : articles) {
                String articleText = article.getContent();
                if (articleText == null)
                    articleText = article.getSummary();

                if (articleText == null)
                    articleText = "";

                Document document = Jsoup.parse(articleText);
                articleDataModel.add(new ArticleDataModel(article.getId(), article.getTitle(), document.text(), article.isUnread()));
            }
        } else {
            articleDataModel.add(new ArticleDataModel("", "loading", "", true));
            //TODO implement error handling
            feedlyCache.refreshArticles();
        }
        syncItemMessage(Shared.URI_ARTICLE, articleDataModel);
    }

    @Override
    public void onDestroy() {
        //close connection
        if (null != mGoogleApiClient) {
            if (mGoogleApiClient.isConnected()) {
                mGoogleApiClient.disconnect();
            }
        }
        super.onDestroy();
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}