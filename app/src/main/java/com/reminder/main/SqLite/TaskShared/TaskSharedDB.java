package com.reminder.main.SqLite.TaskShared;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.reminder.main.SqLite.CommonDB.CommonDB;
import com.reminder.main.SqLite.Tasks.TaskConstants;

import java.util.ArrayList;

public class TaskSharedDB {



    public static void insertOrUpdateMultipleTaskShared(Context context, ArrayList<ContentValues> values, String tableName) {

        //byte sharedType = Objects.equals(tableName, TaskSharedConstants.TASK_SENT_TABLE_NAME) ? TaskSharedConstants.TASK_SHARED_TYPE_SENT : TaskSharedConstants.TASK_SHARED_TYPE_RECEIVED;


        CommonDB commonDB = new CommonDB(context);

        SQLiteDatabase db = commonDB.getWritableDatabase();
        db.beginTransaction();

        Log.d("TAG", "insertOrUpdateMultipleTask: **inserting**: ");

        for (int i = 0; i < values.size(); i++) {

            //values.get(i).put(TaskSharedConstants.SHARED_TYPE, sharedType);

            if (db.insert(tableName, null, values.get(i)) == -1) {
                Log.d("TAG", "insertOrUpdateMultipleTask: **updating**: ");
                db.update(tableName, values.get(i), TaskSharedConstants.TASK_WEB_ID+"=? AND " + TaskSharedConstants.USER_PRIMARY_ID+"=? ", new String[]{values.get(i).getAsString(TaskConstants.TASK_WEB_ID), values.get(i).getAsString(TaskConstants.USER_PRIMARY_ID)});
                Log.d("TAG", "insertOrUpdateMultipleTask: **updated /// **: ");
            }

        }

        db.setTransactionSuccessful();
        db.endTransaction();

        commonDB.close();

    }










    public static void insertOrUpdateSingleTaskShared(Context context, ContentValues values, String tableName, String taskWebId) {

        //byte sharedType = Objects.equals(tableName, TaskSharedConstants.TASK_SENT_TABLE_NAME) ? TaskSharedConstants.TASK_SHARED_TYPE_SENT : TaskSharedConstants.TASK_SHARED_TYPE_RECEIVED;


        CommonDB commonDB = new CommonDB(context);

        SQLiteDatabase db = commonDB.getWritableDatabase();

        Log.d("TAG", "insertOrUpdateMultipleTask: **inserting**: ");

        //values.put(TaskSharedConstants.SHARED_TYPE, sharedType);

        if (db.insert(tableName, null, values) == -1) {
            Log.d("TAG", "insertOrUpdateMultipleTask: **updating**: ");
            db.update(tableName, values, TaskSharedConstants.TASK_WEB_ID+"=?", new String[]{taskWebId});
            Log.d("TAG", "insertOrUpdateMultipleTask: **updated /// **: ");
        }


        commonDB.close();



    }







    public static void insertOrUpdateSingleTaskSharedStatus(Context context, ContentValues values, String taskWebId, String uid) {

        CommonDB commonDB = new CommonDB(context);

        SQLiteDatabase db = commonDB.getWritableDatabase();

        Log.d("TAG", "insertOrUpdateMultipleTask: **updating**: ");

        if (uid == null) {

            // for taskSentTable
            db.update(TaskSharedConstants.TASK_SENT_TABLE_NAME, values, TaskSharedConstants.TASK_WEB_ID+"=?", new String[]{taskWebId});

            // for taskReceivedTable
            db.update(TaskSharedConstants.TASK_RECEIVED_TABLE_NAME, values, TaskSharedConstants.TASK_WEB_ID+"=?", new String[]{taskWebId});

        }
        else {

            // for taskSentTable
            db.update(TaskSharedConstants.TASK_SENT_TABLE_NAME, values, TaskSharedConstants.TASK_WEB_ID+"=? AND "  +TaskSharedConstants.USER_PRIMARY_ID + "=?", new String[]{taskWebId, uid});

            // for taskReceivedTable
            db.update(TaskSharedConstants.TASK_RECEIVED_TABLE_NAME, values, TaskSharedConstants.TASK_WEB_ID+"=? AND "  +TaskSharedConstants.USER_PRIMARY_ID + "=?", new String[]{taskWebId, uid});
        }


        commonDB.close();



    }






}
