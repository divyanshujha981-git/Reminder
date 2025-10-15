package com.reminder.main.SqLite.Request;

import com.reminder.main.Firebase.FirebaseConstants;

public class RequestConstants {

    public static String
        ID = "_id",
        USER_PRIMARY_ID = FirebaseConstants.USER_PRIMARY_ID,
        STATUS = FirebaseConstants.STATUS,
        REQUEST_TYPE_SENT_OR_RECEIVED = FirebaseConstants.REQUEST_TYPE,
        REQUEST_TABLE_NAME = "request_table",
        REQUEST_TABLE_QUERY = " CREATE TABLE " + REQUEST_TABLE_NAME +
                " ( " +
                    ID + " INTEGER AUTO_INCREMENT, " +                          // 0
                    USER_PRIMARY_ID + " TEXT UNIQUE NOT NULL, " +               // 1
                    REQUEST_TYPE_SENT_OR_RECEIVED + " BYTE NOT NULL, " +        // 2
                    STATUS + " BYTE " +                                         // 3
                " )";




    public static final byte REQUEST_SENT_BYTE = FirebaseConstants.REQUEST_SENT_BYTE;
    public static final byte REQUEST_RECEIVED_BYTE = FirebaseConstants.REQUEST_RECEIVED_BYTE;
    public static final byte STATUS_PENDING_BYTE = FirebaseConstants.STATUS_PENDING_BYTE;
    public static final byte STATUS_ACCEPTED_BYTE = FirebaseConstants.STATUS_ACCEPTED_BYTE;



}
