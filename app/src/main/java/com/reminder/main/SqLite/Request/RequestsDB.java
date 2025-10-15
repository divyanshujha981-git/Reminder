package com.reminder.main.SqLite.Request;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.reminder.main.SqLite.CommonDB.CommonDB;

import java.util.ArrayList;

public class RequestsDB {


    public static void deleteRequest(Context context, String tableName, String uid) {

        CommonDB commonDB = new CommonDB(context);

        SQLiteDatabase database = commonDB.getWritableDatabase();

        try {
            database.delete(tableName, RequestConstants.USER_PRIMARY_ID+"=?", new String[]{uid});
        }catch (SQLiteException e) {
            e.printStackTrace();
        }

        database.close();
        commonDB.close();


    }







    public static void insertORUpdateSingleRequest(Context context, String tableName, RequestData data) {

        ContentValues values = new ContentValues();
        values.put(RequestConstants.USER_PRIMARY_ID, data.getUserPrimaryId());
        values.put(RequestConstants.STATUS, data.getStatus());
        values.put(RequestConstants.REQUEST_TYPE_SENT_OR_RECEIVED, data.getRequestType());

        CommonDB commonDB = new CommonDB(context);

        SQLiteDatabase database = commonDB.getWritableDatabase();

        if (database.insert(tableName, null, values) == -1) {
            database.update(tableName, values, RequestConstants.USER_PRIMARY_ID+"=?", new String[]{data.getUserPrimaryId()});
        }

        database.close();
        commonDB.close();


    }



    public static void insertORUpdateSingleRequest(Context context, String tableName, ContentValues values) {

        CommonDB commonDB = new CommonDB(context);

        SQLiteDatabase database = commonDB.getWritableDatabase();

        if (database.insert(tableName, null, values) == -1) {
            database.update(tableName, values, RequestConstants.USER_PRIMARY_ID+"=?", new String[]{values.getAsString(RequestConstants.USER_PRIMARY_ID)});
        }

        database.close();
        commonDB.close();


    }






    public static void insertORUpdateMultipleRequest(Context context, String tableName, ArrayList<RequestData> datas) {

        CommonDB commonDB = new CommonDB(context);

        SQLiteDatabase database = commonDB.getWritableDatabase();
        database.beginTransaction();

        for (RequestData data: datas) {

            ContentValues values = new ContentValues();
            values.put(RequestConstants.USER_PRIMARY_ID, data.getUserPrimaryId());
            if (data.getStatus() != -1) values.put(RequestConstants.STATUS, data.getStatus());
            values.put(RequestConstants.REQUEST_TYPE_SENT_OR_RECEIVED, data.getRequestType());

            if (database.insert(tableName, null, values) == -1) {
                database.update(tableName, values, RequestConstants.USER_PRIMARY_ID+"=?", new String[]{data.getUserPrimaryId()});
            }

        }

        database.setTransactionSuccessful();
        database.endTransaction();

        database.close();
        commonDB.close();


    }


}
