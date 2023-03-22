package com.codestudioapps.learing.app.app;

import android.app.Application;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseMessaging.getInstance().subscribeToTopic("offlinetutorialsnotification");
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

    }
}
