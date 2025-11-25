package com.reminder.main.UserInterfaces.UserTaskInbox.MainActivity;

import static com.reminder.main.SqLite.TaskShared.TaskSharedConstants.TASK_RECEIVED_TABLE_NAME;
import static com.reminder.main.SqLite.TaskShared.TaskSharedConstants.TASK_SENT_TABLE_NAME;
import static com.reminder.main.SqLite.TaskStatus.TaskStatusConstants.DOWNLOADED;
import static com.reminder.main.SqLite.TaskStatus.TaskStatusConstants.TASK_STATUS_TABLE_NAME;
import static com.reminder.main.SqLite.Tasks.TaskConstants.ALARM_DATE;
import static com.reminder.main.SqLite.Tasks.TaskConstants.ALREADY_DONE;
import static com.reminder.main.SqLite.Tasks.TaskConstants.DATE_ARRAY;
import static com.reminder.main.SqLite.Tasks.TaskConstants.PINNED;
import static com.reminder.main.SqLite.Tasks.TaskConstants.PRIORITY;
import static com.reminder.main.SqLite.Tasks.TaskConstants.PRIVATE;
import static com.reminder.main.SqLite.Tasks.TaskConstants.PRIVATE_NO;
import static com.reminder.main.SqLite.Tasks.TaskConstants.REPEATING_ALARM_DATE;
import static com.reminder.main.SqLite.Tasks.TaskConstants.REPEAT_STATUS;
import static com.reminder.main.SqLite.Tasks.TaskConstants.TASK_ID;
import static com.reminder.main.SqLite.Tasks.TaskConstants.TASK_TABLE_NAME;
import static com.reminder.main.SqLite.Tasks.TaskConstants.TASK_WEB_ID;
import static com.reminder.main.SqLite.Tasks.TaskConstants.TOPIC;
import static com.reminder.main.SqLite.Tasks.TaskConstants.USER_PRIMARY_ID;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.reminder.main.Custom.CustomInterfaces;
import com.reminder.main.SqLite.CommonDB.CommonDB;

import java.util.ArrayList;
import java.util.Calendar;


public class UserTaskInboxSQLData {
    private final Context context;
    private CommonDB commonDB;
    private SQLiteDatabase db;
    private final String userPrimaryId;
    private final Calendar calendar = Calendar.getInstance();
    private final Handler handler = new Handler(Looper.getMainLooper());

    private final CustomInterfaces.TaskInboxInterface taskInboxInterface;
    public UserTaskInboxSQLData(
            Context context,
            String userPrimaryId,
            CustomInterfaces.TaskInboxInterface taskInboxInterface
    ) {
        this.context = context;
        this.userPrimaryId = userPrimaryId;
        this.taskInboxInterface = taskInboxInterface;
    }

