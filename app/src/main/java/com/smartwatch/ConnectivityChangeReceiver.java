package com.smartwatch;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.smartwatch.utils.Constants;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * <p>
 * A broadcast receiver (receiver) is an Android component which allows you to register for system
 * or application events. All registered receivers for an event are notified by the Android runtime
 * once this event happens.
 */

public class ConnectivityChangeReceiver extends BroadcastReceiver {
    private static boolean firstConnect = true;
    SharedPreferences sharedPreferences;
    // Tag used to cancel the request
    String tag_string_req = "string_req";
    Context context;

    /**
     * This method is called when the BroadcastReceiver is receiving an Intent broadcast.
     **/
    @Override
    public void onReceive(final Context context, final Intent intent) {
        this.context = context;
        //initialize SharedPreferences for persistence of data
        sharedPreferences = context.getSharedPreferences("ACCOUNT", context.MODE_PRIVATE);

        //Start location sharing service to app server.........
        Intent startServiceIntent = new Intent(context, LocationMonitoringService.class);
        context.startService(startServiceIntent);

        //check for network connectivity
        final ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        final android.net.NetworkInfo wifi = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        final android.net.NetworkInfo mobile = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if (wifi.isConnected()) {
            Constants.connectionType = "Wifi";
            if (firstConnect) {
                firstConnect = false;
                updateViewOnConnectivityStatus(true);
                Log.e("network_______", "connected");
            }
        } else if (mobile.isConnected()) {
            Constants.connectionType = "Mobile data";
            if (firstConnect) {
                // do subroutines here
                firstConnect = false;
                updateViewOnConnectivityStatus(true);
                Log.e("network_______", "connected");
            }
        } else {
            Constants.connectionType = "";
            firstConnect = true;
            Log.e("network_______", "disconnected");
            updateViewOnConnectivityStatus(false);
        }
        Log.e("WORKPLACE", String.valueOf(Constants.isWorkPlace) + "/" + Constants.connectionType);
        if (Constants.isWorkPlace) {
            startNetworkRequestCommands();
        }

    }

    public void startNetworkRequestCommands() {
        if (Constants.connectionType.equalsIgnoreCase("Wifi")) {
            updateViewOnConnectivityStatus(true);
            Log.e("network_______1", "connected");
            networkRequest();
        } else if (Constants.connectionType.equalsIgnoreCase("Mobile data")) {
            updateViewOnConnectivityStatus(true);
            networkRequest();
            Log.e("network_______2", "connected");
        } else {
            Log.e("network_______3", "disconnected");
            updateViewOnConnectivityStatus(false);
            saveOfflineDateToPreferences();
        }
    }

    /**
     * Update UI when network changes
     **/
    private void updateViewOnConnectivityStatus(final boolean isConnected) {
        if (MainActivity.getInstance() != null) {//only if view is visible
            MainActivity.getInstance().updateView(isConnected);
        }

    }

    /**
     * Making the request using volley library. Volley library uses a singleton
     * to make network requests.
     * Requires @params: imei_code, active_time, last_active_time, type
     **/

    private void networkRequest() {
        final String url = context.getResources().getString(R.string.base_url) + "connected.php";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("sync__", response);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("sync__", error.toString());
                networkRequest();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                if (getDeviceIMEI(context) != null) {
                    params.put("imei_code", getDeviceIMEI(context));
                } else {
                    params.put("imei_code", "0000000000000000");
                }
                params.put("active_time", getCurrentTime());
                params.put("last_active_time", getLastActiveTime());
                params.put("type", Constants.connectionType);

                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
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
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context
                .TELEPHONY_SERVICE);
        assert telephonyManager != null;
        return telephonyManager.getDeviceId();
    }

    /**
     * Method to return the current date time using SimpleDateFormat
     **/
    public String getCurrentTime() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault
                ());//dd/MM/yyyy
        Date now = new Date();
        return sdfDate.format(now);
    }
}
