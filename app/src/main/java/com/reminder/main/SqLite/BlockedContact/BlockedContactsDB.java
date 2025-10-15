package com.reminder.main.SqLite.BlockedContact;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.reminder.main.SqLite.CommonDB.CommonDB;

import java.util.ArrayList;

public class BlockedContactsDB {

    public static void insertBlockedContact(Context context, String uid)  {
        ContentValues values = new ContentValues();

        values.put(BlockedContactConstant.USER_PRIMARY_ID, uid);

        CommonDB commonDB = new CommonDB(context);
        commonDB.getWritableDatabase().insert(BlockedContactConstant.BLOCKED_CONTACT_TABLE_NAME, null, values);
        commonDB.close();
    }




    public static void updateBlockedContact(Context context, ContentValues values, String userPrimaryId){
        CommonDB commonDB = new CommonDB(context);
        int n = commonDB.getWritableDatabase().update(BlockedContactConstant.BLOCKED_CONTACT_TABLE_NAME, values, BlockedContactConstant.USER_PRIMARY_ID+"=?" , new String[]{userPrimaryId});
        Log.d("TAG", "updateTask: " + userPrimaryId);
        Log.d("TAG", "updateTask: " + n);
        commonDB.close();
    }




    public static void deleteBlockedContact(Context context, String userPrimaryId){
        CommonDB commonDB = new CommonDB(context);
        commonDB.getWritableDatabase().delete(BlockedContactConstant.BLOCKED_CONTACT_TABLE_NAME, BlockedContactConstant.USER_PRIMARY_ID+"=?" , new String[]{userPrimaryId});
        commonDB.close();
    }




    public static void insertMultipleBlockedContact(Context context, ArrayList<BlockedContactData> data, String requestTableName) {
        CommonDB commonDB = new CommonDB(context);

        SQLiteDatabase db = commonDB.getWritableDatabase();
        db.beginTransaction();

        for (BlockedContactData data1: data) {
            ContentValues values = new ContentValues();
            values.put(BlockedContactConstant.USER_PRIMARY_ID, data1.getUserPrimaryId());
            db.insert(requestTableName, null,values);
        }

        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
        commonDB.close();

    }


    public static void insertMultipleBlockedContact(Context context, ArrayList<String> data) {
        CommonDB commonDB = new CommonDB(context);

        SQLiteDatabase db = commonDB.getWritableDatabase();
        db.beginTransaction();

        for (String data1: data) {
            ContentValues values = new ContentValues();
            values.put(BlockedContactConstant.USER_PRIMARY_ID, data1);
            db.insert(BlockedContactConstant.BLOCKED_CONTACT_TABLE_NAME, null,values);
        }

        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
        commonDB.close();

    }



}
