package com.reminder.main.SqLite.TaskShared;


import com.reminder.main.Firebase.FirebaseConstants;

public class TaskSharedConstants {
    public static final String
            ID = "_id",
            USER_PRIMARY_ID = FirebaseConstants.USER_PRIMARY_ID,
            TASK_WEB_ID = FirebaseConstants.TASK_WEB_ID,
            TASK_SENT_TABLE_NAME = "taskSentTN",
            TASK_RECEIVED_TABLE_NAME = "taskReceivedTN",
             TASK_RECEIVED_TABLE_QUERY = " CREATE TABLE " + TASK_RECEIVED_TABLE_NAME +
                    " ( " +
                        ID + " INTEGER AUTO_INCREMENT, " +                                      // 0
                        USER_PRIMARY_ID + " TEXT NOT NULL, " +                                  // 1
                        TASK_WEB_ID + " TEXT NOT NULL, " +                                      // 2
                     " PRIMARY KEY ( " + USER_PRIMARY_ID + ", " + TASK_WEB_ID + " ) " +
                    " )",

            TASK_SENT_TABLE_QUERY = " CREATE TABLE " + TASK_SENT_TABLE_NAME +
                    " ( " +
                        ID + " INTEGER AUTO_INCREMENT, " +                                      // 0
                        USER_PRIMARY_ID + " TEXT NOT NULL, " +                                  // 1
                        TASK_WEB_ID + " TEXT NOT NULL, " +                                      // 2
                     " PRIMARY KEY ( " + USER_PRIMARY_ID + ", " + TASK_WEB_ID + " ) " +
                    " )";




}
