package com.streamnow.lindaumobile.activities;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

/**
 * Created by Miguel Angel on 18/06/2016.
 */
public class PushNotifications extends GcmListenerService {

    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("message");
        Log.d("From", "From: " + from);
        Log.d("Msg", "Message: " + message);

    }

}
