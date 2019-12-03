package com.ote.otedeliveries.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

import androidx.annotation.NonNull;

public class OteProvider extends ContentProvider {
    public static final String PROVIDER_NAME = "com.ote.otedeliveries.provider.OteProvider";

    private static final int OTES = 1;
    private static final int OTE = 2;
    private static final UriMatcher uriMatcher = getUriMatcher();

    private static UriMatcher getUriMatcher(){
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "ote", OTES);
        uriMatcher.addURI(PROVIDER_NAME, "ote/#", OTE);
        return uriMatcher;
    }

    @Override
    public String getType(@NonNull Uri uri){
        switch (uriMatcher.match(uri)){
            case OTES:
                return "vnd.android.cursor.dir/vnd.om.ote.otedeliveries.androidcontentprovider.provider.ote";
            case OTE:
                return "vnd.android.cursor.item/vnd.om.ote.otedeliveries.androidcontentprovider.provider.ote";
        }
        return "";
    }

    @Override
    public boolean onCreate(){
        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder){
        return null;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values){
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs){
        return 0;
    }
}