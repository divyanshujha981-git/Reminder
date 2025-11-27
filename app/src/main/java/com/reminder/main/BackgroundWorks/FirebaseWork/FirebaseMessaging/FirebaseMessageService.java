package com.reminder.main.BackgroundWorks.FirebaseWork.FirebaseMessaging;

import static com.reminder.main.Custom.CustomFunctions.generateNewTaskID;
import static com.reminder.main.Firebase.FirebaseConstants.DESCRIPTION;
import static com.reminder.main.Firebase.FirebaseConstants.DOWNLOADED;
import static com.reminder.main.Firebase.FirebaseConstants.DOWNLOADED_NO_BYTE;
import static com.reminder.main.Firebase.FirebaseConstants.DOWNLOADED_YES;
import static com.reminder.main.Firebase.FirebaseConstants.DOWNLOADED_YES_BYTE;
import static com.reminder.main.Firebase.FirebaseConstants.PERCENTAGE_COMPLETE;
import static com.reminder.main.Firebase.FirebaseConstants.REPEAT_STATUS;
import static com.reminder.main.Firebase.FirebaseConstants.SET_DEVICE_MESSAGE_TOKEN;
import static com.reminder.main.Firebase.FirebaseConstants.STATUS;
import static com.reminder.main.Firebase.FirebaseConstants.TASKS;
import static com.reminder.main.Firebase.FirebaseConstants.TASK_WEB_ID;
import static com.reminder.main.Firebase.FirebaseConstants.TOPIC;
import static com.reminder.main.Firebase.FirebaseConstants.USER_NAME;
import static com.reminder.main.Firebase.FirebaseConstants.USER_PRIMARY_ID;
import static com.reminder.main.SqLite.TaskShared.TaskSharedConstants.TASK_RECEIVED_TABLE_NAME;
import static com.reminder.main.SqLite.TaskShared.TaskSharedDB.insertOrUpdateMultipleTaskShared;
import static com.reminder.main.SqLite.TaskStatus.TaskStatusDB.insertOrUpdateMultipleTaskStatus;
import static com.reminder.main.SqLite.Tasks.TaskConstants.ALARM_DATE;
import static com.reminder.main.SqLite.Tasks.TaskConstants.ALREADY_DONE_NO_BYTE;
import static com.reminder.main.SqLite.Tasks.TaskConstants.DATE_ARRAY;
import static com.reminder.main.SqLite.Tasks.TaskConstants.DAYS_OF_WEEK;
import static com.reminder.main.SqLite.Tasks.TaskConstants.PINNED_NO;
import static com.reminder.main.SqLite.Tasks.TaskConstants.PRIORITY_NORMAL;
import static com.reminder.main.SqLite.Tasks.TaskConstants.PRIVATE_NO;
import static com.reminder.main.SqLite.Tasks.TaskConstants.REPEAT_STATUS_NO_REPEAT;
import static com.reminder.main.SqLite.Tasks.TaskConstants.TASK_ID;
import static com.reminder.main.SqLite.UserDetails.UserDetailsConstant.USER_DETAILS_TABLE_NAME;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.reminder.main.Firebase.FirebaseConstants;
import com.reminder.main.SqLite.CommonDB.CommonDB;
import com.reminder.main.SqLite.Tasks.TaskData;
import com.reminder.main.SqLite.Tasks.TasksDB;
import com.reminder.main.UserInterfaces.AddTaskPage.AddTask;
import com.reminder.main.UserInterfaces.NotificationPage.TaskReceived.BroadCasts.TaskReceivedAlertBroadcast;

