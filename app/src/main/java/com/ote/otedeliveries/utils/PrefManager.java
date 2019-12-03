package com.ote.otedeliveries.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.ote.otedeliveries.models.Login;

public class PrefManager {
    private static SharedPreferences sharedPreferences;

    private static final String LOGIN_MODEL = "login_model";
    private static final String NEW_VERSION = "new_version";
    private static final String ACCOUNT_VERIFIED = "account_verified";
    private static final String PROFILE_PICTURE = "profile_picture";

    // login model
    public static Login getLoginModel(){
        Login login = new Login();
        login.setLoggedin(false);
        login.setLoggedinBefore(false);
        login.setUsername(null);

        return new Gson().fromJson(sharedPreferences.getString(LOGIN_MODEL, new Gson().toJson(login)), Login.class);
    }

    public static void setLoginModel(Login loginModel){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(LOGIN_MODEL, new Gson().toJson(loginModel));
        editor.apply();
    }

    // profile picture
    public static String getProfilePicture(){
        return sharedPreferences.getString(PROFILE_PICTURE, null);
    }

    public static void setProfilePicture(String profilePicture){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PROFILE_PICTURE, profilePicture);
        editor.apply();
    }

    // verified account
    public static boolean getAccountVerified(){
        return sharedPreferences.getBoolean(ACCOUNT_VERIFIED, false);
    }

    public static void setAccountVerified(boolean accountVerified){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(ACCOUNT_VERIFIED, accountVerified);
        editor.apply();
    }

    // new version
    public static boolean getNewVersion(){
        return sharedPreferences.getBoolean(NEW_VERSION, false);
    }

    public static void setNewVersion(boolean newVersion){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(NEW_VERSION, newVersion);
        editor.apply();
    }

    public PrefManager(Context context){
        sharedPreferences = context.getSharedPreferences("gated", 0);
    }
}