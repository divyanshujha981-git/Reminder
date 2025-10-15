package com.reminder.main.SqLite.CommonDB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.reminder.main.SqLite.BlockedContact.BlockedContactConstant;
import com.reminder.main.SqLite.Request.RequestConstants;
import com.reminder.main.SqLite.TaskShared.TaskSharedConstants;
import com.reminder.main.SqLite.TaskStatus.TaskStatusConstants;
import com.reminder.main.SqLite.Tasks.TaskConstants;
import com.reminder.main.SqLite.UserDetails.UserDetailsConstant;

public class CommonDB extends SQLiteOpenHelper {
    public CommonDB(@Nullable Context context) {
        super(context, CommonDbConstants.DB_NAME, null, CommonDbConstants.VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TaskConstants.TASK_TABLE_QUERY);
        db.execSQL(UserDetailsConstant.USER_DETAILS_TABLE_QUERY);
        db.execSQL(BlockedContactConstant.BLOCKED_CONTACT_TABLE_QUERY);
        db.execSQL(TaskStatusConstants.TASK_STATUS_TABLE_QUERY);
        db.execSQL(RequestConstants.REQUEST_TABLE_QUERY);
        db.execSQL(TaskSharedConstants.TASK_RECEIVED_TABLE_QUERY);
        db.execSQL(TaskSharedConstants.TASK_SENT_TABLE_QUERY);
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(" DROP TABLE IF EXISTS " + TaskConstants.TASK_TABLE_NAME);
        db.execSQL(" DROP TABLE IF EXISTS " + UserDetailsConstant.USER_DETAILS_TABLE_NAME);
        db.execSQL(" DROP TABLE IF EXISTS " + BlockedContactConstant.BLOCKED_CONTACT_TABLE_NAME);
        db.execSQL(" DROP TABLE IF EXISTS " + TaskStatusConstants.TASK_STATUS_TABLE_NAME);
        db.execSQL(" DROP TABLE IF EXISTS " + RequestConstants.REQUEST_TABLE_NAME);
        db.execSQL(" DROP TABLE IF EXISTS " + TaskSharedConstants.TASK_RECEIVED_TABLE_NAME);
        db.execSQL(" DROP TABLE IF EXISTS " + TaskSharedConstants.TASK_SENT_TABLE_NAME);
    }


}
