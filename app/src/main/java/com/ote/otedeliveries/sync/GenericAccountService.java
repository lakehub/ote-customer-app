package com.ote.otedeliveries.sync;

import android.accounts.Account;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class GenericAccountService extends Service {
    private static final String TAG = "GenericAccountService";
    public static final String ACCOUNT_TYPE = "com.ote.otedeliveries";
    public static final String ACCOUNT_NAME = "Ote";
    private Authenticator mAuthenticator;

    @Override
    public void onCreate() {
        Log.i(TAG, "Service created");
        mAuthenticator = new Authenticator(this);
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "Service destroyed");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }

    public static Account GetAccount() {
        return new Account(ACCOUNT_NAME, ACCOUNT_TYPE);
    }
}