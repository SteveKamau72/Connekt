package com.connekt;

import android.app.Application;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

/**
 * Created by steve on 2/12/18.
 */

public class AppController extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(new ConnectivityChangeReceiver(), intentFilter);
    }
}
