package com.ote.otedeliveries.app;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.ote.otedeliveries.models.Login;
import com.ote.otedeliveries.utils.PrefManager;
import com.ote.otedeliveries.utils.Utils;

import java.util.Objects;

import io.realm.Realm;

public class OteApplication extends Application {
    private static OteApplication oteApplication;

    PrefManager prefManager;

    @Override
    public void onCreate() {
        super.onCreate();
        oteApplication = this;

        Realm.init(this);

        prefManager = new PrefManager(this);

        if(!PrefManager.getNewVersion()){
            Realm realm = Utils.getRealmInstance();

            realm.beginTransaction();
            realm.deleteAll();
            realm.commitTransaction();

            Login login = PrefManager.getLoginModel();
            login.setLoggedinBefore(false);
            login.setLoggedin(false);
            login.setUsername(null);

            PrefManager.setLoginModel(login);
            PrefManager.setNewVersion(true);
        }

        FirebaseApp.initializeApp(getApplicationContext());

        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.w(OteApplication.class.getCanonicalName(), "getInstanceId failed", task.getException());
                return;
            }

            // Get new Instance ID token
            String token = Objects.requireNonNull(task.getResult()).getToken();

            SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("regId", token);
            editor.apply();
        });
    }

    public static synchronized OteApplication getInstance(){
        return oteApplication;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}