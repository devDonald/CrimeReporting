package com.example.donald.crimereporting;

import android.app.Application;

import com.firebase.client.Firebase;

/**
 * Created by donald on 9/21/17.
 */

public class CrimesReporting extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Firebase.setAndroidContext(this);
    }
}
