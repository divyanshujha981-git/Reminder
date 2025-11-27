package com.reminder.main.BackgroundWorks.FirebaseWork;

import static com.reminder.main.Custom.CustomFunctions.generateNewTaskID;
import static com.reminder.main.Firebase.FirebaseConstants.BLOCKED_CONTACTS;
import static com.reminder.main.Firebase.FirebaseConstants.DATE_ARRAY;
import static com.reminder.main.Firebase.FirebaseConstants.DESCRIPTION;
import static com.reminder.main.Firebase.FirebaseConstants.DOWNLOADED;
import static com.reminder.main.Firebase.FirebaseConstants.DOWNLOADED_NO_BYTE;
import static com.reminder.main.Firebase.FirebaseConstants.DOWNLOADED_YES;
import static com.reminder.main.Firebase.FirebaseConstants.DOWNLOADED_YES_BYTE;
import static com.reminder.main.Firebase.FirebaseConstants.GET_ACCOUNT_DATA;
import static com.reminder.main.Firebase.FirebaseConstants.GET_USER_PROFILE_DATA;
import static com.reminder.main.Firebase.FirebaseConstants.PERCENTAGE_COMPLETE;
import static com.reminder.main.Firebase.FirebaseConstants.PROFILE_DETAILS;
import static com.reminder.main.Firebase.FirebaseConstants.RECEIVE_REQUEST;
import static com.reminder.main.Firebase.FirebaseConstants.REPEAT_STATUS;
import static com.reminder.main.Firebase.FirebaseConstants.REQUESTS;
import static com.reminder.main.Firebase.FirebaseConstants.REQUEST_RECEIVED_BYTE;
import static com.reminder.main.Firebase.FirebaseConstants.REQUEST_SENT;
import static com.reminder.main.Firebase.FirebaseConstants.REQUEST_SENT_BYTE;
import static com.reminder.main.Firebase.FirebaseConstants.REQUEST_TYPE;
import static com.reminder.main.Firebase.FirebaseConstants.STATUS;
import static com.reminder.main.Firebase.FirebaseConstants.STATUS_ACCEPTED;
import static com.reminder.main.Firebase.FirebaseConstants.STATUS_ACCEPTED_BYTE;
import static com.reminder.main.Firebase.FirebaseConstants.STATUS_PENDING_BYTE;
import static com.reminder.main.Firebase.FirebaseConstants.TASKS;
import static com.reminder.main.Firebase.FirebaseConstants.TASK_ARRAY;
import static com.reminder.main.Firebase.FirebaseConstants.TASK_RECEIVED;
import static com.reminder.main.Firebase.FirebaseConstants.TASK_SENT;
import static com.reminder.main.Firebase.FirebaseConstants.TASK_STATUS;
import static com.reminder.main.Firebase.FirebaseConstants.TASK_WEB_ID;
import static com.reminder.main.Firebase.FirebaseConstants.TOPIC;
import static com.reminder.main.Firebase.FirebaseConstants.USER_ABOUT;
import static com.reminder.main.Firebase.FirebaseConstants.USER_EMAIL;
import static com.reminder.main.Firebase.FirebaseConstants.USER_NAME;
import static com.reminder.main.Firebase.FirebaseConstants.USER_PHONE_NUMBER;
import static com.reminder.main.Firebase.FirebaseConstants.USER_PRIMARY_ID;
import static com.reminder.main.Firebase.FirebaseConstants.USER_PRIMARY_ID_LIST;
import static com.reminder.main.Firebase.FirebaseConstants.USER_PROFESSION;
import static com.reminder.main.Firebase.FirebaseConstants.USER_PROFILE_PIC;
import static com.reminder.main.SqLite.TaskShared.TaskSharedConstants.TASK_RECEIVED_TABLE_NAME;
import static com.reminder.main.SqLite.TaskShared.TaskSharedConstants.TASK_SENT_TABLE_NAME;
import static com.reminder.main.SqLite.TaskShared.TaskSharedDB.insertOrUpdateMultipleTaskShared;
import static com.reminder.main.SqLite.TaskStatus.TaskStatusDB.insertOrUpdateMultipleTaskStatus;
import static com.reminder.main.SqLite.Tasks.TaskConstants.REPEAT_STATUS_NO_REPEAT;
import static com.reminder.main.SqLite.Tasks.TaskConstants.TASK_ID;
import static com.reminder.main.UserInterfaces.HomePage.MainActivity.MainActivity.FIREBASE_AUTH;
import static com.reminder.main.UserInterfaces.HomePage.MainActivity.MainActivity.FIREBASE_FUNCTIONS;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.reminder.main.Firebase.FirebaseConstants;
import com.reminder.main.R;
import com.reminder.main.SqLite.BlockedContact.BlockedContactsDB;
import com.reminder.main.SqLite.Request.RequestConstants;
import com.reminder.main.SqLite.Request.RequestData;
import com.reminder.main.SqLite.Request.RequestsDB;
import com.reminder.main.SqLite.Tasks.TaskConstants;
import com.reminder.main.SqLite.Tasks.TaskData;
import com.reminder.main.SqLite.Tasks.TasksDB;
import com.reminder.main.SqLite.UserDetails.UserDetailsDB;
import com.reminder.main.SqLite.UserDetails.UserDetailsData;
import com.reminder.main.UserInterfaces.AddTaskPage.AddTask;
import com.reminder.main.UserInterfaces.SettingsPage.AccountSettings.AccountSettings;
import com.reminder.main.UserInterfaces.SettingsPage.AccountSettings.EditAccountInfo;

