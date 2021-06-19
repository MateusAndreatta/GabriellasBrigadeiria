package com.mateusandreatta.gabriellasbrigadeiria.service;

import android.content.Intent;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.mateusandreatta.gabriellasbrigadeiria.LoginActivity;
import com.mateusandreatta.gabriellasbrigadeiria.MainActivity;

public class FCMService extends FirebaseMessagingService {

    private final String TAG = "TAG-FCMService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage){
        Log.i(TAG, remoteMessage.getMessageId());
    }


    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);
    }

}
