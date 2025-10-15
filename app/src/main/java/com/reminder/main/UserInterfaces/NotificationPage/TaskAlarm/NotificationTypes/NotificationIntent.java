package com.reminder.main.UserInterfaces.NotificationPage.TaskAlarm.NotificationTypes;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.reminder.main.R;
import com.reminder.main.SqLite.Tasks.TaskConstants;
import com.reminder.main.UserInterfaces.NotificationPage.TaskAlarm.BroadCasts.TaskAlertCancelBroadcast;
import com.reminder.main.UserInterfaces.NotificationPage.TaskAlarm.LaterTask.LaterTask;
import com.reminder.main.UserInterfaces.TaskViewPage.TaskViewMain;

import java.util.Objects;

public class NotificationIntent extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_intent);
        getWindow().addFlags(128);

        sendBroadcast(new Intent(this, TaskAlertCancelBroadcast.class));

        final long taskID = Long.parseLong(Objects.requireNonNull(getIntent().getStringExtra(TaskConstants.TASK_ID)));
        String topic =  Objects.requireNonNull(getIntent().getStringExtra(TaskConstants.TOPIC));
        ((TextView) findViewById(R.id.setTopic)).setText(topic);

        findViewById(R.id.closeBTN).setOnClickListener(view -> {
            sendBroadcast(new Intent(this, TaskAlertCancelBroadcast.class));
            finish();
        });
        findViewById(R.id.laterBTN).setOnClickListener(view -> {
            Intent intent = new Intent(this, LaterTask.class);
            intent.putExtra(TaskConstants.TASK_ID, String.valueOf(taskID));
            sendBroadcast(new Intent(this, TaskAlertCancelBroadcast.class));
            finish();
            startActivity(intent);
        });
        findViewById(R.id.viewBTN).setOnClickListener(view -> {
            Intent intent = new Intent(this, TaskViewMain.class);
            intent.putExtra(TaskConstants.TASK_ID, String.valueOf(taskID));
            finish();
            startActivity(intent);
            sendBroadcast(new Intent(this, TaskAlertCancelBroadcast.class));
        });

    }


}
