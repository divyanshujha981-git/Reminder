package com.reminder.main.UserInterfaces.NotificationPage.TaskAlarm.BroadCasts;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.util.Log;

import com.reminder.main.UserInterfaces.NotificationPage.TaskAlarm.NotificationTypes.TaskNotificationGeneralClass;

public class TaskAlertCancelBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(TaskNotificationGeneralClass.TASK_ALERT_NOTIFICATION_ID);
        Log.d("TAG", "onReceive: **CANCELLATION TRIGGERED**");
        try {
            CameraManager cameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
            cameraManager.setTorchMode(cameraManager.getCameraIdList()[0], false);
        } catch (CameraAccessException | RuntimeException e) {

        }

    }
}
