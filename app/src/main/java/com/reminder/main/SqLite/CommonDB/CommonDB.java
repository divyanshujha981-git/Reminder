package com.reminder.main.SqLite.CommonDB;

import static com.reminder.main.SqLite.Tasks.TaskConstants.DESCRIPTION;
import static com.reminder.main.SqLite.Tasks.TaskConstants.TASK_ID;
import static com.reminder.main.SqLite.Tasks.TaskConstants.TASK_FTS_TABLE_NAME;
import static com.reminder.main.SqLite.Tasks.TaskConstants.TASK_TABLE_NAME;
import static com.reminder.main.SqLite.Tasks.TaskConstants.TOPIC;

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

//        db.execSQL(
//                " CREATE VIRTUAL TABLE " + TASK_FTS_TABLE_NAME +
//                        " USING fts4(topic, description, content="+TASK_TABLE_NAME+", content_rowid="+TASK_ID+")"
//        );
//
//        db.execSQL(
//                " CREATE TRIGGER tasks_ai AFTER INSERT ON " + TASK_TABLE_NAME + " BEGIN " +
//                " INSERT INTO " + TASK_FTS_TABLE_NAME + "("+ TASK_ID + ", " + TOPIC + ") VALUES (new."+ TASK_ID + ", new." + TOPIC + "); END;"
//        );
//
//        db.execSQL(
//                " CREATE TRIGGER tasks_ad AFTER DELETE ON " + TASK_TABLE_NAME + " BEGIN " +
//                " DELETE FROM " + TASK_FTS_TABLE_NAME + " WHERE rowid = old."+ TASK_ID +"; END;"
//        );
//        db.execSQL(
//                " CREATE TRIGGER tasks_au AFTER UPDATE ON " + TASK_TABLE_NAME + " BEGIN " +
//                " UPDATE " + TASK_FTS_TABLE_NAME + " SET topic = new." + TOPIC + " WHERE rowid = old."+ TASK_ID +"; END;"
//        );


    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        db.execSQL(
//                " CREATE VIRTUAL TABLE " + TASK_FTS_TABLE_NAME +
//                        " USING fts4(topic, description, content= "+TASK_TABLE_NAME +"+, content_rowid="+TASK_ID+")"
//        );
//
//        db.execSQL(
//                "CREATE TRIGGER tasks_ai AFTER INSERT ON " + TASK_TABLE_NAME + " BEGIN " +
//                        " INSERT INTO " + TASK_FTS_TABLE_NAME + "("+ TASK_ID + ", " + TOPIC + ") VALUES (new."+ TASK_ID + ", new." + TOPIC + "); END;"
//        );
//
//        db.execSQL(
//                "CREATE TRIGGER tasks_ad AFTER DELETE ON " + TASK_TABLE_NAME + " BEGIN " +
//                        " DELETE FROM " + TASK_FTS_TABLE_NAME + " WHERE rowid = old."+ TASK_ID +"; END;"
//        );
//        db.execSQL(
//                "CREATE TRIGGER tasks_au AFTER UPDATE ON " + TASK_TABLE_NAME + " BEGIN " +
//                        " UPDATE " + TASK_FTS_TABLE_NAME + " SET topic = new." + TOPIC + " WHERE rowid = old."+ TASK_ID +"; END;"
//        );
//
//        db.execSQL(
//                " INSERT INTO tasks_fts(" + TOPIC + ", "  + DESCRIPTION + ") " +
//                " SELECT "  + TOPIC + ", "  + DESCRIPTION +  " FROM "  + TASK_TABLE_NAME + ";"
//        );

    }


}
