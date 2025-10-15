package com.reminder.main.SqLite.TaskStatus;

import com.reminder.main.Firebase.FirebaseConstants;

public class TaskStatusConstants {
    public static final String
            ID = "_id",
            COMMENT = FirebaseConstants.COMMENT,
            TASK_WEB_ID = FirebaseConstants.TASK_WEB_ID,
            PERCENTAGE_COMPLETE = FirebaseConstants.PERCENTAGE_COMPLETE,
            DOWNLOADED = FirebaseConstants.DOWNLOADED,
            USER_PRIMARY_ID = FirebaseConstants.USER_PRIMARY_ID,
            TASK_STATUS_TABLE_NAME = "taskStatusTN",


            TASK_STATUS_TABLE_QUERY = " CREATE TABLE " + TASK_STATUS_TABLE_NAME +
                    " ( " +
                        ID + " INTEGER AUTO_INCREMENT, " +            // 0
                        COMMENT + " TEXT, " +                         // 1
                        PERCENTAGE_COMPLETE + " BYTE NOT NULL, " +    // 2
                        TASK_WEB_ID + " TEXT NOT NULL, " +            // 3
                        USER_PRIMARY_ID + " TEXT NOT NULL, " +        // 4
                        DOWNLOADED + " BYTE NOT NULL, " +             // 5
                        " PRIMARY KEY ( " + TASK_WEB_ID + ", " + USER_PRIMARY_ID + " )" +
                    " )";

    public static byte DOWNLOADED_YES_BYTE = 2;
    public static byte DOWNLOADED_NO_BYTE= 1;
}
