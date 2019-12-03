package com.ote.otedeliveries.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SyncResult;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.ote.otedeliveries.app.Config;
import com.ote.otedeliveries.models.User;
import com.ote.otedeliveries.rest.ApiClient;
import com.ote.otedeliveries.rest.ApiInterface;
import com.ote.otedeliveries.rest.response.PostResponse;
import com.ote.otedeliveries.utils.ConnectionDetector;
import com.ote.otedeliveries.utils.PrefManager;
import com.ote.otedeliveries.utils.Utils;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SyncAdapter extends AbstractThreadedSyncAdapter {
    private final static String TAG = SyncAdapter.class.getSimpleName();

    SyncAdapter(Context context, boolean autoInitialize){
        super(context, autoInitialize);
    }

    SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs){
        super(context, autoInitialize, allowParallelSyncs);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        if(!ConnectionDetector.Companion.getInstance().isConnectingToInternet())
            return;

        if(!PrefManager.getLoginModel().isLoggedin())
            return;

        Realm realm = Utils.getRealmInstance();
        User user = realm.where(User.class).findFirst();

        if(user == null)
            return;

        SharedPreferences sharedPreferences = getContext().getSharedPreferences(Config.SHARED_PREF, 0);
        addUserPushNotificationID(user.getUserID(), sharedPreferences.getString("regId", null));
    }

    private void addUserPushNotificationID(String userID, String pushID){
        if(TextUtils.isEmpty(pushID))
            return;

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<PostResponse> pushNotificationAPICall = apiInterface.pushNotification(
                userID,
                pushID
        );

        pushNotificationAPICall.enqueue(new Callback<PostResponse>() {
            @Override
            public void onResponse(@NonNull Call<PostResponse> call, @NonNull Response<PostResponse> response){

                if(response.isSuccessful() && response.body() != null)
                    Log.e("push success", response.message());
                else
                    Log.e("push error", response.message());
            }

            @Override
            public void onFailure(@NonNull Call<PostResponse> call, @NonNull Throwable t){
                Log.e(SyncAdapter.class.getCanonicalName(), t.toString());
            }
        });
    }
}