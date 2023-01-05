package com.example.light;

import static java.lang.Long.parseLong;

import android.app.Application;
import android.text.format.DateFormat;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class application extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
    }

    public static final String formatTimestamp(String timestamp){
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(parseLong(timestamp));
        String date = DateFormat.format("dd/MM/yyyy",calendar).toString();
        return date;
    }
}
