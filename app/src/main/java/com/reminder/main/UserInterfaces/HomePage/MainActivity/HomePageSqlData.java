package com.reminder.main.UserInterfaces.HomePage.MainActivity;

import static com.reminder.main.SqLite.Request.RequestConstants.REQUEST_TABLE_NAME;
import static com.reminder.main.SqLite.Request.RequestConstants.STATUS;
import static com.reminder.main.SqLite.Request.RequestConstants.STATUS_ACCEPTED_BYTE;
import static com.reminder.main.SqLite.Request.RequestConstants.USER_PRIMARY_ID;
import static com.reminder.main.SqLite.TaskShared.TaskSharedConstants.TASK_RECEIVED_TABLE_NAME;
import static com.reminder.main.SqLite.TaskShared.TaskSharedConstants.TASK_WEB_ID;
import static com.reminder.main.SqLite.TaskStatus.TaskStatusConstants.DOWNLOADED;
import static com.reminder.main.SqLite.TaskStatus.TaskStatusConstants.DOWNLOADED_YES_BYTE;
import static com.reminder.main.SqLite.TaskStatus.TaskStatusConstants.TASK_STATUS_TABLE_NAME;
import static com.reminder.main.SqLite.Tasks.TaskConstants.PINNED_YES;
import static com.reminder.main.SqLite.UserDetails.UserDetailsConstant.USER_DETAILS_TABLE_NAME;
import static com.reminder.main.SqLite.UserDetails.UserDetailsConstant.USER_EMAIL;
import static com.reminder.main.SqLite.UserDetails.UserDetailsConstant.USER_NAME;
import static com.reminder.main.SqLite.UserDetails.UserDetailsConstant.USER_PHONE_NUMBER;
import static com.reminder.main.SqLite.UserDetails.UserDetailsConstant.USER_PROFESSION;
import static com.reminder.main.SqLite.UserDetails.UserDetailsConstant.USER_PROFILE_PIC;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.preference.PreferenceManager;

