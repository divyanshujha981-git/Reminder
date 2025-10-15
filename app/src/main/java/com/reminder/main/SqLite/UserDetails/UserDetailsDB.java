package com.reminder.main.SqLite.UserDetails;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import android.widget.Toast;

import com.reminder.main.SqLite.CommonDB.CommonDB;

import java.util.ArrayList;



public class UserDetailsDB {


    public static void insertUser(Context context, UserDetailsData data) {
        try {
            ContentValues values = new ContentValues();
            if (data.getName() != null) values.put(UserDetailsConstant.USER_NAME, data.getName());
            else {
                Toast.makeText(context, "Please provide the NAME", Toast.LENGTH_SHORT).show();
                return;
            }

            if (data.getUserPrimaryId() != null) values.put(UserDetailsConstant.USER_PRIMARY_ID, data.getUserPrimaryId());
            else {
                Toast.makeText(context, "Please provide the PRIMARY_ID", Toast.LENGTH_SHORT).show();
                return;
            }

            if (data.getProfession() != null) values.put(UserDetailsConstant.USER_PROFESSION, data.getProfession());
            else {
                Toast.makeText(context, "Please provide the PROFESSION", Toast.LENGTH_SHORT).show();
                return;
            }

            if (data.getEmail() != null) values.put(UserDetailsConstant.USER_EMAIL, data.getEmail());

            if (data.getPhoneNumber() != null) values.put(UserDetailsConstant.USER_PHONE_NUMBER, data.getPhoneNumber());

            if (data.getAbout() != null) values.put(UserDetailsConstant.USER_ABOUT, data.getAbout());

            if (data.getProfilePic() != null) values.put(UserDetailsConstant.USER_PROFILE_PIC, data.getProfilePic());


            insertOrUpdateSingleUser(context, values);
        }
        catch (Exception e) {
            Log.d("TAG", "insertUser: " + e);;
        }

    }



    public static boolean insertUser(Context context, ContentValues values) {

        CommonDB commonDB = new CommonDB(context);
        long con = commonDB.getWritableDatabase()
                .insert(UserDetailsConstant.USER_DETAILS_TABLE_NAME, null, values);
        commonDB.close();
        return con != -1;

    }



    public static void updateUser(Context context, ContentValues values, String userID) {
        Log.d("TAG", "updateUser: " + userID);
        CommonDB commonDB = new CommonDB(context);
        SQLiteDatabase database = commonDB.getWritableDatabase();
        try {
            database
                    .update(UserDetailsConstant.USER_DETAILS_TABLE_NAME, values, UserDetailsConstant.USER_PRIMARY_ID+"=?", new String[]{userID});
            Log.e("TAG", "updateUser: **UPDATED**");
        }
        catch (Exception e) {
            Log.e("TAG", "updateUser: ", e);
            database
                    .insert(UserDetailsConstant.USER_DETAILS_TABLE_NAME, null, values);
            Log.e("TAG", "updateUser: **SOMEHOW UPDATED**");
        }

        commonDB.close();

    }








    public static void insertOrUpdateSingleUser(Context context, ContentValues values) {

        String uid = values.getAsString(UserDetailsConstant.USER_PRIMARY_ID);
        if (uid != null) {
            CommonDB commonDB = new CommonDB(context);
            SQLiteDatabase database = commonDB.getWritableDatabase();
            try {
                if (database.insert(UserDetailsConstant.USER_DETAILS_TABLE_NAME, null, values) == -1) {
                    database.update(UserDetailsConstant.USER_DETAILS_TABLE_NAME, values, UserDetailsConstant.USER_PRIMARY_ID+"=?", new String[]{uid});

                }
            }
            catch (SQLiteException e) {
                Log.e("TAG", "insertOrUpdateSingleUser: ", e);
            }

            database.close();
            commonDB.close();

        }

    }




    public static void deleteUser(Context context, String uid) {

        CommonDB commonDB = new CommonDB(context);
        SQLiteDatabase database = commonDB.getWritableDatabase();
        try {
            database.delete(UserDetailsConstant.USER_DETAILS_TABLE_NAME,UserDetailsConstant.USER_PRIMARY_ID+"=?", new String[]{uid});
        }
        catch (SQLiteException e) {
            e.printStackTrace();
        }

        database.close();
        commonDB.close();

    }



    public static void insertOrUpdateMultipleUsers(Context context, ArrayList<UserDetailsData> userDetailsData) {

        CommonDB commonDB = new CommonDB(context);
        String tableName = UserDetailsConstant.USER_DETAILS_TABLE_NAME;

        SQLiteDatabase db = commonDB.getWritableDatabase();
        db.beginTransaction();


        for (UserDetailsData data1: userDetailsData) {
            ContentValues values = new ContentValues();

            values.put(UserDetailsConstant.USER_NAME, data1.getName());
            values.put(UserDetailsConstant.USER_EMAIL, data1.getEmail());
            values.put(UserDetailsConstant.USER_PROFILE_PIC, data1.getProfilePic());
            values.put(UserDetailsConstant.USER_PRIMARY_ID, data1.getUserPrimaryId());
            values.put(UserDetailsConstant.USER_PROFESSION, data1.getProfession());
            values.put(UserDetailsConstant.USER_ABOUT, data1.getAbout());

            try {
                if (db.insert(tableName, null,values) == -1) {
                    db.update(tableName, values, UserDetailsConstant.USER_PRIMARY_ID+"=?", new String[]{data1.getUserPrimaryId()});
                }
            }
            catch (SQLiteException e) {
                db.update(tableName, values, UserDetailsConstant.USER_PRIMARY_ID+"=?", new String[]{data1.getUserPrimaryId()});
            }

        }

        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
        commonDB.close();

    }







}
