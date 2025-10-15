package com.reminder.main.SqLite.UserDetails;

import com.reminder.main.Firebase.FirebaseConstants;

public class UserDetailsConstant {
    public static String
    ID = "_id",
    PRIVATE = "private",
    USER_NAME = FirebaseConstants.USER_NAME,
    USER_EMAIL = FirebaseConstants.USER_EMAIL,
    USER_PROFILE_PIC = FirebaseConstants.USER_PROFILE_PIC,
    USER_PRIMARY_ID = FirebaseConstants.USER_PRIMARY_ID,
    USER_PROFESSION = FirebaseConstants.USER_PROFESSION,
    USER_PHONE_NUMBER = FirebaseConstants.USER_PHONE_NUMBER,
    USER_ABOUT = FirebaseConstants.USER_ABOUT,
    USER_DETAILS_TABLE_NAME = "userDetailsTN",

    USER_DETAILS_TABLE_QUERY =
            " CREATE TABLE " + USER_DETAILS_TABLE_NAME +
                    " ( " +
                        ID + " INTEGER AUTO_INCREMENT, " +             // 0
                        USER_PRIMARY_ID + " TEXT UNIQUE NOT NULL, " +  // 1
                        USER_NAME + " TEXT NOT NULL, " +               // 2
                        USER_EMAIL + " TEXT, " +                       // 3
                        USER_PROFILE_PIC + " TEXT, " +                 // 4
                        USER_PROFESSION + " TEXT, " +                  // 5
                        USER_ABOUT + " TEXT, " +                       // 6
                        USER_PHONE_NUMBER + " TEXT " +                 // 7
                    " ) ";




}