import com.reminder.main.Custom.CustomInterfaces;
import com.reminder.main.R;
import com.reminder.main.SqLite.CommonDB.CommonDB;
import com.reminder.main.SqLite.TaskShared.TaskSharedConstants;
import com.reminder.main.SqLite.Tasks.TaskConstants;
import com.reminder.main.SqLite.Tasks.TaskData;
import com.reminder.main.SqLite.UserDetails.UserDetailsConstant;
import com.reminder.main.SqLite.UserDetails.UserDetailsData;
import com.reminder.main.UserInterfaces.HomePage.Tasks.NavBarDateTemplate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class HomePageSqlData {

    private SQLiteDatabase db;
    private CommonDB commonDB;
    private final CustomInterfaces.TaskSQLInterface taskSqlInterface;
    private final CustomInterfaces.TaskInboxPeopleInterface taskInboxPeopleInterface;
    private final CustomInterfaces.AccountInterface accountInterface;
    private final ArrayList<NavBarDateTemplate> navDateArray = new ArrayList<>();
    private final ArrayList<ArrayList<TaskData>> navDateArrayAn = new ArrayList<>();
    private ArrayList<TaskData> childArray;
    private String navDateCon = null;
    private final Context context;
    private Cursor taskCursor = null;
    private final Handler handler = new Handler(Looper.getMainLooper());
    int dayOfWeek, date, month, year;
    String sum;
    private boolean upcomingAlarmDateFound = false;
    private final Calendar calendar = Calendar.getInstance();
    private boolean pinTaskOnTop;


    public HomePageSqlData(Context context, MainPagePagerAdapter mainPagePagerAdapter) {
        this.context = context;
        taskSqlInterface = mainPagePagerAdapter;
        taskInboxPeopleInterface = mainPagePagerAdapter;
        accountInterface = mainPagePagerAdapter;
    }


    public void getAllData(boolean userSignedIn) {
        startDB();

        Thread thread1 = getPinnedTaskData();
        Thread thread2 = getTaskData(userSignedIn);

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

        if (userSignedIn) {
            Thread thread3 = getTaskInboxData();
            Thread thread4 = getAccountData();

            thread3.start();
            try {
                thread3.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            thread4.start();
            try {
                thread3.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

    }

    private void startDB() {
        commonDB = new CommonDB(context);
        db = commonDB.getReadableDatabase();
    }


    private Thread getPinnedTaskData() {

        return new Thread(() -> {

            pinTaskOnTop = PreferenceManager.getDefaultSharedPreferences(context).getBoolean(context.getString(R.string.pinTaskOnTop), true);

            Cursor cursor = db.rawQuery(" SELECT COUNT(*) FROM " + TaskConstants.TASK_TABLE_NAME + " WHERE " + TaskConstants.PINNED + "=" + PINNED_YES, null);
            cursor.moveToFirst();

            handler.post(() -> {
                taskSqlInterface.setPinnedTaskOnTop(pinTaskOnTop, cursor.getInt(0) > 0);
                cursor.close();
            });


        });


    }


    private Thread getTaskData(boolean userSignedIn) {

        return new Thread(() -> {

            navDateArray.clear();
            navDateArrayAn.clear();

            ArrayList<TaskData> taskData = new ArrayList<>();

            try {
                taskCursor = db.rawQuery(
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


            if (taskCursor != null && taskCursor.getCount() > 0) {
                taskCursor.moveToFirst();

                do {

                    byte pinned = (byte) taskCursor.getInt(6);
                    if (pinTaskOnTop && pinned == PINNED_YES) continue;

                    TaskData data = new TaskData();
                    data.setPrivateTask((byte) taskCursor.getInt(0));
                    data.setTopic(taskCursor.getString(1));
                    data.setPriority((byte) 2);
                    data.setRepeatStatus((byte) taskCursor.getInt(3));
                    data.setDateArray(taskCursor.getString(4));
                    data.setRepeatingAlarmDate(taskCursor.getLong(5));
                    data.setPinned(pinned);
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

                    if (data.getRepeatingAlarmDate() > Calendar.getInstance().getTimeInMillis() && !upcomingAlarmDateFound) {
                        upcomingAlarmDateFound = true;
                        taskSqlInterface.setUpComingTask(data, taskCursor.getPosition());
                    }

                    dayDateNavArray(taskCursor.getPosition(), data, calendar);

                    taskData.add(data);

                } while (taskCursor.moveToNext());

                taskCursor.close();


            }


            if (!upcomingAlarmDateFound) taskSqlInterface.setUpComingTask(null, -1);

            handler.post(() -> {
                taskSqlInterface.setMainTaskData(taskData);
                taskSqlInterface.setNavDateTask(navDateArray);
                taskSqlInterface.setFilteredTask(navDateArrayAn);
                if (!userSignedIn) stopDB();
            });


        });

    }



    private Thread getTaskInboxData() {
        return new Thread(() -> {
            Cursor taskInboxCursor = db.rawQuery(
                    "SELECT " +
                            "u."+USER_NAME + ", " +                         // 0
                            "u."+USER_PROFESSION + ", " +                   // 1
                            "u."+USER_PROFILE_PIC + ", " +                  // 2
                            "r."+USER_PRIMARY_ID +                          // 3
                            " FROM " + REQUEST_TABLE_NAME  + " as r " +
                            " LEFT JOIN " + USER_DETAILS_TABLE_NAME + " as u " +
                            " ON " + "r."+USER_PRIMARY_ID + "=" + "u."+USER_PRIMARY_ID +
                            " WHERE " + " r."+STATUS+"="+STATUS_ACCEPTED_BYTE,
                    null);

            ArrayList<UserDetailsData> taskInboxPeopleData = new ArrayList<>();

            if (taskInboxCursor.getCount() > 0) {

                taskInboxCursor.moveToFirst();

                do {
                    UserDetailsData data = new UserDetailsData();
                    data.setName(taskInboxCursor.getString(0));
                    data.setProfession(taskInboxCursor.getString(1));
                    data.setProfilePic(taskInboxCursor.getString(2));
                    data.setUserPrimaryId(taskInboxCursor.getString(3));

                    taskInboxPeopleData.add(data);
                }
                while (taskInboxCursor.moveToNext());

            }

            handler.post(() -> taskInboxPeopleInterface.getTaskInboxPeopleData(taskInboxPeopleData));
            taskInboxCursor.close();

        });
    }



    private Thread getAccountData() {

        return new Thread(() -> {

            assert MainActivity.FIREBASE_AUTH.getCurrentUser() != null;
            Cursor cursor = db.rawQuery(
                    " SELECT " +
                            " subQuery1.c AS taskSent, " +                  // 0
                            " subQuery2.c AS taskReceived, " +              // 1
                            " subQuery3.c AS completed, " +                 // 2
                            " subQuery4.c AS pending, " +                   // 3
                            " subQuery5.uName AS uName, " +                 // 4
                            " subQuery5.uEmail AS uEmail, " +               // 5
                            " subQuery5.uPhone AS uPhone, " +               // 6
                            " subQuery5.uProfession AS uProfession, " +     // 7
                            " subQuery5.uPic AS uPic " +                    // 8

                            " FROM " +

                            " (SELECT count(*) AS c FROM (" +
                            " SELECT " + TaskSharedConstants.TASK_WEB_ID + " FROM " + TaskSharedConstants.TASK_SENT_TABLE_NAME + " GROUP BY " + TaskSharedConstants.TASK_WEB_ID +
                            ")) AS subQuery1, " +

                            " (SELECT count(*) AS c FROM (" +
                            " SELECT " + TaskConstants.TASK_WEB_ID + " FROM " + TaskSharedConstants.TASK_RECEIVED_TABLE_NAME + " GROUP BY " + TaskConstants.TASK_WEB_ID +
                            ")) AS subQuery2, " +

                            " (SELECT count(*) AS c FROM (" +
                            " SELECT twi FROM (" +
                            " SELECT " + TaskSharedConstants.TASK_SENT_TABLE_NAME+"."+TaskSharedConstants.TASK_WEB_ID + " AS twi FROM " + TaskSharedConstants.TASK_SENT_TABLE_NAME +
                            " LEFT JOIN " + TaskConstants.TASK_TABLE_NAME +
                            " ON " + TaskSharedConstants.TASK_SENT_TABLE_NAME+"."+TaskSharedConstants.TASK_WEB_ID + "=" + TaskConstants.TASK_TABLE_NAME+"."+TaskSharedConstants.TASK_WEB_ID +
                            " WHERE " + TaskConstants.TASK_TABLE_NAME+"."+TaskConstants.ALREADY_DONE + "=" + TaskConstants.ALREADY_DONE_YES_BYTE
                            + " UNION ALL " +
                            " SELECT " + TaskSharedConstants.TASK_RECEIVED_TABLE_NAME+"."+TaskSharedConstants.TASK_WEB_ID + " AS twi FROM " + TaskSharedConstants.TASK_RECEIVED_TABLE_NAME +
                            " LEFT JOIN " + TaskConstants.TASK_TABLE_NAME +
                            " ON " + TaskSharedConstants.TASK_RECEIVED_TABLE_NAME+"."+TaskSharedConstants.TASK_WEB_ID + "=" + TaskConstants.TASK_TABLE_NAME+"."+TaskSharedConstants.TASK_WEB_ID +
                            " WHERE " + TaskConstants.TASK_TABLE_NAME+"."+TaskConstants.ALREADY_DONE + "=" + TaskConstants.ALREADY_DONE_YES_BYTE +
                            ") AS subSubQuery3 GROUP BY twi " +
                            ")) AS subQuery3, " +

                            " (SELECT count(*) AS c FROM (" +
                            " SELECT twi FROM (" +
                            " SELECT " + TaskSharedConstants.TASK_SENT_TABLE_NAME+"."+TaskSharedConstants.TASK_WEB_ID + " AS twi FROM " + TaskSharedConstants.TASK_SENT_TABLE_NAME +
                            " LEFT JOIN " + TaskConstants.TASK_TABLE_NAME +
                            " ON " + TaskSharedConstants.TASK_SENT_TABLE_NAME+"."+TaskSharedConstants.TASK_WEB_ID + "=" + TaskConstants.TASK_TABLE_NAME+"."+TaskSharedConstants.TASK_WEB_ID +
                            " WHERE " + TaskConstants.TASK_TABLE_NAME+"."+TaskConstants.ALREADY_DONE + "=" + TaskConstants.ALREADY_DONE_YES_BYTE
                            + " UNION ALL " +
                            " SELECT " + TaskSharedConstants.TASK_RECEIVED_TABLE_NAME+"."+TaskSharedConstants.TASK_WEB_ID + " AS twi FROM " + TaskSharedConstants.TASK_RECEIVED_TABLE_NAME + " " +
                            " LEFT JOIN " + TaskConstants.TASK_TABLE_NAME +
                            " ON " + TaskSharedConstants.TASK_RECEIVED_TABLE_NAME+"."+TaskSharedConstants.TASK_WEB_ID + "=" + TaskConstants.TASK_TABLE_NAME+"."+TaskSharedConstants.TASK_WEB_ID +
                            " WHERE " + TaskConstants.TASK_TABLE_NAME+"."+TaskConstants.ALREADY_DONE + "=" + TaskConstants.ALREADY_DONE_NO_BYTE +
                            ") AS subSubQuery4 GROUP BY twi " +
                            ")) AS subQuery4, " +

                            " ( SELECT " +
                            UserDetailsConstant.USER_NAME + " AS uName, " +
                            UserDetailsConstant.USER_EMAIL + " AS uEmail, " +
                            UserDetailsConstant.USER_PHONE_NUMBER + " AS uPhone, " +
                            UserDetailsConstant.USER_PROFESSION + " AS uProfession, " +
                            UserDetailsConstant.USER_PROFILE_PIC +" AS uPic " +
                            " FROM " + USER_DETAILS_TABLE_NAME +
                            " WHERE " + UserDetailsConstant.USER_PRIMARY_ID  + " = \"" + MainActivity.FIREBASE_AUTH.getCurrentUser().getUid() + "\"" +
                            ") AS subQuery5 "

                    , null);



            if (cursor.getCount() > 0) {

                cursor.moveToFirst();
                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    Log.d("TAG", "getData: " + cursor.getColumnName(i) + " = " + cursor.getString(i));
                }

                int taskSentCount = cursor.getInt(0);
                int taskReceivedCount = cursor.getInt(1);
                int taskCompletedCount = cursor.getInt(2);
                int taskPendingCount = cursor.getInt(3);

//                userData.setName(cursor.getString(4));
//                userData.setEmail(cursor.getString(5));
//                userData.setPhoneNumber(cursor.getString(6));
//                userData.setProfession(cursor.getString(7));
//                userData.setProfilePic(cursor.getString(8));

                Map<String, Object> accountData = new HashMap<>();

                accountData.put(context.getString(R.string.sent), cursor.getInt(0));
                accountData.put(context.getString(R.string.received), cursor.getInt(1));
                accountData.put(context.getString(R.string.completed), cursor.getInt(2));
                accountData.put(context.getString(R.string.pending), cursor.getInt(3));
                accountData.put(USER_NAME, cursor.getString(4));
                accountData.put(USER_EMAIL, cursor.getString(5));
                accountData.put(USER_PHONE_NUMBER, cursor.getString(6));
                accountData.put(USER_PROFESSION, cursor.getString(7));
                accountData.put(USER_PROFILE_PIC, cursor.getString(8));


                handler.post(() -> {
                    accountInterface.getAccountData(accountData);
                    stopDB();
                });

                cursor.close();


                Log.d("TAG", "getTaskData: " + taskSentCount);
                Log.d("TAG", "getTaskData: " + taskReceivedCount);
                Log.d("TAG", "getTaskData: " + taskCompletedCount);
                Log.d("TAG", "getTaskData: " + taskPendingCount);

            }


        });


    }


    public void getTaskData() {

        navDateArray.clear();
        navDateArrayAn.clear();

        startDB();

        ArrayList<TaskData> taskData = new ArrayList<>();

        try {
            taskCursor = db.rawQuery(
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


        if (taskCursor != null && taskCursor.getCount() > 0) {
            taskCursor.moveToFirst();

            do {

                byte pinned = (byte) taskCursor.getInt(6);
                if (pinTaskOnTop && pinned == PINNED_YES) continue;

                TaskData data = new TaskData();
                data.setPrivateTask((byte) taskCursor.getInt(0));
                data.setTopic(taskCursor.getString(1));
                data.setPriority((byte) 2);
                data.setRepeatStatus((byte) taskCursor.getInt(3));
                data.setDateArray(taskCursor.getString(4));
                data.setRepeatingAlarmDate(taskCursor.getLong(5));
                data.setPinned(pinned);
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

                if (data.getRepeatingAlarmDate() > Calendar.getInstance().getTimeInMillis() && !upcomingAlarmDateFound) {
                    upcomingAlarmDateFound = true;
                    taskSqlInterface.setUpComingTask(data, taskCursor.getPosition());
                }

                dayDateNavArray(taskCursor.getPosition(), data, calendar);

                taskData.add(data);

            } while (taskCursor.moveToNext());

            taskCursor.close();


        }

        if (!upcomingAlarmDateFound) taskSqlInterface.setUpComingTask(null, -1);

        taskSqlInterface.setMainTaskData(taskData);
        taskSqlInterface.setNavDateTask(navDateArray);
        taskSqlInterface.setFilteredTask(navDateArrayAn);

        stopDB();

    }



    private void stopDB() {
        db.close();
        commonDB.close();
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

        if (position == (taskCursor.getCount() - 1)) {
            navDateArrayAn.add(childArray);
        }


    }








}
