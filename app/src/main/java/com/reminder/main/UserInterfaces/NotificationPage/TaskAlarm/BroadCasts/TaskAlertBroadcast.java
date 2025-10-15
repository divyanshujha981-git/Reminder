package com.reminder.main.UserInterfaces.NotificationPage.TaskAlarm.BroadCasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.util.Log;

import androidx.preference.PreferenceManager;

import com.reminder.main.BackgroundWorks.TaskWork.RescheduleTaskAfterAlarmTrigger;
import com.reminder.main.R;
import com.reminder.main.SqLite.Tasks.TaskConstants;
import com.reminder.main.UserInterfaces.NotificationPage.TaskAlarm.NotificationTypes.TaskNotificationGeneralClass;


public class TaskAlertBroadcast extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        String topic = intent.getStringExtra(TaskConstants.TOPIC);
        long taskID = intent.getLongExtra(TaskConstants.TASK_ID, 0L);
        int locked = intent.getIntExtra(TaskConstants.PRIVATE, TaskConstants.PRIVATE_NO);

        int alreadyDone = intent.getIntExtra(TaskConstants.ALREADY_DONE, TaskConstants.ALREADY_DONE_NO_BYTE);

        boolean alreadyDoneModeStatus = PreferenceManager.getDefaultSharedPreferences(context).getBoolean(context.getString(R.string.post_notification_for_completed_task), false);


        Log.d("TAG", "onReceive: " + alreadyDoneModeStatus);
        Log.d("TAG", "onReceive: " + (alreadyDone == TaskConstants.ALREADY_DONE_YES_BYTE ? " ALREADY_DONE_YES " : "ALREADY_DONE_NO"));


        //if (!alreadyDoneModeStatus || (alreadyDone == TaskConstants.ALREADY_DONE_NO)) {
        //    postNotification(context, topic, locked, taskID);
        //}

        if (alreadyDoneModeStatus) {
            postNotification(context, topic, locked, taskID);
        }
        else if (alreadyDone == TaskConstants.ALREADY_DONE_NO_BYTE) {
            postNotification(context, topic, locked, taskID);
        }


        rescheduleTask(context, taskID);


        Log.d("TAG", "onReceive: **ALARM TRIGGERED**");
        Log.d("TAG", "onReceive: **" + topic + "**");
        Log.d("TAG", "onReceive: **" + locked + "**");




    }



    private void postNotification(Context context, String topic, int locked, long taskID) {

        TaskNotificationGeneralClass taskNotificationGeneralClass = new TaskNotificationGeneralClass(context);
        taskNotificationGeneralClass.taskAlert(topic, taskID, locked);

        if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean(context.getString(R.string.turn_on_flash_for_task_alert), true)) {
            try {

                CameraManager cameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
                cameraManager.setTorchMode(cameraManager.getCameraIdList()[0], true);

            } catch (CameraAccessException | RuntimeException ignored) {

            }
        }

    }




    private void rescheduleTask(Context context, long taskID) {

        RescheduleTaskAfterAlarmTrigger.rescheduleCurrentTask(context, taskID);
        context.sendBroadcast(new Intent(context, RescheduleTaskAfterAlarmTrigger.class));

    }




    private void playSound() {

    }



}
