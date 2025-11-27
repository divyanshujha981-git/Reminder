package com.reminder.main.UserInterfaces.NotificationPage.TaskReceived.NotificationTypes;

import static com.reminder.main.Custom.CustomFunctions.getIdFromTaskID;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.preference.PreferenceManager;

import com.reminder.main.R;
import com.reminder.main.UserInterfaces.NotificationPage.NotificationConstants.NotificationConstants;
import com.reminder.main.UserInterfaces.NotificationPage.TaskAlarm.BroadCasts.TaskAlertCancelBroadcast;


public class TaskReceivedNotificationGeneralClass {
    public static int TASK_RECEIVED_NOTIFICATION_ID = 4;
    private final Context context;

    public TaskReceivedNotificationGeneralClass(Context context) {
        this.context = context;
    }

    public void taskAlert(String topic, String taskWebID, String userName) {

        if (ActivityCompat.checkSelfPermission(context, "android.permission.POST_NOTIFICATIONS") != PackageManager.PERMISSION_GRANTED) {
            return;
        }


        boolean taskAutoDownload = PreferenceManager.getDefaultSharedPreferences(context).getBoolean(context.getString(R.string.taskAutoDownload), false);


        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context, NotificationConstants.TASK_RECEIVED_NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.app_logo)
                .setContentTitle(topic)
                .setContentText("By: " + userName)
                .setAutoCancel(true)
                .setOngoing(false);

        if (!taskAutoDownload) {
            notificationBuilder.addAction(
                    R.drawable.download,
                    "Download",
                    getCancelIntent(context, taskWebID));
        }


        NotificationManagerCompat manager = NotificationManagerCompat.from(context);
        manager.notify(TASK_RECEIVED_NOTIFICATION_ID, notificationBuilder.build());


    }


    private PendingIntent getCancelIntent(Context context, String taskID) {

        Intent cancelIntent = new Intent(context, TaskAlertCancelBroadcast.class);
        return PendingIntent.getBroadcast(context, getIdFromTaskID(taskID), cancelIntent, PendingIntent.FLAG_IMMUTABLE);


    }




}
