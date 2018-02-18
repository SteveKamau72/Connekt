package com.connekt;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.net.ssl.SSLContext;

/**
 * <p>
 * A broadcast receiver (receiver) is an Android component which allows you to register for system
 * or application events. All registered receivers for an event are notified by the Android runtime
 * once this event happens.
 */

public class ConnectivityChangeReceiver extends BroadcastReceiver {
    String connectionType;
    private static boolean firstConnect = true;
    SharedPreferences sharedPreferences;

    /**
     * This method is called when the BroadcastReceiver is receiving an Intent broadcast.
     **/
    @Override
    public void onReceive(final Context context, final Intent intent) {
        //initialize SharedPreferences for persistence of data
        sharedPreferences = context.getSharedPreferences("ACCOUNT", context.MODE_PRIVATE);

        //check for network connectivity
        final ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        final android.net.NetworkInfo wifi = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        final android.net.NetworkInfo mobile = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if (wifi.isConnected()) {
            connectionType = "Wifi";
            if (firstConnect) {
                firstConnect = false;
                networkRequest(context);
                updateViewOnConnectivityStatus(true, context);
                Log.e("network_______", "connected");
            }
        } else if (mobile.isConnected()) {
            connectionType = "Mobile data";
            if (firstConnect) {
                // do subroutines here
                firstConnect = false;
                networkRequest(context);
                updateViewOnConnectivityStatus(true, context);
                Log.e("network_______", "connected");
            }
        } else {
            firstConnect = true;
            Log.e("network_______", "disconnected");
            saveOfflineDateToPreferences();
            updateViewOnConnectivityStatus(false, context);
        }

    }

    /**
     * Update UI when network changes
     **/
    private void updateViewOnConnectivityStatus(final boolean isConnected, Context context) {
        if (MainActivity.getInstance() != null) {//only if view is visible
            MainActivity.getInstance().updateView(isConnected);
        }

    }

    /**
     * Initialize SSL
     *
     * @param mContext
     */
    public static void initializeSSLContext(Context mContext) {
        try {
            SSLContext.getInstance("TLSv1.2");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    /**
     * Making the request using loopj AsyncHttpClient.
     * Requires @params: imei_code, active_time, last_active_time, type
     **/
    private void networkRequest(Context context) {
        initializeSSLContext(context);
        final String url = context.getResources().getString(R.string.base_url) + "connected.php";
        final int DEFAULT_TIMEOUT = 20 * 10000;
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(DEFAULT_TIMEOUT);
        final RequestParams params = new RequestParams();
        params.put("imei_code", getDeviceIMEI(context));
        params.put("active_time", getCurrentTime());
        params.put("last_active_time", getLastActiveTime());
        params.put("type", connectionType);

        client.get(url, params, new AsyncHttpResponseHandler() {
            @Override

            public void onStart() {
                Log.e("sync__", params.toString());
            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                String s = new String(responseBody);
                Log.e("sync__", s);
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                Log.e("sync__", error.getMessage());
            }
        });
    }

    /**
     * Using SharedPreferences to save last active time
     **/
    private void saveOfflineDateToPreferences() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("last_active_time", getCurrentTime());
        editor.apply();
    }

    /**
     * Fetch last active time from SharedPreferences
     **/
    private String getLastActiveTime() {
        return sharedPreferences.getString("last_active_time", getCurrentTime());
    }


    /**
     * Method to return device IMEI. This requires explicit permissions that need to be granted from
     * Android 6.0 and greater.
     **/
    @SuppressLint({"MissingPermission", "HardwareIds"})
    public String getDeviceIMEI(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (MainActivity.getInstance().isPermissionGranted()) {
            return telephonyManager.getDeviceId();
        } else {
            return "";
        }
    }

    /**
     * Method to return the current date time using SimpleDateFormat
     **/
    public String getCurrentTime() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());//dd/MM/yyyy
        Date now = new Date();
        return sdfDate.format(now);
    }
}
