package com.reminder.main.UserInterfaces.PeoplePage.MainActivity;

import static com.reminder.main.SqLite.Request.RequestConstants.REQUEST_TYPE_SENT_OR_RECEIVED;
import static com.reminder.main.SqLite.Request.RequestConstants.STATUS;
import static com.reminder.main.SqLite.Request.RequestConstants.STATUS_ACCEPTED_BYTE;
import static com.reminder.main.SqLite.Request.RequestConstants.STATUS_PENDING_BYTE;
import static com.reminder.main.SqLite.Request.RequestConstants.USER_PRIMARY_ID;
import static com.reminder.main.SqLite.UserDetails.UserDetailsConstant.USER_NAME;
import static com.reminder.main.SqLite.UserDetails.UserDetailsConstant.USER_PROFESSION;
import static com.reminder.main.SqLite.UserDetails.UserDetailsConstant.USER_PROFILE_PIC;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.reminder.main.Other.ApplicationCustomInterfaces;
import com.reminder.main.SqLite.CommonDB.CommonDB;
import com.reminder.main.SqLite.Request.RequestConstants;
import com.reminder.main.SqLite.Request.RequestData;
import com.reminder.main.SqLite.UserDetails.UserDetailsConstant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PeopleSQLData {

    private final Context context;
    private CommonDB commonDB;
    private SQLiteDatabase db;
    private final ApplicationCustomInterfaces.PeopleData peopleData;
    private final Handler handler = new Handler(Looper.getMainLooper());


    public PeopleSQLData (Context context, ApplicationCustomInterfaces.PeopleData peopleData) {
        this.peopleData = peopleData;
        this.context = context;
    }


    public void getPeopleData() {

        startDB();

        Thread thread1 = setPeoplePendingAndAcceptedData(db);
        Thread thread2 = setPeopleAcceptedData(db);
        Thread thread3 = setPeoplePendingData(db);

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

        thread3.start();
        try {
            thread3.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }


    private void startDB() {
        commonDB = new CommonDB(context);
        db = commonDB.getReadableDatabase();

    }


    private Thread setPeoplePendingAndAcceptedData(SQLiteDatabase db) {
        return new Thread(() -> {

            Cursor cursor = db.rawQuery(" SELECT * FROM " + RequestConstants.REQUEST_TABLE_NAME, null);

            Map<String, RequestData> requestData = new HashMap<>();

            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    RequestData data = new RequestData();
                    data.setUserPrimaryId(cursor.getString(1));
                    data.setRequestType((byte) cursor.getInt(2));
                    data.setStatus((byte) cursor.getInt(3));
                    requestData.put(data.getUserPrimaryId(), data);
                }
                while (cursor.moveToNext());
            }

            handler.post(() -> peopleData.setPeoplePendingAndAcceptedData(requestData));

            cursor.close();

        });
    }


    private Thread setPeoplePendingData(SQLiteDatabase db) {

        return new Thread(() -> {

            Cursor cursor = db.rawQuery(
                    "SELECT " +
                            "u."+USER_NAME + ", " +                         // 0
                            "u."+USER_PROFESSION + ", " +                   // 1
                            "u."+USER_PROFILE_PIC + ", " +                  // 2
                            "r."+USER_PRIMARY_ID + ", " +                   // 3
                            "r."+STATUS + ", " +                            // 4
                            "r."+REQUEST_TYPE_SENT_OR_RECEIVED +            // 5
                        " FROM " + RequestConstants.REQUEST_TABLE_NAME  + " as r " +
                        " LEFT JOIN " + UserDetailsConstant.USER_DETAILS_TABLE_NAME + " as u " +
                        " ON " + "r."+USER_PRIMARY_ID + "=" + "u."+USER_PRIMARY_ID +
                        " WHERE " + " r."+STATUS+"="+STATUS_PENDING_BYTE,
                    null);

            ArrayList<PeoplePendingOrAcceptedData> requestData = new ArrayList<>();

            if (cursor.getCount() > 0) {

                cursor.moveToFirst();

                do {
                    PeoplePendingOrAcceptedData data = new PeoplePendingOrAcceptedData();
                    data.USER_DATA.setName(cursor.getString(0));
                    data.USER_DATA.setProfession(cursor.getString(1));
                    data.USER_DATA.setProfilePic(cursor.getString(2));
                    data.USER_DATA.setUserPrimaryId(cursor.getString(3));
                    data.REQUEST_DATA.setUserPrimaryId(cursor.getString(3));
                    data.REQUEST_DATA.setStatus((byte) cursor.getInt(4));
                    data.REQUEST_DATA.setRequestType((byte) cursor.getInt(5));

                    Log.d("TAG", "getRequestDataFromSQLite: " + cursor.getString(0));
                    Log.d("TAG", "getRequestDataFromSQLite: " + data.USER_DATA.getProfession());
                    Log.d("TAG", "getRequestDataFromSQLite: " + data.REQUEST_DATA.getRequestType());

                    requestData.add(data);
                }
                while (cursor.moveToNext());



            }

            handler.post(() -> peopleData.setPeoplePendingData(requestData));

            cursor.close();

        });

    }


    private Thread setPeopleAcceptedData(SQLiteDatabase db) {
        return new Thread(() -> {
            Cursor cursor = db.rawQuery(
                    "SELECT " +
                            "u."+USER_NAME + ", " +                         // 0
                            "u."+USER_PROFESSION + ", " +                   // 1
                            "u."+USER_PROFILE_PIC + ", " +                  // 2
                            "r."+USER_PRIMARY_ID + ", " +                   // 3
                            "r."+STATUS + ", " +                            // 4
                            "r."+REQUEST_TYPE_SENT_OR_RECEIVED +            // 5
                            " FROM " + RequestConstants.REQUEST_TABLE_NAME  + " as r " +
                            " LEFT JOIN " + UserDetailsConstant.USER_DETAILS_TABLE_NAME + " as u " +
                            " ON " + "r."+USER_PRIMARY_ID + "=" + "u."+USER_PRIMARY_ID +
                            " WHERE " + " r."+STATUS+"="+STATUS_ACCEPTED_BYTE,
                    null);

            ArrayList<PeoplePendingOrAcceptedData> requestData = new ArrayList<>();

            if (cursor.getCount() > 0) {

                cursor.moveToFirst();

                do {
                    PeoplePendingOrAcceptedData data = new PeoplePendingOrAcceptedData();
                    data.USER_DATA.setName(cursor.getString(0));
                    data.USER_DATA.setProfession(cursor.getString(1));
                    data.USER_DATA.setProfilePic(cursor.getString(2));
                    data.USER_DATA.setUserPrimaryId(cursor.getString(3));
                    data.REQUEST_DATA.setUserPrimaryId(cursor.getString(3));
                    data.REQUEST_DATA.setStatus((byte) cursor.getInt(4));
                    data.REQUEST_DATA.setRequestType((byte) cursor.getInt(5));

                    Log.d("TAG", "getRequestDataFromSQLite: " + cursor.getString(0));
                    Log.d("TAG", "getRequestDataFromSQLite: " + data.USER_DATA.getProfession());
                    Log.d("TAG", "getRequestDataFromSQLite: " + data.REQUEST_DATA.getRequestType());

                    requestData.add(data);
                }
                while (cursor.moveToNext());

            }

            handler.post(() -> {
                peopleData.setPeopleAcceptedData(requestData);
                stopDB();
            });
            cursor.close();



        });
    }


    private void stopDB() {
            db.close();
            commonDB.close();
    }


}
