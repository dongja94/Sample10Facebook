package com.example.dongja94.samplefacebook;

import android.app.Application;

import com.facebook.FacebookSdk;

/**
 * Created by dongja94 on 2016-03-08.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FacebookSdk.sdkInitialize(this);
    }
}
