package com.streamnow.lindaumobile.activities;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.streamnow.lindaumobile.R;
import com.streamnow.lindaumobile.datamodel.LDSessionUser;
import com.streamnow.lindaumobile.utils.Lindau;

import java.io.IOException;

/**
 * Created by Miguel Angel on 17/06/2016.
 */

public class RegistrationIntentService extends IntentService {
    private static String SENDER_ID = "583844385806";

    public RegistrationIntentService(){
        super(SENDER_ID);
    }
  @Override
    public void onHandleIntent(Intent intent){

      //SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

      try {
          InstanceID instanceID = InstanceID.getInstance(this);
          String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                  GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
          Log.i("TOKEN", "GCM Registration Token: " + token);

        //  sendRegistrationToServer(token);
         // subscribeTopics(token);
       //   sharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, true).apply();
      } catch (Exception e) {
          Log.d("Fail token", "Failed to complete token refresh", e);
       //   sharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false).apply();
      }
      //Intent registrationComplete = new Intent(QuickstartPreferences.REGISTRATION_COMPLETE);
      //LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
  }
    private void subscribeTopics(String token) throws IOException {
        GcmPubSub pubSub = GcmPubSub.getInstance(this);
        pubSub.subscribe(token, "/topics/"+Lindau.getInstance().getCurrentSessionUser().userInfo.id, null);
    }
    //envio del RegistrationID(token)a nuestro server
    private void sendRegistrationToServer(String token){




    }

}
