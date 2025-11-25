package com.reminder.main.UserInterfaces.HomePage.Tasks;

import static com.reminder.main.SqLite.TaskShared.TaskSharedConstants.TASK_RECEIVED_TABLE_NAME;
import static com.reminder.main.SqLite.TaskShared.TaskSharedConstants.TASK_WEB_ID;
import static com.reminder.main.SqLite.TaskStatus.TaskStatusConstants.DOWNLOADED;
import static com.reminder.main.SqLite.TaskStatus.TaskStatusConstants.DOWNLOADED_YES_BYTE;
import static com.reminder.main.SqLite.TaskStatus.TaskStatusConstants.TASK_STATUS_TABLE_NAME;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import androidx.preference.PreferenceManager;

import com.reminder.main.Custom.CustomInterfaces;
import com.reminder.main.SqLite.CommonDB.CommonDB;
import com.reminder.main.SqLite.TaskShared.TaskSharedConstants;
import com.reminder.main.SqLite.TaskStatus.TaskStatusConstants;
import com.reminder.main.SqLite.Tasks.TaskConstants;
import com.reminder.main.SqLite.Tasks.TaskData;

import java.util.ArrayList;
import java.util.Calendar;

public class TaskSqlData {


    private final SQLiteDatabase database;
    private final CommonDB commonDB;
    private final CustomInterfaces.TaskSQLInterface taskSqlInterface;
    private final ArrayList<NavBarDateTemplate> navDateArray = new ArrayList<>();
    private final ArrayList<ArrayList<TaskData>> navDateArrayAn = new ArrayList<>();
    private ArrayList<TaskData> childArray;
    private String navDateCon = null;
    private final Context context;
    private Cursor cursor = null;
    int dayOfWeek, date, month, year;
    String sum;
    private boolean upcomingAlarmDate = false;
    private final Calendar calendar = Calendar.getInstance();
    boolean pinTaskOnTop;


    public TaskSqlData(Context context, CustomInterfaces.TaskSQLInterface taskSqlInterface, boolean pinTaskOnTop) {
        this.taskSqlInterface = taskSqlInterface;
        this.commonDB = new CommonDB(context);
        this.context = context;
        this.database = commonDB.getReadableDatabase();
        this.pinTaskOnTop = pinTaskOnTop;
    }


