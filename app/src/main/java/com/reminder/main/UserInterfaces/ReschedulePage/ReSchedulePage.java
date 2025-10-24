package com.reminder.main.UserInterfaces.ReschedulePage;

import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.reminder.main.BackgroundWorks.TaskWork.RescheduleTaskAfterAlarmTrigger;
import com.reminder.main.Other.ApplicationCustomInterfaces;
import com.reminder.main.R;
import com.reminder.main.SqLite.CommonDB.CommonDB;
import com.reminder.main.SqLite.Tasks.TaskConstants;
import com.reminder.main.SqLite.Tasks.TaskData;
import com.reminder.main.SqLite.Tasks.TasksDB;
import com.reminder.main.UserInterfaces.AddTaskPage.AddTask;
import com.reminder.main.UserInterfaces.Global.DatePickerClass;
import com.reminder.main.UserInterfaces.Global.TimePickerClass;
import com.reminder.main.UserInterfaces.HomePage.MainActivity.MainActivity;
import com.reminder.main.UserInterfaces.NotificationPage.TaskAlarm.BroadCasts.TaskAlertBroadcast;

import org.json.JSONException;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;


public class ReSchedulePage extends AppCompatActivity implements
        ApplicationCustomInterfaces.DateTime,
        ApplicationCustomInterfaces.RepeatStatus {
    private final Calendar finalAlarmDate = Calendar.getInstance();
    private LinearLayout daysInWeekView;
    private EditText setHour, setMinute;
    private Button amBtn, pmBtn;
    private byte finalRepeatStatus = TaskConstants.REPEAT_STATUS_NO_REPEAT;
    private final ArrayList<Integer> finalDaysInWeek = new ArrayList<>();
    private final DecimalFormat format = new DecimalFormat("00");
    public static final byte RESCHEDULING_NOT_REQUIRED = 1;
    private MaterialButton selectDateBtn;
    private LinearLayout repeatStatusView;
    private TaskData taskData;


    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.reschedule_page);

        declare();
        initiate();

    }


    private void declare() {
        taskData = getCurrentData();

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(taskData.getAlarmDate());

        setHour = findViewById(R.id.setHour);
        setMinute = findViewById(R.id.setMinute);
        daysInWeekView = findViewById(R.id.daysInWeek);
        selectDateBtn = findViewById(R.id.selectDateButton);


        amBtn = findViewById(R.id.setAM);
        pmBtn = findViewById(R.id.setPM);

        repeatStatusView = findViewById(R.id.repeatStatusView);
    }


    private void initiate() {
        //SET DEFAULT ALARM TIME
        finalAlarmDate.setTimeInMillis(taskData.getRepeatingAlarmDate());
        finalRepeatStatus = taskData.getRepeatStatus();

        setAmPm(finalAlarmDate.get(Calendar.AM_PM));

        ((TextView) findViewById(R.id.topic_reschedule)).setText(taskData.getTopic());
        ((TextView) findViewById(R.id.description_reschedule)).setText(taskData.getDescription());

        setHour.setText(format.format(finalAlarmDate.get(Calendar.HOUR) == 0 ? 12 : finalAlarmDate.get(Calendar.HOUR)));
        setMinute.setText(format.format(finalAlarmDate.get(Calendar.MINUTE)));
        selectDateBtn.setText(new StringBuilder(finalAlarmDate.get(Calendar.DAY_OF_MONTH) + "-" + (finalAlarmDate.get(Calendar.MONTH) + 1) + "-" + finalAlarmDate.get(Calendar.YEAR)));

        daysInWeekView.setVisibility(finalRepeatStatus == 0 ? View.GONE : View.VISIBLE);
        new RepeatStatusSpinnerClass(this, finalRepeatStatus, findViewById(R.id.setRepeatType));
        setRepeatStatus(finalRepeatStatus);

        selectDateBtn.setOnClickListener(v -> {
            DatePickerClass datePickerClass = new DatePickerClass(this);
            datePickerClass.showCalendar();
        });


        findViewById(R.id.selectTimeButton).setOnClickListener(v -> {
            TimePickerClass timePickerClass = new TimePickerClass(getSupportFragmentManager(), this);
            timePickerClass.timeFunction();
        });


        findViewById(R.id.expandTimeView).setOnClickListener(v -> MainActivity.expandHideView(v, repeatStatusView));


        amBtn.setOnClickListener(v -> setAmPm(Calendar.AM));
        pmBtn.setOnClickListener(v -> setAmPm(Calendar.PM));


        setFinalDaysInWeekArray();


        findViewById(R.id.rescheduleAddTask).setOnClickListener(v -> {
            if (rescheduleTask()) {
                finish();
            }
        });


    }


    private void setAmPm(int amPm) {
        finalAlarmDate.set(Calendar.AM_PM, amPm);
        if (amPm == Calendar.AM) {
            amBtn.setTextColor(getResources().getColor(R.color.blue_violet, null));
            pmBtn.setTextColor(getResources().getColor(R.color.grey, null));
        } else {
            pmBtn.setTextColor(getResources().getColor(R.color.blue_violet, null));
            amBtn.setTextColor(getResources().getColor(R.color.grey, null));
        }

    }


    @Override
    public void setTime(@NonNull int[] timeArr, byte amPm) {
        setAmPm(amPm);

        Log.d("TAG", "setDate: " + (timeArr[0] > 12 ? timeArr[0] - 12 : timeArr[0] == 0 ?
                12 : timeArr[0]));
        Log.d("TAG", "setDate: " + timeArr[1]);

        setHour.setText(
                format.format(
                        timeArr[0] > 12 ? timeArr[0] - 12 :
                                timeArr[0] == 0 ?
                                        12 : timeArr[0]
                )
        );

        setMinute.setText(format.format(timeArr[1]));
    }


    @Override
    public void setDate(@NonNull int[] setDate) {
        finalAlarmDate.set(Calendar.YEAR, setDate[0]);
        finalAlarmDate.set(Calendar.MONTH, setDate[1]);
        finalAlarmDate.set(Calendar.DAY_OF_MONTH, setDate[2]);

        Log.d("TAG", "setDate: " + setDate[2]);
        Log.d("TAG", "setDate: " + setDate[1] + 1);
        Log.d("TAG", "setDate: " + setDate[0]);

        selectDateBtn.setText(new StringBuilder(setDate[2] + "-" + (setDate[1] + 1) + "-" + setDate[0]));
    }


    private TaskData getCurrentData() {
        CommonDB commonDB = new CommonDB(this);
        Cursor cursor = commonDB.getReadableDatabase().rawQuery(
                " SELECT * FROM " + TaskConstants.TASK_TABLE_NAME +
                        " WHERE " + TaskConstants.TASK_ID + " = " + getIntent().getLongExtra(TaskConstants.TASK_ID, 0),
                null
        );


        cursor.moveToFirst();

        /*

                ID + "  INTEGER PRIMARY KEY AUTOINCREMENT, " +              // 0
                PRIVATE + " BYTE NOT NULL, " +                              // 1
                TOPIC + " TEXT NOT NULL, " +                                // 2
                DESCRIPTION + " TEXT, " +                                   // 3
                ALARM_DATE + " LONG NOT NULL, " +                           // 4
                REPEAT_STATUS + " BYTE NOT NULL, " +                        // 5
                DATE_ARRAY + " JSON, " +                                    // 6
                REPEATING_ALARM_DATE + " LONG NOT NULL, " +                 // 7
                LATER_ALARM_DATE + " LONG, " +                              // 8
                ALREADY_DONE + " BYTE NOT NULL, " +                         // 9
                PINNED + " BYTE NOT NULL, " +                               // 10
                TASK_ID + " LONG NOT NULL, " +                              // 11
                PRIORITY + " BYTE NOT NULL," +                              // 12


        */

        TaskData data = new TaskData();
        data.setPrivateTask((byte) cursor.getInt(1));
        data.setTopic(cursor.getString(2));
        data.setDescription(cursor.getString(3));
        data.setAlarmDate(cursor.getLong(4));
        data.setRepeatStatus((byte) cursor.getInt(5));
        data.setDateArray(cursor.getString(6));
        data.setRepeatingAlarmDate(cursor.getLong(7));
        data.setLaterAlarmDate(cursor.getLong(8));
        data.setAlreadyDone((byte) cursor.getInt(9));
        data.setPinned((byte) cursor.getInt(10));
        data.setTaskId(cursor.getString(11));
        data.setPriority((byte) cursor.getInt(12));

        commonDB.close();
        cursor.close();

        return data;

    }


    private void setFinalDaysInWeekArray() {
        for (int i = 0; i < 7; i++) {
            int finalI = i;
            Chip chip = (Chip) daysInWeekView.getChildAt(finalI);
            chip.setOnClickListener(v -> selectUnselectDaysOfWeek(finalI));
            try {
                int element = taskData.getDateArray().getInt(i);
                finalDaysInWeek.add(element);
                ((Chip) daysInWeekView.getChildAt(element - 1)).setChecked(true);
            } catch (JSONException ignored) {
            }
        }
    }


    private void selectUnselectDaysOfWeek(int position) {
        if (finalDaysInWeek.contains(position + 1)) finalDaysInWeek.remove((Object) (position + 1));
        else finalDaysInWeek.add(position + 1);
    }


    public static int getDateGap(ArrayList<Integer> finalDaysInWeekArray, int dayOfMonth, int dayOfWeek) {
        int newDay = -1;
        int i = 0;

        Log.d("TAG", "getDateGap: " + dayOfMonth + " " + dayOfWeek);

        for (; i < finalDaysInWeekArray.size(); i++) {
            if (dayOfWeek < finalDaysInWeekArray.get(i)) {
                newDay = finalDaysInWeekArray.get(i);
                break;
            }
        }

        if (newDay == -1) newDay = finalDaysInWeekArray.get(0);

        i = dayOfWeek;
        dayOfMonth++;
        for (; i < dayOfWeek + 7; i++) {
            if (TaskConstants.DAYS_OF_WEEK_ARRAY[i] == newDay) break;
            dayOfMonth++;
        }
        return dayOfMonth;
    }


    private boolean rescheduleTask() {

        int finalHour = Integer.parseInt(setHour.getText().toString());
        int finalMinute = Integer.parseInt(setMinute.getText().toString());
        finalAlarmDate.set(Calendar.HOUR, finalHour == 12 ? 0 : finalHour);
        finalAlarmDate.set(Calendar.MINUTE, finalMinute);
        finalAlarmDate.set(Calendar.SECOND, 0);
        finalAlarmDate.set(Calendar.MILLISECOND, 0);


        if (
                (
                        finalAlarmDate.getTimeInMillis() > Calendar.getInstance().getTimeInMillis()
                ) || (
                        finalAlarmDate.getTimeInMillis() < Calendar.getInstance().getTimeInMillis() && finalRepeatStatus == TaskConstants.REPEAT_STATUS_FROM_AD
                )
        ) {


            ContentValues contentValues = new ContentValues();
            if (finalDaysInWeek != null) {
                Collections.sort(finalDaysInWeek);
                contentValues.put(TaskConstants.DATE_ARRAY, finalDaysInWeek.toString());
            }


            contentValues.put(TaskConstants.REPEAT_STATUS, finalRepeatStatus);
            contentValues.put(TaskConstants.ALARM_DATE, finalAlarmDate.getTimeInMillis());

            contentValues.put(
                    TaskConstants.REPEATING_ALARM_DATE,
                    finalRepeatStatus == TaskConstants.REPEAT_STATUS_NO_REPEAT ?
                            finalAlarmDate.getTimeInMillis() :
                            AddTask.rescheduleDateAndTIme(
                                    finalDaysInWeek,
                                    finalAlarmDate.getTimeInMillis(),
                                    finalRepeatStatus
                            )
            );

            TasksDB.updateTask(this, contentValues, taskData.getTaskId());
            cancelAndRescheduleTask();


            return true;

        } else {
            Toast.makeText(this, "Please select valid time", Toast.LENGTH_SHORT).show();
            return false;
        }


    }


    @Override
    public void setRepeatStatus(byte status) {
        daysInWeekView.setVisibility(status == TaskConstants.REPEAT_STATUS_NO_REPEAT ? View.GONE : View.VISIBLE);
        finalRepeatStatus = status;
    }




    private void cancelAndRescheduleTask() {
        Intent alarmIntent = new Intent(this, TaskAlertBroadcast.class);

        int requestCode = PreferenceManager.getDefaultSharedPreferences(this)
                .getInt(getString(R.string.currently_scheduled_task_request_code), 0);

        PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(this, requestCode, alarmIntent, PendingIntent.FLAG_IMMUTABLE);
        alarmPendingIntent.cancel();

        sendBroadcast(new Intent(this, RescheduleTaskAfterAlarmTrigger.class));
    }



}



