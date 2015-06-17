package com.swt.smartrss.wear.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.support.wearable.view.WearableListView;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.*;
import com.swt.smartrss.core.Shared;
import com.swt.smartrss.core.helper.ObjectSerializer;
import com.swt.smartrss.core.models.ArticleDataModel;
import com.swt.smartrss.core.models.ArticleRequestModel;
import com.swt.smartrss.wear.R;
import com.swt.smartrss.wear.adapters.ListAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements DataApi.DataListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = MainActivity.class.getName();
    private ListAdapter mListAdapter;
    private WearableListView mlistView;

    //communication
    private Node mNode;
    private GoogleApiClient mGoogleApiClient;
    private boolean mResolveError = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mListAdapter = new ListAdapter(getApplicationContext(), new ArrayList<ArticleDataModel>());
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mlistView = (WearableListView) findViewById(R.id.list);
                mlistView.setClickListener(new WearableListView.ClickListener() {
                    @Override
                    public void onClick(WearableListView.ViewHolder viewHolder) {
                        openArticle("id123123123");
                    }

                    @Override
                    public void onTopEmptyRegionClick() {

                    }
                });
                mlistView.setAdapter(mListAdapter);
            }
        });
    }

    //region Communication
    private void sendMessage(String path, byte[] data) {
        if (mNode != null && mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            Wearable.MessageApi.sendMessage(
                    mGoogleApiClient, mNode.getId(), path, data).setResultCallback(
                    new ResultCallback<MessageApi.SendMessageResult>() {
                        @Override
                        public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                            if (!sendMessageResult.getStatus().isSuccess()) {
                                Log.e(TAG, "Failed to send message: " + sendMessageResult.getStatus().getStatusCode());
                            }
                        }
                    }
            );
        } else {
            Log.e(TAG, "Device is not connected");
        }
    }

    private void openArticle(String articleID) {
        ArticleRequestModel articleRequestModel = new ArticleRequestModel(articleID);
        try {
            sendMessage(Shared.URI_OPEN_ARTICLE, ObjectSerializer.serialize(articleRequestModel));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void requestArticles() {
        sendMessage(Shared.URI_REQUEST_ARTICLE_LIST, null);
    }

    @Override
    protected void onResume() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Wearable.DataApi.removeListener(mGoogleApiClient, this);
        mGoogleApiClient.disconnect();
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (!mResolveError) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Wearable.DataApi.addListener(mGoogleApiClient, this);
        Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
            @Override
            public void onResult(NodeApi.GetConnectedNodesResult nodes) {
                for (Node node : nodes.getNodes()) {
                    mNode = node;
                }
                requestArticles();
            }
        });
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        mResolveError = true;
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer) {
        Log.d(TAG, "onDataChanged");
        for (DataEvent dataEvent : dataEventBuffer) {
            if (dataEvent.getType() == DataEvent.TYPE_CHANGED) {
                DataItem item = dataEvent.getDataItem();
                if (item.getUri().getPath().compareTo(Shared.URI_ARTICLE) == 0) {
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                    processRawArticle(dataMap.getByteArray(Shared.KEY_DATA));
                }
            }
        }
    }

    private void processRawArticle(byte[] data) {
        try {
            Log.d(TAG, "processRawArticle");
            final List<ArticleDataModel> articleList = (List<ArticleDataModel>) ObjectSerializer.deserialize(data);
            for (ArticleDataModel articleDataModel : articleList) {
                Log.d(TAG, "feed title: " + articleDataModel.title);
            }
            MainActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    mListAdapter.setList(articleList);
                    mListAdapter.notifyDataSetChanged();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    //endregion
}