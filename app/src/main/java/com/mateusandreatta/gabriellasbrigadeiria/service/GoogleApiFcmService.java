package com.mateusandreatta.gabriellasbrigadeiria.service;

import com.mateusandreatta.gabriellasbrigadeiria.model.FCMResponse;
import com.mateusandreatta.gabriellasbrigadeiria.model.NotificationRequest;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface GoogleApiFcmService {
    @Headers({
            "Content-Type: application/json",
            "Authorization: key=AAAAVYTf5tc:APA91bG-lCBcv1Vr49tWYTStP_e1dtx6WcMnpAFJLvGhQYa_wTH1xrx3BKLiJra3uI7R9aOfwVQcTgq5XBGbg0zmyrtS7vuAeJWMoSS5JnW8Q29IGZCN159i4utT1N-xuKfC3ezyr6qq"
    })
    @POST("fcm/send")
    Call<FCMResponse> sendNotification(@Body NotificationRequest notification);
}