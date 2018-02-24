package com.connekt;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    TextView tvStatus, tvVersion;
    ImageView imgStatus;
    RelativeLayout rootLayout;
    private static MainActivity mainActivityRunningInstance;

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
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        initViews();

        // At activity startup we manually check the internet status and change
        // the connectivity status
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            updateView(true);
        } else {
            updateView(false);
        }

        //Request user for required permissions
        isPermissionGranted();
    }

    /**
     * Initialize views
     **/
    private void initViews() {
        tvStatus = findViewById(R.id.status);
        tvVersion = findViewById(R.id.version);
        imgStatus = findViewById(R.id.img_status);
        rootLayout = findViewById(R.id.root_layout);
        tvVersion.setText("V.1");
    }

    /**
     * Allows read only access to phone state, including the phone number of the device, IMEI, current
     * cellular network information, the status of any ongoing calls, and a list of any PhoneAccounts
     * registered on the device.
     * Protection level: dangerous
     **/
    public boolean isPermissionGranted() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (checkSelfPermission(android.Manifest.permission.READ_PHONE_STATE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.e("TAG", "Permission is granted");
                return true;
            } else {
                Log.e("TAG", "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
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
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {

            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "Permission granted", Toast.LENGTH_SHORT).show();
                    //do ur specific task after read phone state granted
                } else {
                    Toast.makeText(getApplicationContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                    finish();
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
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
}
