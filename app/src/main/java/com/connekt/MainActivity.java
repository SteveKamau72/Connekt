package com.connekt;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.connekt.Geofence.Constants;
import com.connekt.Geofence.GeofenceRegistrationService;
import com.connekt.Geofence.LocationMonitoringService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends AppCompatActivity implements GoogleApiClient
        .ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "MainActivity";
    private static final int REQUEST_LOCATION_PERMISSION_CODE = 101;
    private static MainActivity mainActivityRunningInstance;
    TextView tvStatus, tvVersion;
    ImageView imgStatus;
    RelativeLayout rootLayout;
    private GeofencingRequest geofencingRequest;
    private GoogleApiClient googleApiClient;
    private PendingIntent pendingIntent;

    public static MainActivity getInstance() {
        return mainActivityRunningInstance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Get activity's running instance
        mainActivityRunningInstance = this;

        // Hiding Title bar of this activity screen
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        //Making this activity, full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager
                .LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        initViews();

        // At activity startup we manually check the internet status and change
        // the connectivity status
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context
                .CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            updateView(true);
        } else {
            updateView(false);
        }

        //Request user for required permissions
        isPermissionGranted();

        buildGoogleClient();
    }

    /**
     * Initialize views
     **/
    private void initViews() {
        tvStatus = findViewById(R.id.status);
        tvVersion = findViewById(R.id.version);
        imgStatus = findViewById(R.id.img_status);
        rootLayout = findViewById(R.id.root_layout);
        tvVersion.setText("V.2");
    }

    private synchronized void buildGoogleClient() {
        googleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
    }

    /**
     * Allows read only access to phone state, including the phone number of the device, IMEI,
     * current
     * cellular network information, the status of any ongoing calls, and a list of any
     * PhoneAccounts
     * registered on the device.
     * Protection level: dangerous
     **/
    public boolean isPermissionGranted() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (checkSelfPermission(android.Manifest.permission.READ_PHONE_STATE) ==
                    PackageManager.PERMISSION_GRANTED) {
                Log.e("TAG", "Permission is granted");
                return true;
            } else {
                Log.e("TAG", "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission
                        .READ_PHONE_STATE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.e("TAG", "Permission is granted");
            return true;
        }
    }


    /**
     * Permission results callback
     **/
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[]
            grantResults) {
        switch (requestCode) {

            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager
                        .PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "Permission granted", Toast
                            .LENGTH_SHORT).show();
                    //do ur specific task after read phone state granted
                } else {
                    Toast.makeText(getApplicationContext(), "Permission denied", Toast
                            .LENGTH_SHORT).show();
                    finish();
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void startLocationMonitor() {
        Log.e(TAG, "start location monitor");
        Intent serviceIntent = new Intent(this, LocationMonitoringService.class);
        startService(serviceIntent);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 100ms
                Log.e("start_______", Constants.location.getLatitude() + Constants.location
                        .getLongitude() + "");
                startGeofencing();
            }
        }, 60000);
    }

    private void startGeofencing() {
        Log.e(TAG, "Start geofencing monitoring call");
        pendingIntent = getGeofencePendingIntent();
        geofencingRequest = new GeofencingRequest.Builder().setInitialTrigger(Geofence
                .GEOFENCE_TRANSITION_ENTER).addGeofence(getGeofence()).build();

        if (!googleApiClient.isConnected()) {
            Log.e(TAG, "Google API client not connected");
        } else {
            try {
                LocationServices.GeofencingApi.addGeofences(googleApiClient, geofencingRequest,
                        pendingIntent).setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        if (status.isSuccess()) {
                            Log.e(TAG, "Successfully Geofencing Connected");
                        } else {
                            Log.e(TAG, "Failed to add Geofencing " + status.getStatus());
                        }
                    }
                });
            } catch (SecurityException e) {
                Log.e(TAG, e.getMessage());
            }
        }
    }

    @NonNull
    private Geofence getGeofence() {
        Log.e("getGeofence: ","Started___" );
        //LatLng latLng = Constants.AREA_LANDMARKS.get(Constants.GEOFENCE_ID_WORKPLACE);
        Double latitude = Constants.location.getLatitude(), longitude = Constants.location
                .getLongitude();
        return new Geofence.Builder().setRequestId(Constants.GEOFENCE_ID_WORKPLACE)
                .setExpirationDuration(Geofence.NEVER_EXPIRE).setCircularRegion(latitude,
                        longitude, Constants.GEOFENCE_RADIUS_IN_METERS)
                .setNotificationResponsiveness(1000).setTransitionTypes(Geofence
                        .GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT).build();
    }

    private PendingIntent getGeofencePendingIntent() {
        if (pendingIntent != null) {
            return pendingIntent;
        }
        Intent intent = new Intent(this, GeofenceRegistrationService.class);
        return PendingIntent.getService(this, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
    }

    private void stopGeoFencing() {
        pendingIntent = getGeofencePendingIntent();
        LocationServices.GeofencingApi.removeGeofences(googleApiClient, pendingIntent)
                .setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                if (status.isSuccess()) Log.e(TAG, "Stop geofencing");
                else Log.e(TAG, "Not stop geofencing");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        int response = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable
                (MainActivity.this);
        if (response != ConnectionResult.SUCCESS) {
            Log.e(TAG, "Google Play Service Not Available");
            GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, response, 1)
                    .show();
        } else {
            Log.e(TAG, "Google play service available");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.reconnect();
        startLocationMonitor();

        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (!gps_enabled && !network_enabled) {
            //showSettingDialog();
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        mainActivityRunningInstance = this;
        //googleApiClient.disconnect();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mainActivityRunningInstance = this;
    }

    /**
     * Update UI when network changes
     **/
    public void updateView(boolean isConnected) {
        if (isConnected) {
            tvStatus.setText("Internet Connected");
            rootLayout.setBackgroundColor(getResources().getColor(R.color.connectedColor));
            imgStatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_connected));
        } else {
            tvStatus.setText("Internet Disconnected");
            rootLayout.setBackgroundColor(getResources().getColor(R.color.disconnectedColor));
            imgStatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_disconnected));
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (Constants.location == null) {
            startLocationMonitor();
        } else {
            Log.e("start_______", Constants.location.getLatitude() + Constants.location
                    .getLongitude() + "");
        }
        //startGeofencing();
        //startLocationMonitor();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "Connection Failed:" + connectionResult.getErrorMessage());
    }
}