import org.json.JSONArray;
import org.json.JSONException;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SyncFirebaseData extends Service {



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(this::syncAccountData).start();
        return super.onStartCommand(intent, flags, startId);
    }


    private void syncAccountData() {

        try {
            if (FirebaseAuth.getInstance().getUid() != null) {
                Log.d("TAG", "syncAccountData: **SYNCING STARTED**");

                Map<String, Object> map = new HashMap<>();
                map.put(FirebaseConstants.DEVICE_MESSAGE_TOKEN, FirebaseMessaging.getInstance().getToken().getResult());

                FIREBASE_FUNCTIONS.getHttpsCallable(GET_ACCOUNT_DATA)
                        .call(new Gson().toJson(map))
                        .addOnSuccessListener(httpsCallableResult -> {
                            // set account data
                            assert httpsCallableResult.getData() != null;
                            setAccountDataToSQLite(this, (Map<?, ?>) httpsCallableResult.getData());

                        })
                        .addOnFailureListener(e -> {
                            // do required stuffs that are supposed to be done after the response from server
                        })
                        .addOnCompleteListener(task -> {
                            // do required stuffs that are supposed to be done after the response from server
                        });
            }

        }
        catch (Exception e) {
            Log.w("TAG", "syncAccountData: ", e);
        }


    }



    public static void setAccountDataToSQLite(Context context, Map<?, ?> data) {


        Map<?,?> profileDetails = (Map<?,?>) data.get(PROFILE_DETAILS);
        Map<?,?> requestDetails = (Map<?,?>) data.get(REQUESTS);
        Map<?,?> taskDetails = (Map<?,?>) data.get(TASKS);
        ArrayList<String> blockedContactsDetails = (ArrayList<String>) data.get(BLOCKED_CONTACTS);

        if (profileDetails != null) setProfileDataToSQLite(context, profileDetails);
        else context.startActivity(new Intent(context, EditAccountInfo.class));

        if (requestDetails != null) getOtherUserDetailsFromFirebase(context, setRequestDataToSQLite(context, requestDetails));;
        if (taskDetails != null) taskSubCollections(context, taskDetails);
        if (blockedContactsDetails != null) setBlockedContactDataToSQLite(context, blockedContactsDetails);



    }



    public static void setProfileDataToSQLite(Context context, Map<?,?> profileDetails) {


        boolean isAccountPrivate = false;

        try {
            isAccountPrivate = Boolean.parseBoolean(String.valueOf(profileDetails.get(FirebaseConstants.IS_ACCOUNT_PRIVATE)));

        }
        catch (Exception ignored) {
            Log.w("TAG", "setProfileDataToSQLite: NO ACCOUNT PRIVACY FOUND");
        }

        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean(ContextCompat.getString(context, R.string.isAccountPrivate), isAccountPrivate);
        editor.apply();

        String userName = String.valueOf(profileDetails.get(USER_NAME));
        String userEmail = String.valueOf(profileDetails.get(USER_EMAIL));
        String phoneNumber = String.valueOf(profileDetails.get(USER_PHONE_NUMBER));
        String userPrimaryId = FIREBASE_AUTH.getUid();
        String userAbout = String.valueOf(profileDetails.get(USER_ABOUT));
        String userProfession = String.valueOf(profileDetails.get(USER_PROFESSION));
        String userProfilePic = String.valueOf(profileDetails.get(USER_PROFILE_PIC));

        Log.d("TAG", "setProfileDataToSQLite: " + phoneNumber);

        if (userProfession.isEmpty() || userName.isEmpty()) {
            context.startActivity(new Intent(context, EditAccountInfo.class));
        }
        else {
            ContentValues values = new ContentValues();
            values.put(USER_NAME, userName);
            values.put(USER_EMAIL, userEmail);
            values.put(USER_PHONE_NUMBER, phoneNumber);
            values.put(USER_ABOUT, userAbout);
            values.put(USER_PROFESSION, userProfession);
            values.put(USER_PROFILE_PIC, userProfilePic);
            values.put(USER_PRIMARY_ID, userPrimaryId);

//            UserDetailsData data = new UserDetailsData();
//            data.setName(userName);
//            data.setEmail(userEmail);
//            data.setProfilePic(userProfilePic);
//            data.setUserPrimaryId(userPrimaryId);
//            data.setProfession(userProfession);
//            data.setAbout(userAbout);
//            data.setPhoneNumber(phoneNumber);

            UserDetailsDB.insertOrUpdateSingleUser(context, values);
        }



    }



    public static ArrayList<String> setRequestDataToSQLite(Context context, Map<?,?> requestDetails) {


        ArrayList<String> userPrimaryIdList = new ArrayList<>();


        // --------- Shared preference part ------ //

        boolean receiveRequest = true;

        try {
            receiveRequest = (boolean) requestDetails.get(RECEIVE_REQUEST);
            requestDetails.remove(RECEIVE_REQUEST);

        } catch (Exception ignored) {}

        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean(ContextCompat.getString(context, R.string.receiveRequest), receiveRequest);
        editor.apply();


        // --------- --------------------- ------ //

        ArrayList<RequestData> requestDataArrayList = new ArrayList<>();

        requestDetails.forEach((key, object) -> {

            RequestData requestData = new RequestData();
            Map<?, ?> map = (Map<?, ?>) object;

            String userPrimaryId = (String) key;
            byte status = ((boolean) map.get(STATUS)) == STATUS_ACCEPTED ? STATUS_ACCEPTED_BYTE : STATUS_PENDING_BYTE;
            byte requestType = ((boolean) map.get(REQUEST_TYPE)) == REQUEST_SENT ? REQUEST_SENT_BYTE : REQUEST_RECEIVED_BYTE;

            requestData.setRequestType(requestType);
            requestData.setStatus(status);
            requestData.setUserPrimaryId(userPrimaryId);

            if (!userPrimaryIdList.contains(userPrimaryId)) userPrimaryIdList.add(userPrimaryId);

            requestDataArrayList.add(requestData);

        });

        RequestsDB.insertORUpdateMultipleRequest(context, RequestConstants.REQUEST_TABLE_NAME, requestDataArrayList);


        return userPrimaryIdList;


    }



    public static void taskSubCollections(Context context, Map<?,?> taskDetails) {

        Map<?,?> taskData = (Map<?, ?>) taskDetails.get(TASKS);
        Map<?,?> taskSent = (Map<?, ?>) taskDetails.get(TASK_SENT);
        Map<?,?> taskReceived = (Map<?, ?>) taskDetails.get(TASK_RECEIVED);
        Map<?,?> taskStatus = (Map<?, ?>) taskDetails.get(TASK_STATUS);

        if (taskData != null) setTaskDataToSQLite(context, taskData);
        if (taskSent != null) setTaskSentDataToSQLite(context, taskSent);
        if (taskReceived != null) setTaskReceivedDataToSQLite(context, taskReceived);
        if (taskStatus != null) setTaskStatusDataToSQLite(context, taskStatus);

        // --------- Shared preference part ------ //

        boolean receiveTask = true;
        boolean taskAutoDownload = false;

        try {
            receiveTask = (boolean) taskDetails.get(FirebaseConstants.RECEIVE_TASK);
        } catch (Exception ignored) {}

        try {
            taskAutoDownload = (boolean) taskDetails.get(FirebaseConstants.TASK_AUTO_DOWNLOAD);
        } catch (Exception ignored) {}

        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean(ContextCompat.getString(context, R.string.receiveTask), receiveTask);
        editor.putBoolean(ContextCompat.getString(context, R.string.taskAutoDownload), taskAutoDownload);
        editor.apply();

        // --------- --------------------- ------ //

    }



    public static void setUserDetailsToSQLite(Context context, Map<?,?> userDetails) {
        ArrayList<UserDetailsData> usersData = new ArrayList<>();
        userDetails.forEach((key, object) -> {

            Map<?,?> data = (Map<?,?>) object;
            String userPrimaryId = (String) key;
            String name = (String) data.get(USER_NAME);
            String email = (String) data.get(USER_EMAIL);
            String profession = (String) data.get(USER_PROFESSION);
            String about = (String) data.get(USER_ABOUT);
            String phoneNumber = (String) data.get(USER_PHONE_NUMBER);
            String profilePic = (String) data.get(USER_PROFILE_PIC);

            UserDetailsData userData = new UserDetailsData();
            userData.setUserPrimaryId(userPrimaryId);
            userData.setName(name);
            userData.setEmail(email);
            userData.setProfession(profession);
            userData.setAbout(about);
            userData.setPhoneNumber(phoneNumber);
            userData.setProfilePic(profilePic);

            usersData.add(userData);

        });
        UserDetailsDB.insertOrUpdateMultipleUsers(context, usersData);
    }



    public static void setTaskDataToSQLite(Context context, Map<?,?> taskDetails) {

        ArrayList<TaskData> taskDataData = new ArrayList<>();

        taskDetails.forEach((key, object) -> {

            Map<?,?> map = (Map<?, ?>) object;
            String taskWebId = (String) key;
            String topic = (String) map.get(TOPIC);
            String taskIO = map.containsKey(TASK_ID) ? (String) map.get(TASK_ID) : generateNewTaskID(context);
            String description = (String) map.get(DESCRIPTION);
            long alarmDate = (long) map.get(FirebaseConstants.ALARM_DATE);
            byte repeatStatus = map.containsKey(REPEAT_STATUS) ? Byte.parseByte(map.get(REPEAT_STATUS)+"") : REPEAT_STATUS_NO_REPEAT;
            JSONArray dateArray = null;

            Log.d("TAG", "setTaskDataToSQLite: " + map.containsKey(DATE_ARRAY));

            try {
                dateArray = new JSONArray((String) (
                        map.containsKey(DATE_ARRAY) ?
                                map.get(DATE_ARRAY)
                                :
                                TaskConstants.DAYS_OF_WEEK
                ));
            }
            catch (JSONException e) {
                Log.w("TAG", "setTaskDataToSQLite: ", e);
            }


            ArrayList<Integer> dateArrayList = new ArrayList<>();
            for (int i = 0; i < dateArray.length(); i++) {
                try {
                    dateArrayList.add(dateArray.getInt(i));
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }


            taskDataData.add(
                    new TaskData(
                            TaskConstants.PRIVATE_NO,
                            topic,
                            description,
                            alarmDate,
                            repeatStatus,
                            dateArray.toString(),
                            repeatStatus == TaskConstants.REPEAT_STATUS_NO_REPEAT ?
                                    alarmDate :
                                    AddTask.rescheduleDateAndTIme(
                                            dateArrayList,
                                            alarmDate,
                                            repeatStatus
                                    ),
                            0,
                            TaskConstants.ALREADY_DONE_NO_BYTE,
                            TaskConstants.PINNED_NO,
                            null,
                            taskIO,
                            TaskConstants.PRIORITY_NORMAL,
                            taskWebId
                    )
            );
        });

        TasksDB.insertOrUpdateMultipleTask(context, taskDataData, null);

    }



    public static void setTaskSentDataToSQLite(Context context, Map<?,?> taskSentDetails) {

        ArrayList<ContentValues> taskSharedDataArray = new ArrayList<>();

        taskSentDetails.forEach((taskWebId, obj) -> {

            Map<?,?> subData = (Map<?,?>) obj;
            ArrayList<?> taskArray = (ArrayList<?>) subData.get(TASK_ARRAY);


            if (taskArray != null && !taskArray.isEmpty()) {

                for (Object data: taskArray) {

                    Map<?,?> d = (Map<?,?>) data;

                    String uid = String.valueOf(d.get(FirebaseConstants.USER_PRIMARY_ID));
                    boolean downloaded = Boolean.parseBoolean(String.valueOf(d.get(DOWNLOADED)));

                    ContentValues values = new ContentValues();

                    values.put(USER_PRIMARY_ID, uid);
                    values.put(TASK_WEB_ID, String.valueOf(taskWebId));
                    values.put(DOWNLOADED, downloaded == DOWNLOADED_YES ? DOWNLOADED_YES_BYTE : DOWNLOADED_NO_BYTE );

                    taskSharedDataArray.add(values);

                }

            }




        });




        insertOrUpdateMultipleTaskShared(context, taskSharedDataArray, TASK_SENT_TABLE_NAME);

    }



    private static void setTaskReceivedDataToSQLite(Context context, Map<?, ?> taskReceivedDetails) {

        Log.d("TAG", "setTaskReceivedDataToSQLite: " + taskReceivedDetails);

        ArrayList<ContentValues> taskSharedDataArray = new ArrayList<>();
        ArrayList<ContentValues> taskStatusDataArray = new ArrayList<>();

        taskReceivedDetails.forEach((taskWebId, obj) -> {

            Map<?,?> fd = (Map<?,?>) obj;

            byte downloaded = Boolean.parseBoolean(String.valueOf(fd.get(DOWNLOADED))) == DOWNLOADED_YES ? DOWNLOADED_YES_BYTE : DOWNLOADED_NO_BYTE;
            ArrayList<?> uidList = (ArrayList<?>) fd.get(FirebaseConstants.USER_PRIMARY_ID_LIST);

            if (uidList != null && !uidList.isEmpty()) {

                uidList.forEach((uid) -> {

                    ContentValues taskShared = new ContentValues();
                    ContentValues taskStatus = new ContentValues();

                    taskShared.put(USER_PRIMARY_ID, String.valueOf(uid));
                    taskShared.put(TASK_WEB_ID, String.valueOf(taskWebId));

                    taskStatus.put(USER_PRIMARY_ID, String.valueOf(uid));
                    taskStatus.put(TASK_WEB_ID, String.valueOf(taskWebId));
                    taskStatus.put(PERCENTAGE_COMPLETE, 0);
                    taskStatus.put(DOWNLOADED, downloaded);

                    taskSharedDataArray.add(taskShared);
                    taskStatusDataArray.add(taskStatus);

                });

            }

        });

        insertOrUpdateMultipleTaskShared(context, taskSharedDataArray, TASK_RECEIVED_TABLE_NAME);
        insertOrUpdateMultipleTaskStatus(context, taskStatusDataArray);

    }



    public static void setBlockedContactDataToSQLite(Context context, ArrayList<String> blockedContactDetails) {
        BlockedContactsDB.insertOrUpdateMultipleBlockedContact(context, blockedContactDetails);
    }



    public static void getOtherUserDetailsFromFirebase(Context context, ArrayList<String> userPrimaryIdList) {
        Map<String, ArrayList<String>> map = new HashMap<>();
        map.put(USER_PRIMARY_ID_LIST, userPrimaryIdList);

        FIREBASE_FUNCTIONS.getHttpsCallable(GET_USER_PROFILE_DATA)
                .call(new Gson().toJson(map))
                .addOnSuccessListener(httpsCallableResult -> {

                    if (httpsCallableResult.getData() != null) {
                        setUserDetailsToSQLite(context, (Map<?, ?>) httpsCallableResult.getData());
                    }
                })
                .addOnFailureListener(e -> AccountSettings.signOut(context, isSignedOut -> {
                    AccountSettings.signOut(context, val -> {});
                }))
                .addOnCompleteListener(task -> {
                });
    }



    public static void setTaskStatusDataToSQLite(Context context, Map<?,?> taskStatusDetails) {



    }


}
