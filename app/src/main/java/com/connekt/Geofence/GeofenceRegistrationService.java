package com.connekt.Geofence;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

public class GeofenceRegistrationService extends IntentService {

    private static final String TAG = "GeoIntentService";

    public GeofenceRegistrationService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.e(TAG, "__started" );
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            Log.e(TAG, "GeofencingEvent error " + geofencingEvent.getErrorCode());
        } else {
            int transaction = geofencingEvent.getGeofenceTransition();
            List<Geofence> geofences = geofencingEvent.getTriggeringGeofences();
            Geofence geofence = geofences.get(0);
            String requestId = geofence.getRequestId();
            if (transaction == Geofence.GEOFENCE_TRANSITION_ENTER && requestId.equals(Constants.GEOFENCE_ID_WORKPLACE)) {
                Log.e("GeofenceService", "Fired Entering Copia" );
            } else {
                Log.e("GeofenceService", "Fired exiting Copia" );
            }
        }
    }
}
