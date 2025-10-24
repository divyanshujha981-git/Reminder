package com.reminder.main.UserInterfaces.SendTaskPage;

import static com.reminder.main.SqLite.Tasks.TaskConstants.PINNED_YES;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.preference.PreferenceManager;

import com.reminder.main.Other.ApplicationCustomInterfaces;
import com.reminder.main.SqLite.CommonDB.CommonDB;
import com.reminder.main.SqLite.Tasks.TaskConstants;
import com.reminder.main.SqLite.Tasks.TaskData;
import com.reminder.main.UserInterfaces.HomePage.Tasks.TaskSqlData;

import java.util.ArrayList;
import java.util.Calendar;

import kotlinx.coroutines.scheduling.Task;

public class TaskSQLData {
    private SQLiteDatabase db;
    private CommonDB commonDB;
    private Cursor taskCursor = null;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final ApplicationCustomInterfaces.TaskSQLInterface taskSQLInterface;
    private final Calendar calendar = Calendar.getInstance();
    private final Context context;
    public TaskSQLData (Context context) {
        this.context = context;
        taskSQLInterface = (ApplicationCustomInterfaces.TaskSQLInterface) context;
    }

    public void getAllData() {

        startDB();

        Thread thread1 = getTaskData();

        thread1.start();

    }

    private void startDB() {
        commonDB = new CommonDB(context);
        db = commonDB.getReadableDatabase();
    }

    public Thread getTaskData() {

        return new Thread(() -> {

            ArrayList<TaskData> taskData = new ArrayList<>();

            try {
                taskCursor = db.rawQuery(
                        "SELECT " +

                                TaskConstants.PRIVATE + ", " + // 0
                                TaskConstants.TOPIC + ", " + // 1
                                TaskConstants.PRIORITY + ", " + // 2
                                TaskConstants.REPEAT_STATUS + ", " + // 3
                                TaskConstants.DATE_ARRAY + ", " + // 4
                                TaskConstants.REPEATING_ALARM_DATE + ", " + // 5
                                TaskConstants.PINNED + ", " + // 6
                                TaskConstants.ALREADY_DONE + ", " + // 7
                                TaskConstants.TASK_ID + ", " +  // 8
                                TaskConstants.ID + ", " +  // 9
                                TaskConstants.ALARM_DATE + ", " +  // 10
                                TaskConstants.TASK_WEB_ID +  // 11

                                " FROM " + TaskConstants.TASK_TABLE_NAME +
                                " WHERE " + TaskConstants.PRIVATE + " != " + TaskConstants.PRIVATE_YES +
                                " ORDER BY " + TaskConstants.REPEATING_ALARM_DATE, null);
            } catch (SQLiteException e) {
                Log.e("TAG", "getSQLData: " + e );
            }

            if (taskCursor != null && taskCursor.getCount() > 0) {
                taskCursor.moveToFirst();

                do {

                    TaskData data = new TaskData();
                    data.setPrivateTask((byte) taskCursor.getInt(0));
                    data.setTopic(taskCursor.getString(1));
                    data.setPriority((byte) 2);
                    data.setRepeatStatus((byte) taskCursor.getInt(3));
                    data.setDateArray(taskCursor.getString(4));
                    data.setRepeatingAlarmDate(taskCursor.getLong(5));
                    data.setPinned((byte) taskCursor.getInt(6));
                    data.setAlreadyDone((byte) taskCursor.getInt(7));
                    data.setTaskId(taskCursor.getString(8));

                    calendar.setTimeInMillis(data.getRepeatingAlarmDate());
                    data.setYear(calendar.get(Calendar.YEAR));
                    data.setMonth((byte) calendar.get(Calendar.MONTH));
                    data.setDate((byte) calendar.get(Calendar.DAY_OF_MONTH));
                    data.setHour((byte) (calendar.get(Calendar.HOUR) == 0 ? 12 : calendar.get(Calendar.HOUR)));
                    data.setMinute((byte) calendar.get(Calendar.MINUTE));
                    data.setAmPm((byte) calendar.get(Calendar.AM_PM));


                    data.setId(taskCursor.getInt(9));
                    data.setAlarmDate(taskCursor.getLong(10));
                    data.setTaskWebId(taskCursor.getString(11));


                    taskData.add(data);

                } while (taskCursor.moveToNext());

                taskCursor.close();


            }

            handler.post(() -> {
                taskSQLInterface.setMainTaskData(taskData);
                stopDB();
            });

        });





    }


    private void stopDB() {
        db.close();
        commonDB.close();
    }


}
