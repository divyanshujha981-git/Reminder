package com.reminder.main.UserInterfaces.NotificationPage.TaskReceived.BroadCasts;

import static com.reminder.main.Firebase.FirebaseConstants.USER_NAME;
import static com.reminder.main.SqLite.Tasks.TaskConstants.TASK_WEB_ID;
import static com.reminder.main.SqLite.Tasks.TaskConstants.TOPIC;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.reminder.main.SqLite.Tasks.TaskConstants;
import com.reminder.main.UserInterfaces.NotificationPage.TaskReceived.NotificationTypes.TaskReceivedNotificationGeneralClass;


public class TaskReceivedAlertBroadcast extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        String topic = intent.getStringExtra(TOPIC);
        String taskWebId = intent.getStringExtra(TASK_WEB_ID);
        String userName = intent.getStringExtra(USER_NAME);

        postNotification(context, topic, taskWebId, userName);

    }



    private void postNotification(Context context, String topic, String taskWebId, String userName) {

        TaskReceivedNotificationGeneralClass taskNotificationGeneralClass = new TaskReceivedNotificationGeneralClass(context);
        taskNotificationGeneralClass.taskAlert(topic, taskWebId, userName);


    }



}
