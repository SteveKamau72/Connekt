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
    private static long ACTIVE_TIME_MILLIS = 2629746000L;
    NetworkStatsManager networkStatsManager;
    int packageUid;

    public NetworkStatsHelper(NetworkStatsManager networkStatsManager) {
        this.networkStatsManager = networkStatsManager;
    }

    public NetworkStatsHelper(NetworkStatsManager networkStatsManager, int packageUid) {
        this.networkStatsManager = networkStatsManager;
        this.packageUid = packageUid;
    }

    public NetworkStatsHelper(NetworkStatsManager networkStatsManager, long
            lastActiveTimeInMillis) {
        this.networkStatsManager = networkStatsManager;
        ACTIVE_TIME_MILLIS = lastActiveTimeInMillis;
    }

    public long getAllRxBytesMobile(Context context) {
        NetworkStats.Bucket bucket;
        try {
            bucket = networkStatsManager.querySummaryForDevice(ConnectivityManager.TYPE_MOBILE,
                    getSubscriberId(context, ConnectivityManager.TYPE_MOBILE),
                    0,
                    System.currentTimeMillis());
        } catch (RemoteException e) {
            return -1;
        }
        return bucket.getRxBytes();
    }

    public long getAllRxBytesWifi(Context context) {
        NetworkStats.Bucket bucket;
        try {
            bucket = networkStatsManager.querySummaryForDevice(ConnectivityManager.TYPE_WIFI,
                    getSubscriberId(context, ConnectivityManager.TYPE_WIFI),
                    ACTIVE_TIME_MILLIS,
                    System.currentTimeMillis());
        } catch (RemoteException e) {
            return -1;
        }
        return bucket.getRxBytes();
    }

    public long getAllTxBytesMobile(Context context) {
        NetworkStats.Bucket bucket;
        try {
            bucket = networkStatsManager.querySummaryForDevice(ConnectivityManager.TYPE_MOBILE,
                    getSubscriberId(context, ConnectivityManager.TYPE_MOBILE),
                    0,
                    System.currentTimeMillis());
        } catch (RemoteException e) {
            return -1;
        }
        return bucket.getTxBytes();
    }


    public long getAllTxBytesWIFI(Context context) {
        NetworkStats.Bucket bucket;
        try {
            bucket = networkStatsManager.querySummaryForDevice(ConnectivityManager.TYPE_WIFI,
                    getSubscriberId(context, ConnectivityManager.TYPE_WIFI),
                    0,
                    System.currentTimeMillis());
        } catch (RemoteException e) {
            return -1;
        }
        return bucket.getTxBytes();
    }


    public long getPackageRxBytesMobile() {
        NetworkStats networkStats;
        long endTime = System.currentTimeMillis();
        long startTime = endTime - ACTIVE_TIME_MILLIS;
        networkStats = networkStatsManager.queryDetailsForUid(
                ConnectivityManager.TYPE_MOBILE,
                "",
                startTime,
                endTime,
                packageUid);
        NetworkStats.Bucket bucket = new NetworkStats.Bucket();
        networkStats.getNextBucket(bucket);
        return bucket.getRxBytes();
    }

    public long getPackageTxBytesMobile() {
        NetworkStats networkStats;
        long endTime = System.currentTimeMillis();
        long startTime = endTime - ACTIVE_TIME_MILLIS;
        networkStats = networkStatsManager.queryDetailsForUid(
                ConnectivityManager.TYPE_MOBILE,
                "",
                startTime,
                endTime,
                packageUid);
        NetworkStats.Bucket bucket = new NetworkStats.Bucket();
        networkStats.getNextBucket(bucket);
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

    public long getPackageRxBytesWifi() {
        NetworkStats networkStats;
        long endTime = System.currentTimeMillis();
        long startTime = endTime - ACTIVE_TIME_MILLIS;
        networkStats = networkStatsManager.queryDetailsForUid(
                ConnectivityManager.TYPE_WIFI,
                "",
                startTime,
                endTime,
                packageUid);
        NetworkStats.Bucket bucket = new NetworkStats.Bucket();
        networkStats.getNextBucket(bucket);
        return bucket.getRxBytes();
    }

    public long getPackageTxBytesWifi() {
        NetworkStats networkStats;
        long endTime = System.currentTimeMillis();
        long startTime = endTime - ACTIVE_TIME_MILLIS;
        networkStats = networkStatsManager.queryDetailsForUid(
                ConnectivityManager.TYPE_WIFI,
                "",
                startTime,
                endTime,
                packageUid);
        NetworkStats.Bucket bucket = new NetworkStats.Bucket();
        networkStats.getNextBucket(bucket);
        return bucket.getTxBytes();
    }

}
