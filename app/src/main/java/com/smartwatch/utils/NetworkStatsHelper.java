package com.smartwatch.utils;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.usage.NetworkStats;
import android.app.usage.NetworkStatsManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.RemoteException;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;


@TargetApi(Build.VERSION_CODES.M)
public class NetworkStatsHelper {
    NetworkStatsManager networkStatsManager;

    public NetworkStatsHelper(NetworkStatsManager networkStatsManager) {
        this.networkStatsManager = networkStatsManager;
    }

    public long getAllTxBytesMobile(Context context, long startTimeInMillis) {
        NetworkStats.Bucket bucket;
        try {
            bucket = networkStatsManager.querySummaryForUser(ConnectivityManager.TYPE_MOBILE,
                    getSubscriberId(context, ConnectivityManager.TYPE_MOBILE),
                    startTimeInMillis,
                    System.currentTimeMillis());
        } catch (RemoteException e) {
            return -1;
        }
        return bucket.getTxBytes();
    }

    public long getAllTxBytesWifi(Context context, long startTimeInMillis) {
        NetworkStats.Bucket bucket;
        try {
            bucket = networkStatsManager.querySummaryForUser(ConnectivityManager.TYPE_WIFI,
                    getSubscriberId(context, ConnectivityManager.TYPE_WIFI),
                    startTimeInMillis,
                    System.currentTimeMillis());
        } catch (RemoteException e) {
            return -1;
        }
        return bucket.getTxBytes();
    }

    private String getSubscriberId(Context context, int networkType) {
        if (ConnectivityManager.TYPE_MOBILE == networkType) {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context
                    .TELEPHONY_SERVICE);

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) !=
                    PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return "";
            }
            return tm.getSubscriberId();
        }
        return "";
    }

}
