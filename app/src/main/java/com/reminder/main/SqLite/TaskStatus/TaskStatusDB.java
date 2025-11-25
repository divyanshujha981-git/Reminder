package com.reminder.main.SqLite.TaskStatus;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.reminder.main.Firebase.FirebaseConstants;
import com.reminder.main.SqLite.CommonDB.CommonDB;

import java.util.ArrayList;




public class TaskStatusDB {





    public static void insertOrUpdateMultipleTaskStatus(Context context, ArrayList<ContentValues> data) {
        CommonDB commonDB = new CommonDB(context);
        SQLiteDatabase db = commonDB.getWritableDatabase();
        db.beginTransaction();

        for (ContentValues data1: data) {
            try {
                db.insert(TaskStatusConstants.TASK_STATUS_TABLE_NAME, null, data1);
            }
            catch (Exception e) {
                db.update(
                        TaskStatusConstants.TASK_STATUS_TABLE_NAME,
                        data1,
                        TaskStatusConstants.TASK_WEB_ID+"=?" + (data1.get(FirebaseConstants.USER_PRIMARY_ID) == null ? "" : "AND" + TaskStatusConstants.USER_PRIMARY_ID+"=?"),
                        new String[]{data1.getAsString(FirebaseConstants.TASK_WEB_ID), data1.getAsString(FirebaseConstants.USER_PRIMARY_ID)}
                );
            }

        }

        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
        commonDB.close();

    }




    public static void insertOrUpdateSingleTaskStatus(Context context, String uid, String taskWebId, ContentValues values) {

        CommonDB commonDB = new CommonDB(context);
        SQLiteDatabase db = commonDB.getWritableDatabase();
        db.beginTransaction();

        String
                taskWebIdColumn = TaskStatusConstants.TASK_WEB_ID,
                uidCol = TaskStatusConstants.USER_PRIMARY_ID;


        if (
                db.insert(
                        TaskStatusConstants.TASK_STATUS_TABLE_NAME,
                        null,
                        values
                ) == -1
        ) {

            if (uid != null && uid.length() > 0) {
                db.update(
                        TaskStatusConstants.TASK_STATUS_TABLE_NAME,
                        values,
                        taskWebIdColumn + "=? AND " + uidCol + "=?",
                        new String[] {taskWebId, uid}
                );
            }
            else {
                db.update(
                        TaskStatusConstants.TASK_STATUS_TABLE_NAME,
                        values,
                        taskWebIdColumn + "=?",
                        new String[] {taskWebId}
                );
            }

        }


        commonDB.close();

    }



}
