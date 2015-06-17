package com.swt.smartrss.app.services;

import android.os.Bundle;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.*;
import com.swt.smartrss.core.Shared;
import com.swt.smartrss.core.helper.ObjectSerializer;
import com.swt.smartrss.core.models.ArticleDataModel;
import com.swt.smartrss.core.models.ArticleRequestModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Dropsoft on 17.06.2015.
 */
public class DataListenerService extends WearableListenerService implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = DataListenerService.class.getName();

    private GoogleApiClient mGoogleApiClient;

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d(TAG, "onMessageReceived " + messageEvent.getPath());
        if (messageEvent.getPath().equals(Shared.URI_REQUEST_ARTICLE_LIST)) {
            syncArticles();
        } else if (messageEvent.getPath().equals(Shared.URI_OPEN_ARTICLE)) {
            try {
                ArticleRequestModel requestModel = (ArticleRequestModel) ObjectSerializer.deserialize(messageEvent.getData());
                if (requestModel != null) {
                    Log.d(TAG, "requestModel " + requestModel.id);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
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
    }

    private void syncArticles() {
        Log.d(TAG, "syncArticles");
        List<ArticleDataModel> articleDataModel = new ArrayList<ArticleDataModel>();
        for (int i = 1; i <= 10; i++)
            articleDataModel.add(new ArticleDataModel("title phone" + i, "text"));

        PutDataMapRequest putDataMapRequest = PutDataMapRequest.create(Shared.URI_ARTICLE);
        try {
            DataMap dataMap = putDataMapRequest.getDataMap();
            dataMap.putByteArray(Shared.KEY_DATA, ObjectSerializer.serialize(articleDataModel));
            dataMap.putLong(Shared.KEY_TIME, new Date().getTime());
        } catch (IOException e) {
            e.printStackTrace();
        }
        PutDataRequest putDataRequest = putDataMapRequest.asPutDataRequest();
        Wearable.DataApi.putDataItem(mGoogleApiClient, putDataRequest);
    }

    @Override
    public void onDestroy() {
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