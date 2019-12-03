package com.ote.otedeliveries.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;

import com.ote.otedeliveries.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class Utils {
    private Context context;
    private static Utils mInstance = null;


    public static Utils getInstance(Context ctx){
        if(mInstance == null){
            mInstance = new Utils(ctx.getApplicationContext());
        }

        return mInstance;
    }

    private Utils(Context context) {
        this.context = context;
    }

    public static void hideLoadingPane(LinearLayout sendingProgress, LinearLayout sendingFields) {
        sendingFields.setVisibility(View.VISIBLE);
        sendingProgress.setVisibility(View.GONE);
    }

    public static void hideContentPane(LinearLayout sendingProgress, LinearLayout sendingFields) {
        sendingFields.setVisibility(View.GONE);
        sendingProgress.setVisibility(View.VISIBLE);
    }

    public static AlertDialog getSimpleAlertDialog(Context context, String title, String content, DialogInterface.OnClickListener onOkClick) {
        return getBasicBuilderComponents(context, title, content, onOkClick).create();
    }

    public static AlertDialog getSimpleAlertDialog(Context context, String content) {
        return getSimpleAlertDialog(context, null, content);
    }

    public static AlertDialog getSimpleAlertDialog(Context context, String title, String content) {
        return getBasicBuilderComponents(context, title, content, null).create();
    }

    private static AlertDialog.Builder getBasicBuilderComponents(Context context, String title, String content, final DialogInterface
            .OnClickListener onOkClick) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (title != null && !title.isEmpty()) {
            builder.setTitle(title);
        }
        if (content != null && !content.isEmpty()) {
            builder.setMessage(content);
        }
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (onOkClick != null) {
                    onOkClick.onClick(dialog, which);
                }
                dialog.dismiss();
            }
        });
        return builder;
    }

    public static String getTimeStamp(String dateStr) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timestamp = "";

        Calendar calendar = Calendar.getInstance();
        String today = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));

        today = today.length() < 2 ? "0" + today : today;

        try{
            Date date = format.parse(dateStr);
            SimpleDateFormat todayFormat = new SimpleDateFormat("dd");
            String dateToday = todayFormat.format(date);
            format = dateToday.equals(today) ? new SimpleDateFormat("hh:mm") : new SimpleDateFormat("dd/MM/YYYY");
            String date1 = format.format(date);
            timestamp = date1;
        }
        catch(ParseException e){
            e.printStackTrace();
        }

        return timestamp;
    }

    public static String gatedUCase(String value){
        char[] array = value.toCharArray();
        array[0] = Character.toUpperCase(array[0]);

        for(int i = 1; i < array.length; i++){
            if(Character.isWhitespace(array[i - 1])){
                array[i] = Character.toUpperCase(array[i]);
            }
        }

        return new String(array);
    }

    public static String getVisitorPass() {
        String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        int count = 5;

        StringBuilder builder = new StringBuilder();

        while (count-- != 0) {
            int character = (int)(Math.random() * ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }
        return builder.toString();
    }

    public static String getFormattedDateSimple(Long dateTime) {
        SimpleDateFormat newFormat = new SimpleDateFormat("YYYY-MM-dd hh:mm:ss");
        return newFormat.format(new Date(dateTime));
    }

    public static String getCurrentTimestamp(){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateformat = new SimpleDateFormat("YYYY-MM-dd hh:mm:ss");
        return dateformat.format(calendar.getTime());
    }

    public static String getShorttenedDate(Long dateTime){
        SimpleDateFormat newFormat = new SimpleDateFormat("dd MMM yyyy");
        return newFormat.format(new Date(dateTime));
    }

    public static Realm getRealmInstance() {
        return Realm.getInstance(new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build());
    }

    public static String getInvoiceNumber() {
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

        int invoiceLength = 7;

        StringBuilder builder = new StringBuilder();
        while (invoiceLength-- != 0) {
            int character = (int)(Math.random() * alphabet.length());
            builder.append(alphabet.charAt(character));
        }

        return builder.toString();
    }
}