    public void getTaskInboxData() {

        startDB();


        Thread thread1 = getTaskSentData();
        Thread thread2 = getTaskReceivedData();

        thread1.start();
        try {
            thread1.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        thread2.start();
        try {
            thread2.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }


    private void startDB() {
        commonDB = new CommonDB(context);
        db = commonDB.getReadableDatabase();
    }


    private Thread getTaskSentData() {
        return new Thread(() -> {
            Cursor cursor;
            /*
             cursor = db.rawQuery(
                    " SELECT " +
                            " task."+TOPIC + ", " +//----------------------0
                            " task."+PRIORITY + ", " +//-------------------1
                            " task."+REPEAT_STATUS + ", " +//--------------2
                            " task."+DATE_ARRAY + ", " +//-----------------3
                            " task."+REPEATING_ALARM_DATE + ", " +//-------4
                            " task."+PINNED + ", " +//---------------------5
                            " task."+ALREADY_DONE + ", " +//---------------6
                            " task."+TASK_ID + ", " +//--------------------7
                            " task."+ALARM_DATE + ", " +//-----------------8
                            " task."+TASK_WEB_ID + ", " +//----------------9
                            " taskStatus."+DOWNLOADED +//------------------10
                    " FROM " + TASK_SENT_TABLE_NAME + " as taskSent " +
                    " LEFT JOIN " + TASK_TABLE_NAME + " as task " +
                    " ON " + " taskSent."+TASK_WEB_ID +"="+ " task."+TASK_WEB_ID +
                    " LEFT JOIN " + TASK_STATUS_TABLE_NAME + " as taskStatus " +
                    " ON " + " taskSent."+TASK_WEB_ID +"="+ " taskStatus."+TASK_WEB_ID +
                    " WHERE " + " task."+PRIVATE + "=" +PRIVATE_NO + " AND " + " taskSent."+USER_PRIMARY_ID+"="+"\""+userPrimaryId+"\""
                    , null
            );

             */

            cursor = db.rawQuery(
                    " SELECT " +
                            " task."+TOPIC + ", " +//----------------------0
                            " task."+PRIORITY + ", " +//-------------------1
                            " task."+REPEAT_STATUS + ", " +//--------------2
                            " task."+DATE_ARRAY + ", " +//-----------------3
                            " task."+REPEATING_ALARM_DATE + ", " +//-------4
                            " task."+PINNED + ", " +//---------------------5
                            " task."+ALREADY_DONE + ", " +//---------------6
                            " task."+TASK_ID + ", " +//--------------------7
                            " task."+ALARM_DATE + ", " +//-----------------8
                            " task."+TASK_WEB_ID + ", " +//----------------9
                            " taskShared."+DOWNLOADED + ", " +//-----------10
                            " taskShared."+USER_PRIMARY_ID +//-------------11

                            " FROM (" +
                                " SELECT " +
                                    " taskStatus."+TASK_WEB_ID + ", " +
                                    " taskStatus."+USER_PRIMARY_ID + ", " +
                                    " taskStatus."+DOWNLOADED +
                                " FROM " + TASK_SENT_TABLE_NAME + " as taskSent " +
                                " INNER JOIN " + TASK_STATUS_TABLE_NAME + " as taskStatus " +
                                " ON " + " taskSent."+TASK_WEB_ID +"="+ " taskStatus."+TASK_WEB_ID +
                                " WHERE " + " taskSent."+USER_PRIMARY_ID+"="+"\""+userPrimaryId+"\"" +
                            ") as taskShared " +
                            " INNER JOIN " + TASK_TABLE_NAME + " as task " +
                            " ON task."+TASK_WEB_ID + "=" + " taskShared."+TASK_WEB_ID +
                                " WHERE " +
                            " taskShared."+USER_PRIMARY_ID+"="+"\""+userPrimaryId+"\""

                    , null
            );

            ArrayList<UserTaskInboxData> taskSentData = new ArrayList<>();

            if (cursor.getCount() > 0) {

                cursor.moveToFirst();
                Log.d("TAG", "getTaskSentData: " + cursor.getCount());

                do {
                    UserTaskInboxData data = new UserTaskInboxData();

                    data.TASK_DATA.setPrivateTask(PRIVATE_NO);
                    data.TASK_DATA.setTopic(cursor.getString(0));
                    data.TASK_DATA.setPriority((byte) cursor.getInt(1));
                    data.TASK_DATA.setRepeatStatus((byte) cursor.getInt(2));
                    data.TASK_DATA.setDateArray(cursor.getString(3));
                    data.TASK_DATA.setRepeatingAlarmDate(cursor.getLong(4));
                    data.TASK_DATA.setPinned((byte) cursor.getInt(5));
                    data.TASK_DATA.setAlreadyDone((byte) cursor.getInt(6));
                    data.TASK_DATA.setTaskId(cursor.getString(7));

                    calendar.setTimeInMillis(data.TASK_DATA.getRepeatingAlarmDate());
                    data.TASK_DATA.setHour((byte) (calendar.get(Calendar.HOUR) == 0 ? 12 : calendar.get(Calendar.HOUR)));
                    data.TASK_DATA.setMinute((byte) calendar.get(Calendar.MINUTE));
                    data.TASK_DATA.setAmPm((byte) calendar.get(Calendar.AM_PM));


                    data.TASK_DATA.setAlarmDate(cursor.getLong(8));
                    data.TASK_DATA.setTaskWebId(cursor.getString(9));
                    data.TASK_STATUS_DATA.setTaskWebId(cursor.getString(9));
                    data.TASK_STATUS_DATA.setDownloaded((byte) cursor.getInt(10));

                    Log.d("TAG", "getTaskSentData: " + data.TASK_DATA.getTopic());
                    taskSentData.add(data);
                }
                while (cursor.moveToNext());



            }

            handler.post(() -> {
                taskInboxInterface.setTaskSentList(taskSentData);
                cursor.close();
            });
        });
    }


    private Thread getTaskReceivedData() {
        return new Thread(() -> {
            Cursor cursor;

            /*
            cursor = db.rawQuery(
                    " SELECT " +
                            " task."+TOPIC + ", " +//----------------------0
                            " task."+PRIORITY + ", " +//-------------------1
                            " task."+REPEAT_STATUS + ", " +//--------------2
                            " task."+DATE_ARRAY + ", " +//-----------------3
                            " task."+REPEATING_ALARM_DATE + ", " +//-------4
                            " task."+PINNED + ", " +//---------------------5
                            " task."+ALREADY_DONE + ", " +//---------------6
                            " task."+TASK_ID + ", " +//--------------------7
                            " task."+ALARM_DATE + ", " +//-----------------8
                            " task."+TASK_WEB_ID + ", " +//----------------9
                            " taskStatus."+DOWNLOADED +//------------------10
                            " FROM " + TASK_RECEIVED_TABLE_NAME + " as taskReceived " +
                            " LEFT JOIN " + TASK_TABLE_NAME + " as task " +
                            " ON " + " taskReceived."+TASK_WEB_ID +"="+ " task."+TASK_WEB_ID +
                            " LEFT JOIN " + TASK_STATUS_TABLE_NAME + " as taskStatus " +
                            " ON " + " taskReceived."+TASK_WEB_ID +"="+ " taskStatus."+TASK_WEB_ID +
                            " WHERE " + "task."+PRIVATE + "=" +PRIVATE_NO + " AND " + " taskReceived."+USER_PRIMARY_ID+"="+"\""+userPrimaryId+"\""
                    , null
            );


             */

            cursor = db.rawQuery(
                    " SELECT " +
                            " task."+TOPIC + ", " +//----------------------0
                            " task."+PRIORITY + ", " +//-------------------1
                            " task."+REPEAT_STATUS + ", " +//--------------2
                            " task."+DATE_ARRAY + ", " +//-----------------3
                            " task."+REPEATING_ALARM_DATE + ", " +//-------4
                            " task."+PINNED + ", " +//---------------------5
                            " task."+ALREADY_DONE + ", " +//---------------6
                            " task."+TASK_ID + ", " +//--------------------7
                            " task."+ALARM_DATE + ", " +//-----------------8
                            " task."+TASK_WEB_ID + ", " +//----------------9
                            " taskShared."+DOWNLOADED + ", " +//-----------10
                            " taskShared."+USER_PRIMARY_ID +//-------------11

                            " FROM (" +
                                " SELECT " +
                                    " taskStatus."+TASK_WEB_ID + ", " +
                                    " taskStatus."+USER_PRIMARY_ID + ", " +
                                    " taskStatus."+DOWNLOADED +
                                " FROM " + TASK_RECEIVED_TABLE_NAME + " as taskReceived " +
                                " INNER JOIN " + TASK_STATUS_TABLE_NAME + " as taskStatus " +
                                " ON " + " taskReceived."+TASK_WEB_ID +"="+ " taskStatus."+TASK_WEB_ID +
                                " WHERE " + " taskReceived."+USER_PRIMARY_ID+"="+"\""+userPrimaryId+"\"" +
                            ") as taskShared " +
                            " INNER JOIN " + TASK_TABLE_NAME + " as task " +
                            " ON task."+TASK_WEB_ID + "=" + " taskShared."+TASK_WEB_ID +
                            " WHERE " +
                            " taskShared."+USER_PRIMARY_ID+"="+"\""+userPrimaryId+"\""

                    , null
            );

            if (cursor.getCount() > 0) {
                cursor.moveToFirst();



                ArrayList<UserTaskInboxData> taskReceivedData = new ArrayList<>();

                do {
                    UserTaskInboxData data = new UserTaskInboxData();

                    data.TASK_DATA.setPrivateTask(PRIVATE_NO);
                    data.TASK_DATA.setTopic(cursor.getString(0));
                    data.TASK_DATA.setPriority((byte) cursor.getInt(1));
                    data.TASK_DATA.setRepeatStatus((byte) cursor.getInt(2));
                    data.TASK_DATA.setDateArray(cursor.getString(3));
                    data.TASK_DATA.setRepeatingAlarmDate(cursor.getLong(4));
                    data.TASK_DATA.setPinned((byte) cursor.getInt(5));
                    data.TASK_DATA.setAlreadyDone((byte) cursor.getInt(6));
                    data.TASK_DATA.setTaskId(cursor.getString(7));

                    calendar.setTimeInMillis(data.TASK_DATA.getRepeatingAlarmDate());
                    data.TASK_DATA.setHour((byte) (calendar.get(Calendar.HOUR) == 0 ? 12 : calendar.get(Calendar.HOUR)));
                    data.TASK_DATA.setMinute((byte) calendar.get(Calendar.MINUTE));
                    data.TASK_DATA.setAmPm((byte) calendar.get(Calendar.AM_PM));

                    data.TASK_DATA.setAlarmDate(cursor.getLong(8));
                    data.TASK_DATA.setTaskWebId(cursor.getString(9));
                    data.TASK_STATUS_DATA.setTaskWebId(cursor.getString(9));
                    data.TASK_STATUS_DATA.setDownloaded((byte) cursor.getInt(10));

                    Log.d("TAG", "getTaskReceivedData: " + data.TASK_DATA.getTopic());

                    taskReceivedData.add(data);
                }
                while (cursor.moveToNext());

                handler.post(() -> {
                    taskInboxInterface.setTaskReceivedList(taskReceivedData);
                    cursor.close();

                    stopDB();
                });


            }
        });
    }


    private void stopDB() {
        db.close();
        commonDB.close();
    }




}
