package com.reminder.main.BackgroundWorks.FirebaseWork;

import static com.reminder.main.Firebase.FirebaseConstants.SET_DEVICE_MESSAGE_TOKEN;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.reminder.main.Firebase.FirebaseConstants;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class FirebaseMessageService extends FirebaseMessagingService {






    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);

        Map<String, String> map = new HashMap<>();

        Log.d("TAG", "onNewToken: **MESSAGE TOKEN CHANGED**");

        FirebaseFunctions
                .getInstance()
                .getHttpsCallable(SET_DEVICE_MESSAGE_TOKEN)
                .call(new Gson().toJson(map))
                .addOnSuccessListener(httpsCallableResult -> {
                    Log.d("TAG", "onNewToken: **TOKEN UPDATED**");
                })
                .addOnFailureListener(e -> {
                    Log.d("TAG", "onNewToken: **TOKEN NOT UPDATED**");
                })
                .addOnCompleteListener(task -> {
                    Log.d("TAG", "onNewToken: **REQUESTED FOR TOKEN UPDATE**");
                });
        Log.d("TAG", "onNewToken: -->> " + token);
    }







    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);

        Log.d("TAG", "onMessageReceived: ");

        if (message.getNotification() != null) {

            String topic = message.getNotification().getTitle();

            Log.d("TAG", "onMessageReceived: " + topic);
            Log.d("TAG", "onMessageReceived: " + message.getData());

            if (Objects.equals(topic, FirebaseConstants.TOPIC_CHANGE_IN_TASK_STATUS)) {
                handleTaskStatus(message.getData());
            }

            else if (Objects.equals(topic, FirebaseConstants.TOPIC_CHANGE_IN_REQUEST)) {
                handleRequest(message.getData());
            }

            else if (Objects.equals(topic, FirebaseConstants.TOPIC_CHANGE_IN_TASK)) {
                handleTask(message.getData());
            }
        }

    }













    private void handleTaskStatus(Map<?, ?> map) {
        Log.d("TAG", "handleTaskStatus: " + map);
    }












    private void handleRequest(Map<?, ?> map) {
        Log.d("TAG", "handleRequest: " + map);
    }










    private void handleTask(Map<?, ?> map) {
        Log.d("TAG", "handleTask: " + map);
    }







    public static Bitmap getBitmapProfilePic(Context context, String userProfilePic) {
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(Uri.parse(userProfilePic)));
            Log.d("TAG", "createNotification: ");
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bitmap;
    }



}