    public void getSQLData() {
        new Thread(() -> {

            ArrayList<TaskData> taskData = new ArrayList<>();

            try {
                cursor = database.rawQuery(
                        "SELECT " +

                                " tasks."+TaskConstants.PRIVATE + ", " +      // 0
                                " tasks."+TaskConstants.TOPIC + ", " +        // 1
                                " tasks."+TaskConstants.PRIORITY + ", " +     // 2
                                " tasks."+TaskConstants.REPEAT_STATUS + ", " +// 3
                                " tasks."+TaskConstants.DATE_ARRAY + ", " +   // 4
                                " tasks."+TaskConstants.REPEATING_ALARM_DATE + ", " + // 5
                                " tasks."+TaskConstants.PINNED + ", " +       // 6
                                " tasks."+TaskConstants.ALREADY_DONE + ", " + // 7
                                " tasks."+TaskConstants.TASK_ID + ", " +      // 8
                                " tasks."+TaskConstants.ID + ", " +           // 9
                                " tasks."+TaskConstants.ALARM_DATE + ", " +   // 10
                                " tasks."+TaskConstants.TASK_WEB_ID + ", " +  // 11
                                " COALESCE(taskStatus."+DOWNLOADED+", "+ DOWNLOADED_YES_BYTE +") AS " + DOWNLOADED + // <— DEFAULT VALUE ADDED

                                " FROM " + TaskConstants.TASK_TABLE_NAME + " as tasks " +

                                " LEFT JOIN (" +
                                " SELECT " +
                                " taskReceived."+TASK_WEB_ID + ", " +
                                " COALESCE(ts."+DOWNLOADED+", 1) AS "+DOWNLOADED + // <— DEFAULT INSIDE SUBQUERY ALSO
                                " FROM " + TASK_RECEIVED_TABLE_NAME + " as taskReceived " +
                                " LEFT JOIN " + TASK_STATUS_TABLE_NAME + " as ts " +
                                " ON taskReceived."+TASK_WEB_ID+" = ts."+TASK_WEB_ID +  // <-- missing join condition fixed
                                " GROUP BY taskReceived."+TASK_WEB_ID +
                                ") as taskStatus " +

                                " ON tasks." + TASK_WEB_ID + " = taskStatus." + TASK_WEB_ID +

                                " WHERE " +
                                " COALESCE(taskStatus."+DOWNLOADED+", "+DOWNLOADED_YES_BYTE+") = " + DOWNLOADED_YES_BYTE +
                                " AND " +
                                "tasks." + TaskConstants.PRIVATE + " != " + TaskConstants.PRIVATE_YES
                                +
                                (
                                        PreferenceManager.getDefaultSharedPreferences(context).getBoolean("rmvTask", false)
                                                ?
                                                " AND tasks." + TaskConstants.ALREADY_DONE + " != " + TaskConstants.ALREADY_DONE_YES_BYTE
                                                :
                                                ""
                                )
                                +
                                (
                                        pinTaskOnTop
                                                ?
                                                " AND tasks." + TaskConstants.PINNED + " = " + TaskConstants.PINNED_NO
                                                :
                                                ""
                                )
                                +
                                " ORDER BY tasks." + TaskConstants.REPEATING_ALARM_DATE
                        , null);

            }
            catch (SQLiteException e) {
                Log.e("TAG", "getSQLData: " + e );
            }


            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();

                if (pinTaskOnTop) {
                    do {
                        TaskData data = new TaskData();
                        data.setPrivateTask((byte) cursor.getInt(0));
                        data.setTopic(cursor.getString(1));
                        data.setPriority((byte) 2);
                        data.setRepeatStatus((byte) cursor.getInt(3));
                        data.setDateArray(cursor.getString(4));
                        data.setRepeatingAlarmDate(cursor.getLong(5));
                        data.setPinned((byte) cursor.getInt(6));
                        data.setAlreadyDone((byte) cursor.getInt(7));
                        data.setTaskId(cursor.getString(8));

                        calendar.setTimeInMillis(data.getRepeatingAlarmDate());
                        data.setHour((byte) (calendar.get(Calendar.HOUR) == 0 ? 12 : calendar.get(Calendar.HOUR)));
                        data.setMinute((byte) calendar.get(Calendar.MINUTE));
                        data.setAmPm((byte) calendar.get(Calendar.AM_PM));


                        data.setId(cursor.getInt(9));
                        data.setAlarmDate(cursor.getLong(10));
                        data.setTaskWebId(cursor.getString(11));

                        if (data.getRepeatingAlarmDate() > Calendar.getInstance().getTimeInMillis() && !upcomingAlarmDate) {
                            upcomingAlarmDate = true;
                            taskSqlInterface.setUpComingTask(data, cursor.getPosition());
                        }

                        dayDateNavArray(cursor.getPosition(), data, calendar);
                        if (data.getPinned() != TaskConstants.PINNED_YES) taskData.add(data);

                    } while (cursor.moveToNext());

                } else {
                    do {
                        TaskData data = new TaskData();
                        data.setPrivateTask((byte) cursor.getInt(0));
                        data.setTopic(cursor.getString(1));
                        data.setPriority((byte) 2);
                        data.setRepeatStatus((byte) cursor.getInt(3));
                        data.setDateArray(cursor.getString(4));
                        data.setRepeatingAlarmDate(cursor.getLong(5));
                        data.setPinned((byte) cursor.getInt(6));
                        data.setAlreadyDone((byte) cursor.getInt(7));
                        data.setTaskId(cursor.getString(8));

                        calendar.setTimeInMillis(data.getRepeatingAlarmDate());
                        data.setHour((byte) (calendar.get(Calendar.HOUR) == 0 ? 12 : calendar.get(Calendar.HOUR)));
                        data.setMinute((byte) calendar.get(Calendar.MINUTE));
                        data.setAmPm((byte) calendar.get(Calendar.AM_PM));


                        data.setId(cursor.getInt(9));
                        data.setAlarmDate(cursor.getLong(10));
                        data.setTaskWebId(cursor.getString(11));

                        if (data.getRepeatingAlarmDate() > Calendar.getInstance().getTimeInMillis() && !upcomingAlarmDate) {
                            upcomingAlarmDate = true;
                            taskSqlInterface.setUpComingTask(data, cursor.getPosition());
                        }

                        dayDateNavArray(cursor.getPosition(), data, calendar);
                        taskData.add(data);

                    } while (cursor.moveToNext());

                }
                cursor.close();


            }


            if (!upcomingAlarmDate) taskSqlInterface.setUpComingTask(null, -1);

            taskSqlInterface.setMainTaskData(taskData);
            taskSqlInterface.setNavDateTask(navDateArray);
            taskSqlInterface.setFilteredTask(navDateArrayAn);

            commonDB.close();
            database.close();

        }).start();

    }



    private void dayDateNavArray(int position, TaskData taskData, Calendar calendar) {

        dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        date = calendar.get(Calendar.DAY_OF_MONTH);
        month = calendar.get(Calendar.MONTH);
        year = calendar.get(Calendar.YEAR);

        sum = ((String.valueOf(dayOfWeek) + date) + month) + year;

        if (navDateCon == null) {
            childArray = new ArrayList<>();
            childArray.add(taskData);

            navDateArray.add(new NavBarDateTemplate(dayOfWeek, date, month, calendar.getTimeInMillis()));
            navDateCon = sum;
        } else if (sum.equals(navDateCon)) {
            childArray.add(taskData);
        } else {
            navDateArrayAn.add(childArray);
            childArray = new ArrayList<>();
            childArray.add(taskData);

            navDateCon = sum;
            navDateArray.add(new NavBarDateTemplate(dayOfWeek, date, month, calendar.getTimeInMillis()));

        }

        if (position == (cursor.getCount() - 1)) {
            navDateArrayAn.add(childArray);
        }


    }








}
