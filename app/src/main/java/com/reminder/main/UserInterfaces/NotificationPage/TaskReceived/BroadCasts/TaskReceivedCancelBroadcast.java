package com.reminder.main.UserInterfaces.NotificationPage.TaskReceived.BroadCasts;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.reminder.main.UserInterfaces.NotificationPage.TaskReceived.NotificationTypes.TaskReceivedNotificationGeneralClass;

public class TaskReceivedCancelBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(TaskReceivedNotificationGeneralClass.TASK_RECEIVED_NOTIFICATION_ID);
        Log.d("TAG", "onReceive: **CANCELLATION TRIGGERED**");

    }
}
