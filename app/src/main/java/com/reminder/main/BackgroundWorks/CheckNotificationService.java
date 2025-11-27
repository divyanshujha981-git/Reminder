package com.reminder.main.BackgroundWorks;

import static com.reminder.main.UserInterfaces.NotificationPage.NotificationConstants.NotificationConstants.TASK_NOTIFICATION_CHANNEL_ID;
import static com.reminder.main.UserInterfaces.NotificationPage.NotificationConstants.NotificationConstants.TASK_RECEIVED_NOTIFICATION_CHANNEL_ID;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;

import com.reminder.main.R;

public class CheckNotificationService extends Service {


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }




    @Override
    public void onCreate() {
        super.onCreate();



        new Thread(() -> {


            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            String appStart = getString(R.string.application_started_for_the_first_time);

            if (!sharedPreferences.getBoolean(appStart, false)) {

                Log.d("TAG", "onCreate: **CREATING CHANNELS**");


                //--->
                taskAlertNotification();
                taskReceivedNotification();
                //--->

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(appStart, true);
                editor.apply();


                BatteryReceiver batteryReceiver = new BatteryReceiver();
                IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
                registerReceiver(batteryReceiver, filter);


            }



        }).start();

    }





    private void taskAlertNotification() {

        Uri taskAlertURI;
        NotificationChannel notificationChannel;
        NotificationManager compat;

        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();


        //--->
        taskAlertURI = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.task_alert_ringtone_2);

        notificationChannel = new NotificationChannel(TASK_NOTIFICATION_CHANNEL_ID, "Task alert", NotificationManager.IMPORTANCE_HIGH);
        notificationChannel.setDescription("Alerts you for scheduled tasks");

        notificationChannel.enableVibration(true);
        notificationChannel.setVibrationPattern(new long[]{0, 500, 300, 500});

        notificationChannel.setSound(taskAlertURI, audioAttributes);


        compat = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        compat.createNotificationChannel(notificationChannel);

        //--->




    }






    private void taskReceivedNotification() {

        Uri taskReceivedAlertURI;
        NotificationChannel notificationChannel;
        NotificationManager compat;

        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        //--->
        taskReceivedAlertURI = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.task_received_sound);

        notificationChannel = new NotificationChannel(TASK_RECEIVED_NOTIFICATION_CHANNEL_ID, "Task received alert", NotificationManager.IMPORTANCE_HIGH);
        notificationChannel.setDescription("Alerts you for scheduled tasks");

        notificationChannel.enableVibration(true);
        notificationChannel.setVibrationPattern(new long[]{0, 300, 200, 300});

        notificationChannel.setSound(taskReceivedAlertURI, audioAttributes);

        compat = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        compat.createNotificationChannel(notificationChannel);

        //--->




    }



}
