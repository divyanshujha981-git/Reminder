package com.reminder.main.SqLite.BlockedContact;

import com.reminder.main.Firebase.FirebaseConstants;

public class BlockedContactConstant {
    public static final String
            ID = "_id",
            USER_PRIMARY_ID = FirebaseConstants.USER_PRIMARY_ID,
            BLOCKED_CONTACT_TABLE_NAME = "blockedContactTN",
            BLOCKED_CONTACT_TABLE_QUERY =
                    "CREATE TABLE " + BLOCKED_CONTACT_TABLE_NAME +
                    " ( " +
                            ID + " INTEGER AUTO_INCREMENT, " +                // 0
                            USER_PRIMARY_ID + " TEXT NOT NULL " +             // 1
                    " ) ";
}
