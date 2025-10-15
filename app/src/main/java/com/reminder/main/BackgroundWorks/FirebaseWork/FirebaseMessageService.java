package com.reminder.main.BackgroundWorks.FirebaseWork;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.reminder.main.Firebase.FirebaseConstants;

import java.io.FileNotFoundException;
import java.util.Map;
import java.util.Objects;


public class FirebaseMessageService extends FirebaseMessagingService {






    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d("TAG", "onNewToken: -->> " + token);
    }







    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);

        if (message.getNotification() != null) {

            String topic = message.getNotification().getTitle();

            Log.d("TAG", "onMessageReceived: " + message.getData());

            if (Objects.equals(topic, FirebaseConstants.TOPIC_CHANGE_IN_TASK_STATUS)) {
                handleTaskStatus(message.getData());
            }

            else if (Objects.equals(topic, FirebaseConstants.TOPIC_CHANGE_IN_REQUEST)) {
                handleRequest(message.getData());
            }

            else if (Objects.equals(topic, FirebaseConstants.TOPIC_CHANGE_IN_TASK)) {
                handleTask(message.getData());
            }
        }

    }













    private void handleTaskStatus(Map<?, ?> map) {

//        String uid = String.valueOf(map.get(FirebaseConstants.USER_PRIMARY_ID));
//        String twi = String.valueOf(map.get(FirebaseConstants.TASK_WEB_ID));
//        byte pc = Byte.parseByte(String.valueOf(map.get(FirebaseConstants.PERCENTAGE_COMPLETE)));
//        byte st = Byte.parseByte(String.valueOf(map.get(FirebaseConstants.SHARED_TYPE)));
//
//        ContentValues values = new ContentValues();
//        values.put(FirebaseConstants.USER_PRIMARY_ID, uid);
//        values.put(FirebaseConstants.TASK_WEB_ID, twi);
//        values.put(FirebaseConstants.PERCENTAGE_COMPLETE, pc);
//        values.put(FirebaseConstants.SHARED_TYPE, st);
//
//        TaskSharedDB.insertOrUpdateSingleTaskSharedStatus(getApplicationContext(), values, twi, uid);
//
//        if (pc == 100) {
//
//            // post notification
//
//            String taskSharedTable = st == TaskSharedConstants.TASK_SHARED_TYPE_RECEIVED ? TaskSharedConstants.TASK_RECEIVED_TABLE_NAME : TaskSharedConstants.TASK_SENT_TABLE_NAME;
//            String userDetailsTable = UserDetailsConstant.USER_DETAILS_TABLE_NAME;
//            String taskTable = TaskConstants.TASK_TABLE_NAME;
//
//            CommonDB commonDB = new CommonDB(this);
//            Cursor cursor = commonDB.getReadableDatabase().rawQuery(
//                    "SELECT " +
//                            userDetailsTable+"."+UserDetailsConstant.USER_NAME + ", " +
//                            userDetailsTable+"."+UserDetailsConstant.USER_PROFILE_PIC + ", " +
//                            taskTable+"."+TaskConstants.TOPIC +
//                        " FROM " + taskSharedTable +
//                            " LEFT JOIN " + taskTable + " ON " + taskSharedTable+"."+TaskSharedConstants.TASK_WEB_ID+"="+taskTable+"."+TaskConstants.TASK_WEB_ID +
//                            " LEFT JOIN " + userDetailsTable + " ON " + taskSharedTable+"."+TaskSharedConstants.USER_PRIMARY_ID+"="+userDetailsTable+"."+UserDetailsConstant.USER_PRIMARY_ID +
//                        " WHERE " + taskSharedTable+"."+TaskSharedConstants.TASK_WEB_ID+"=\""+twi+"\" AND " + taskSharedTable+"."+TaskSharedConstants.USER_PRIMARY_ID+"=\""+uid+"\""
//                    , null);
//
//            if (cursor != null && cursor.getCount() > 0) {
//
//                cursor.moveToFirst();
//
//                new TaskStatusNotification(
//                        this,
//                        uid,
//                        cursor.getString(0),
//                        cursor.getString(1),
//                        cursor.getString(2)
//                ).createNotification();
//
//                cursor.close();
//
//            }
//            commonDB.close();
//        }


    }












    private void handleRequest(Map<?, ?> map) {

//        String statusString = String.valueOf(map.get(FirebaseConstants.STATUS));
//
//
//        String uid = String.valueOf(map.get(FirebaseConstants.USER_PRIMARY_ID));
//        String taskType = String.valueOf(map.get(FirebaseConstants.TASK_TYPE));
//
//
//
//        String requestTableName = taskType.equals(RequestConstants.SEND_TASK) ? RequestConstants.SEND_TASK_TABLE_NAME : RequestConstants.RECEIVE_TASK_TABLE_NAME;
//
//        if (statusString.equals("null")) {
//            RequestsDB.deleteRequest(this, requestTableName, uid);
//        }
//        else {
//
//            byte requestType = Boolean.parseBoolean(String.valueOf(map.get(FirebaseConstants.REQUEST_TYPE))) == RequestConstants.REQUEST_SENT_BOOL ? RequestConstants.REQUEST_SENT_BYTE : RequestConstants.REQUEST_RECEIVED_BYTE;
//            boolean stat = Boolean.parseBoolean(statusString);
//            byte requestStatus = stat == RequestConstants.REQUEST_STATUS_PENDING_BOOL ? RequestConstants.REQUEST_STATUS_PENDING_BYTE : RequestConstants.REQUEST_STATUS_ACCEPTED_BYTE;
//
//            Log.d("TAG", "handleRequest: ////" + requestType);
//
//            ContentValues values = new ContentValues();
//            values.put(FirebaseConstants.USER_PRIMARY_ID, uid);
//            values.put(FirebaseConstants.STATUS, requestStatus);
//            values.put(FirebaseConstants.REQUEST_TYPE, requestType);
//            RequestsDB.insertORUpdateSingleRequest(this, requestTableName, values);
//
//
//            // post notification
//
//            String userDetailsTable = UserDetailsConstant.USER_DETAILS_TABLE_NAME;
//
//            CommonDB commonDB = new CommonDB(this);
//            Cursor cursor = commonDB.getReadableDatabase().rawQuery(
//                    "SELECT " +
//                                userDetailsTable+"."+UserDetailsConstant.USER_NAME + ", " +
//                                userDetailsTable+"."+UserDetailsConstant.USER_PROFILE_PIC +
//                            " FROM " + userDetailsTable +
//                            " WHERE " + userDetailsTable+"."+UserDetailsConstant.USER_PRIMARY_ID+"=\""+uid+"\""
//                    , null);
//
//            if (cursor != null && cursor.getCount() > 0) {
//
//                cursor.moveToFirst();
//
//                new RequestStatusChangeNotificationGeneralClass(
//                        this,
//                        uid,
//                        cursor.getString(0),
//                        cursor.getString(1),
//                        taskType,
//                        requestType,
//                        requestStatus
//                ).createNotification();
//
//                cursor.close();
//
//            }
//            commonDB.close();
//
//
//
//
//
//        }

    }










    private void handleTask(Map<?, ?> map) {

//        Log.d("TAG", "handleTask: " + map.get(FirebaseConstants.TASK_DATA));
//
//        try {
//
//            JSONObject jsonObject = new JSONObject(String.valueOf(map.get(FirebaseConstants.TASK_DATA)));
//            String taskWebId = String.valueOf(map.get(FirebaseConstants.TASK_WEB_ID));
//
//
//            String topic = jsonObject.getString(FirebaseConstants.TOPIC);
//            String description = null;
//            try {
//                description = jsonObject.getString(FirebaseConstants.DESCRIPTION);
//            }
//            catch (Exception ignored) {
//
//            }
//
//            long alarmDate = jsonObject.getLong(FirebaseConstants.ALARM_DATE);
//
//            byte repeatStatus = TaskConstants.REPEAT_STATUS_NO_REPEAT;
//            try {
//                repeatStatus = (byte) jsonObject.getInt(FirebaseConstants.REPEAT_STATUS);
//            }
//            catch (Exception ignored) {
//
//            }
//
//            JSONArray jsonArray = new JSONArray(TaskConstants.DAYS_OF_WEEK);
//            ArrayList<Integer> dateArray = new ArrayList<>();
//            try {
//                jsonArray = jsonObject.getJSONArray(FirebaseConstants.DATE_ARRAY);
//            }
//            catch (Exception ignored) {
//
//            }
//
//            for (int i = 0; i < jsonArray.length(); i++) {
//                dateArray.add(jsonArray.getInt(i));
//            }
//
//            TaskData taskData = new TaskData(
//                    TaskConstants.PRIVATE_NO,
//                    topic,
//                    description,
//                    alarmDate,
//                    repeatStatus,
//                    jsonArray.toString(),
//                    repeatStatus == TaskConstants.REPEAT_STATUS_NO_REPEAT ?
//                            alarmDate :
//                            rescheduleDateAndTIme(
//                                    dateArray,
//                                    alarmDate,
//                                    repeatStatus
//                            ),
//                    0,
//                    TaskConstants.ALREADY_DONE_NO,
//                    TaskConstants.PINNED_NO,
//                    null,
//                    Long.parseLong(taskWebId.split("_")[1]),
//                    TaskConstants.PRIORITY_NORMAL,
//                    taskWebId
//            );
//
//
//            TasksDB.insertTask(this, taskData);
//
//
//
//            boolean taskAutoDownload = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(getString(R.string.taskAutoDownload), true);
//            String userPrimaryId = String.valueOf(map.get(FirebaseConstants.USER_PRIMARY_ID));
//            ContentValues values = new ContentValues();
//
//            values.put(TaskSharedConstants.USER_PRIMARY_ID, userPrimaryId);
//            values.put(TaskSharedConstants.TASK_WEB_ID, taskWebId);
//            values.put(TaskSharedConstants.DOWNLOADED, taskAutoDownload);
//            values.put(TaskSharedConstants.TASK_WEB_ID, taskWebId);
//            values.put(TaskSharedConstants.SHARED_TYPE, TaskSharedConstants.TASK_SHARED_TYPE_RECEIVED);
//
//            TaskSharedDB.insertOrUpdateSingleTaskShared(this, values, TaskSharedConstants.TASK_RECEIVED_TABLE_NAME, taskWebId);
//
//
//
//            CommonDB commonDB = new CommonDB(this);
//            String userDetailsTable = UserDetailsConstant.USER_DETAILS_TABLE_NAME;
//            Cursor cursor = commonDB.getReadableDatabase().rawQuery(
//                    "SELECT " +
//                            UserDetailsConstant.USER_NAME + ", " +
//                            UserDetailsConstant.USER_PROFILE_PIC +
//                            " FROM " + userDetailsTable +
//                            " WHERE " + UserDetailsConstant.USER_PRIMARY_ID+"=\""+userPrimaryId+"\""
//                    , null);
//
//            Log.d("TAG", "handleTask: " + cursor.getCount());
//
//            if (cursor != null && cursor.getCount() > 0) {
//
//                cursor.moveToFirst();
//
//                new TaskReceivedGeneralNotification(
//                        topic,
//                        this,
//                        cursor.getString(0),
//                        cursor.getString(1),
//                        userPrimaryId
//                        ).createNotification();
//
//                cursor.close();
//
//            }
//
//            commonDB.close();
//
//
//
//
//
//        }
//        catch (JSONException ignored) {
//
//        }




    }







    public static Bitmap getBitmapProfilePic(Context context, String userProfilePic) {
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(Uri.parse(userProfilePic)));
            Log.d("TAG", "createNotification: ");
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bitmap;
    }



}
