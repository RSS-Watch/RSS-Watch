package com.swt.smartrss.app.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;
import com.google.gson.*;
import com.swt.smartrss.app.GlobalApplication;
import com.swt.smartrss.app.R;
import com.swt.smartrss.app.helper.StateManager;
import org.feedlyapi.model.TokenObject;
import org.feedlyapi.model.requests.TokenRequest;
import org.feedlyapi.retrofit.FeedlyInterface;
import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.android.AndroidLog;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;

import java.lang.reflect.Type;
import java.util.Calendar;


public class RedirectActivity extends Activity {

    private static GsonConverter createConverter() {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Calendar.class, new JsonDeserializer() {
            public Calendar deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(json.getAsNumber().longValue());
                return calendar;
            }
        });
        return new GsonConverter(builder.create());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redirect);
        final Activity activity = this;
        final StateManager stateManager = ((GlobalApplication) getApplication()).getStateManager();

        Intent intent = getIntent();
        final Uri redirectUri = intent.getData();
        String authCode = redirectUri.getQueryParameter("code");

        final FeedlyInterface api = getApi();

        api.getAccessTokenAsync(new TokenRequest(authCode, "sandbox", "YNXZHOH3GPYO6DF7B43K", "http://localhost"), new Callback<TokenObject>() {

            @Override
            public void success(TokenObject token, Response response) {
                if (token == null) {
                    failure(null);
                    return;
                }
                final String accessToken = token.getAccessToken();
                if (accessToken == null || accessToken.isEmpty()) {
                    failure(null);
                    return;
                }
                stateManager.getAndroidPreferences().setFeedlyToken(accessToken);
                Intent intent = new Intent(activity, MainActivity.class);
                startActivity(intent);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                onFailure(retrofitError);
            }
        });
    }

    private FeedlyInterface getApi() {
        RestAdapter adapter2 = (new RestAdapter.Builder()).setEndpoint("http://sandbox.feedly.com/v3").setRequestInterceptor(new RequestInterceptor() {
            public void intercept(RequestFacade request) {
//                request.addHeader("Authorization", "OAuth " + "");
            }
        }).setConverter(createConverter()).setLogLevel(RestAdapter.LogLevel.FULL).setLog(new AndroidLog("Retrofit")).build();
        return adapter2.create(FeedlyInterface.class);
//        return FeedlyApiProvider.getApi();
    }

    public void onFailure(RetrofitError error) {
        Toast toast = Toast.makeText(this, "Es ist ein Fehler aufgetreten.", Toast.LENGTH_LONG);
        toast.show();
    }
}