import org.json.JSONArray;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class FirebaseMessageService extends FirebaseMessagingService {






    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);

        Map<String, String> map = new HashMap<>();

        Log.d("TAG", "onNewToken: **MESSAGE TOKEN CHANGED**");

        FirebaseFunctions
                .getInstance()
                .getHttpsCallable(SET_DEVICE_MESSAGE_TOKEN)
                .call(new Gson().toJson(map))
                .addOnSuccessListener(httpsCallableResult -> {
                    Log.d("TAG", "onNewToken: **TOKEN UPDATED**");
                })
                .addOnFailureListener(e -> {
                    Log.d("TAG", "onNewToken: **TOKEN NOT UPDATED**");
                })
                .addOnCompleteListener(task -> {
                    Log.d("TAG", "onNewToken: **REQUESTED FOR TOKEN UPDATE**");
                });
        Log.d("TAG", "onNewToken: -->> " + token);

    }







    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);

        Log.d("TAG", "onMessageReceived: ");

        if (message.getNotification() != null) {

            String topic = message.getNotification().getTitle();

            Log.d("TAG", "onMessageReceived: " + topic);
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
        Log.d("TAG", "handleTaskStatus: " + map);
    }












    private void handleRequest(Map<?, ?> map) {
        Log.d("TAG", "handleRequest: " + map);
    }










    private void handleTask(Map<String, String> taskDetails) {

        String userPrimaryId = taskDetails.get(USER_PRIMARY_ID);
        if (userPrimaryId.equals(FirebaseAuth.getInstance().getUid())) return;

        String taskString = taskDetails.get(TASKS);
        String statusString = taskDetails.get(STATUS);

        ObjectMapper objectMapper = new ObjectMapper();
        Map<?, ?> taskMap = null;
        try {
            taskMap = objectMapper.readValue(taskString, Map.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        Map<?, ?> statusMap = null;
        try {
            statusMap = objectMapper.readValue(statusString, Map.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }


        setTaskDataToSQLite(taskMap, userPrimaryId);
        setTaskReceivedDataToSQLite(statusMap, userPrimaryId);

    }









    private void setTaskDataToSQLite(Map<?,?> taskDetails, String uid) {

        Log.d("TAG", "setTaskDataToSQLite: " + taskDetails);

        ArrayList<TaskData> taskDataData = new ArrayList<>();

        taskDetails.forEach((key, object) -> {



            String taskWebId = (String) key;
            Map<?,?> map = (Map<?, ?>) object;
            String topic = (String) map.get(TOPIC);
            String taskIO = map.containsKey(TASK_ID) ? (String) map.get(TASK_ID) : generateNewTaskID(this);
            String description = map.containsKey(DESCRIPTION) ? (String) map.get(DESCRIPTION) : "" ;
            long alarmDate = (long) map.get(ALARM_DATE);
            byte repeatStatus = map.containsKey(REPEAT_STATUS) ? Byte.parseByte(map.get(REPEAT_STATUS)+"") : REPEAT_STATUS_NO_REPEAT;

            ArrayList<Integer> dateArrayList = null;
            String dateArray;
            try {
                dateArrayList = (ArrayList<Integer>) taskDetails.get(DATE_ARRAY);
                dateArray = new JSONArray(dateArrayList).toString();
            }  catch (Exception e) {
                dateArray = DAYS_OF_WEEK;
            }

            if (dateArrayList == null) {
                dateArrayList = new ArrayList<>();
                try {
                    JSONArray jsonArray = new JSONArray(dateArray);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        dateArrayList.add(jsonArray.getInt(i));
                    }
                }
                catch (Exception ignored) {

                }
            }

            taskDataData.add(
                    new TaskData(
                            PRIVATE_NO,
                            topic,
                            description,
                            alarmDate,
                            repeatStatus,
                            dateArray,
                            repeatStatus == REPEAT_STATUS_NO_REPEAT ?
                                    alarmDate :
                                    AddTask.rescheduleDateAndTIme(
                                            dateArrayList,
                                            alarmDate,
                                            repeatStatus
                                    ),
                            0,
                            ALREADY_DONE_NO_BYTE,
                            PINNED_NO,
                            null,
                            taskIO,
                            PRIORITY_NORMAL,
                            taskWebId
                    )
            );

            postTaskReceivedNotification(taskWebId, topic, uid);

        });

        TasksDB.insertMultipleTask(this, taskDataData);

    }









    private void setTaskReceivedDataToSQLite(Map<?,?> taskReceivedDetails, String uid) {

        Log.d("TAG", "setTaskReceivedDataToSQLite: " + taskReceivedDetails);

        ArrayList<ContentValues> taskSharedDataArray = new ArrayList<>();
        ArrayList<ContentValues> taskStatusDataArray = new ArrayList<>();

        taskReceivedDetails.forEach((key, obj) -> {

            Map<?,?> fd = (Map<?,?>) obj;

            String taskWebId = (String) key;
            byte downloaded = Boolean.parseBoolean(String.valueOf(fd.get(DOWNLOADED))) == DOWNLOADED_YES ? DOWNLOADED_YES_BYTE : DOWNLOADED_NO_BYTE;

            ContentValues taskSharedValues = new ContentValues();
            taskSharedValues.put(TASK_WEB_ID, taskWebId);
            taskSharedValues.put(USER_PRIMARY_ID, uid);

            ContentValues taskStatusValues = new ContentValues();
            taskStatusValues.put(USER_PRIMARY_ID, uid);
            taskStatusValues.put(TASK_WEB_ID, taskWebId);
            taskStatusValues.put(PERCENTAGE_COMPLETE, 0);
            taskStatusValues.put(DOWNLOADED, downloaded);

            taskSharedDataArray.add(taskSharedValues);
            taskStatusDataArray.add(taskStatusValues);

        });

        insertOrUpdateMultipleTaskShared(this, taskSharedDataArray, TASK_RECEIVED_TABLE_NAME);
        insertOrUpdateMultipleTaskStatus(this, taskStatusDataArray);

    }










    private void postTaskReceivedNotification(String taskWebID, String topic, String uid) {
        new Thread(() -> {

            CommonDB commonDB = new CommonDB(this);
            SQLiteDatabase database = commonDB.getReadableDatabase();
            Cursor cursor = database.rawQuery(
                    "SELECT " + USER_NAME + " FROM " + USER_DETAILS_TABLE_NAME +
                        " WHERE " + USER_PRIMARY_ID + " = " + uid

                            , null);

            String userName;
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                userName = cursor.getString(0);
            } else {
                userName = "[ Unknown ]";
            }

            Handler handler = new Handler(Looper.getMainLooper());
            Intent intent = new Intent(this, TaskReceivedAlertBroadcast.class);
            intent.putExtra(TOPIC, topic);
            intent.putExtra(USER_NAME, userName);
            intent.putExtra(TASK_WEB_ID, taskWebID);


            handler.post(() -> {
                sendBroadcast(intent);
            });
            cursor.close();
            database.close();
            commonDB.close();


        }).start();
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
