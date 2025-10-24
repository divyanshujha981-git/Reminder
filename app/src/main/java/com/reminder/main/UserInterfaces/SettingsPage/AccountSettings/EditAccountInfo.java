package com.reminder.main.UserInterfaces.SettingsPage.AccountSettings;


import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.reminder.main.Firebase.FirebaseConstants.BLOCKED_CONTACTS;
import static com.reminder.main.Firebase.FirebaseConstants.DESCRIPTION;
import static com.reminder.main.Firebase.FirebaseConstants.GET_ACCOUNT_DATA;
import static com.reminder.main.Firebase.FirebaseConstants.GET_USER_PROFILE_DATA;
import static com.reminder.main.Firebase.FirebaseConstants.PROFILE_DETAILS;
import static com.reminder.main.Firebase.FirebaseConstants.RECEIVE_REQUEST;
import static com.reminder.main.Firebase.FirebaseConstants.REPEAT_STATUS;
import static com.reminder.main.Firebase.FirebaseConstants.REQUESTS;
import static com.reminder.main.Firebase.FirebaseConstants.REQUEST_RECEIVED_BYTE;
import static com.reminder.main.Firebase.FirebaseConstants.REQUEST_SENT;
import static com.reminder.main.Firebase.FirebaseConstants.REQUEST_SENT_BYTE;
import static com.reminder.main.Firebase.FirebaseConstants.REQUEST_TYPE;
import static com.reminder.main.Firebase.FirebaseConstants.SET_ACCOUNT_DATA;
import static com.reminder.main.Firebase.FirebaseConstants.STATUS;
import static com.reminder.main.Firebase.FirebaseConstants.STATUS_ACCEPTED;
import static com.reminder.main.Firebase.FirebaseConstants.STATUS_ACCEPTED_BYTE;
import static com.reminder.main.Firebase.FirebaseConstants.STATUS_PENDING_BYTE;
import static com.reminder.main.Firebase.FirebaseConstants.TASKS;
import static com.reminder.main.Firebase.FirebaseConstants.TASK_RECEIVED;
import static com.reminder.main.Firebase.FirebaseConstants.TASK_SENT;
import static com.reminder.main.Firebase.FirebaseConstants.TASK_STATUS;
import static com.reminder.main.Firebase.FirebaseConstants.TOPIC;
import static com.reminder.main.Firebase.FirebaseConstants.USER_ABOUT;
import static com.reminder.main.Firebase.FirebaseConstants.USER_EMAIL;
import static com.reminder.main.Firebase.FirebaseConstants.USER_NAME;
import static com.reminder.main.Firebase.FirebaseConstants.USER_PHONE_NUMBER;
import static com.reminder.main.Firebase.FirebaseConstants.USER_PRIMARY_ID;
import static com.reminder.main.Firebase.FirebaseConstants.USER_PRIMARY_ID_LIST;
import static com.reminder.main.Firebase.FirebaseConstants.USER_PROFESSION;
import static com.reminder.main.Firebase.FirebaseConstants.USER_PROFILE_PIC;
import static com.reminder.main.SqLite.Tasks.TaskConstants.REPEAT_STATUS_NO_REPEAT;
import static com.reminder.main.UserInterfaces.HomePage.MainActivity.MainActivity.FIREBASE_AUTH;
import static com.reminder.main.UserInterfaces.HomePage.MainActivity.MainActivity.FIREBASE_FUNCTIONS;
import static com.reminder.main.UserInterfaces.LoginRegisterPage.LoginRegister.SIGN_IN_TYPE_GOOGLE;
import static com.reminder.main.UserInterfaces.LoginRegisterPage.LoginRegister.SIGN_IN_TYPE_PHONE;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.preference.PreferenceManager;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.reminder.main.Firebase.FirebaseConstants;
import com.reminder.main.Other.AlertDialogueForAll;
import com.reminder.main.R;
import com.reminder.main.SqLite.BlockedContact.BlockedContactsDB;
import com.reminder.main.SqLite.CommonDB.CommonDB;
import com.reminder.main.SqLite.Request.RequestConstants;
import com.reminder.main.SqLite.Request.RequestData;
import com.reminder.main.SqLite.Request.RequestsDB;
import com.reminder.main.SqLite.Tasks.TaskConstants;
import com.reminder.main.SqLite.Tasks.TaskData;
import com.reminder.main.SqLite.Tasks.TasksDB;
import com.reminder.main.SqLite.UserDetails.UserDetailsConstant;
import com.reminder.main.SqLite.UserDetails.UserDetailsDB;
import com.reminder.main.SqLite.UserDetails.UserDetailsData;
import com.reminder.main.UserInterfaces.AddTaskPage.AddTask;
import com.reminder.main.UserInterfaces.HomePage.MainActivity.MainActivity;
import com.reminder.main.UserInterfaces.LoginRegisterPage.LoginRegister;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class EditAccountInfo extends AppCompatActivity {

    private TextInputEditText setName, setEmail, setProfession, setPhoneNumber, setAbout;
    private ShapeableImageView setProfilePic;
    private MaterialButton saveButton, cancelButton;
    private final UserDetailsData userDetailsData = new UserDetailsData();
    private final FirebaseFunctions FIREBASE_FUNCTION = FIREBASE_FUNCTIONS;
    private CoordinatorLayout circularProgress;
    private String GET_SIGN_IN_TYPE;
    private final FirebaseUser FIREBASE_USER = FIREBASE_AUTH.getCurrentUser();
    private AlertDialogueForAll alertDialogueForAll;
    private boolean dataFoundInFirebase = false;
    private final ArrayList<String> userPrimaryIdList = new ArrayList<>();

    private final String TAG = "TAG";






    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_setting_activity_info);

        declaration();
        assignMethods();

    }



    @Override
    protected void onStart() {
        super.onStart();
        getAccountDetails();
    }



    private void declaration() {
        GET_SIGN_IN_TYPE = getIntent().getStringExtra(LoginRegister.SIGN_IN_TYPE);



        alertDialogueForAll = new AlertDialogueForAll(this);

        setName = findViewById(R.id.setName);
        setEmail = findViewById(R.id.setEmail);
        setProfession = findViewById(R.id.setProfession);
        setPhoneNumber = findViewById(R.id.setPhoneNumber);
        setAbout = findViewById(R.id.setAbout);
        setProfilePic = findViewById(R.id.setProfilePic);
        circularProgress = findViewById(R.id.circular_progress_middle_view);

        saveButton = findViewById(R.id.save);
        cancelButton = findViewById(R.id.cancel);


        assignSignInType();

    }



    private void assignSignInType() {

        if (GET_SIGN_IN_TYPE == null) {

            saveButton.setText(getString(R.string.save));

            if (FIREBASE_USER != null) {

                String providerId = FIREBASE_USER.getProviderData().get(1).getProviderId();
                Log.e(TAG, "declaration:" + providerId + " = " + PhoneAuthProvider.PROVIDER_ID);

                switch (providerId) {
                    case GoogleAuthProvider.PROVIDER_ID:
                        GET_SIGN_IN_TYPE = SIGN_IN_TYPE_GOOGLE;
                        break;
                    case PhoneAuthProvider.PROVIDER_ID:
                        GET_SIGN_IN_TYPE = SIGN_IN_TYPE_PHONE;
                        break;
                    default:
                        Log.e(TAG, "declaration: ** DECLARE THE \"GET_SIGN_IN_TYPE\" **" );
                        Log.e(TAG, "declaration: ** DECLARE THE ??????????????? **" );
                        finish();
                }


            }
            else {
                Log.e(TAG, "declaration: ** DECLARE THE \"GET_SIGN_IN_TYPE\" **" );
                finish();
            }

        }
        else {
            // so that it will know the parent activity was not LoginActivity
            cancelButton.setVisibility(GONE);
            ((LinearLayout) cancelButton.getParent()).getChildAt(1).setVisibility(GONE);
        }

    }



    private void assignMethods() {
        saveButton.setOnClickListener(v -> updateProfileDataFirebase());
        cancelButton.setOnClickListener(v -> finish());


        if (Objects.equals(GET_SIGN_IN_TYPE, SIGN_IN_TYPE_GOOGLE)) {
            setEmail.setEnabled(false);
        }
        else {
            setPhoneNumber.setEnabled(false);
        }


    }



    private void getAccountDetails() {

        if (MainActivity.userFoundInSQLite(this)) {

            CommonDB commonDB = new CommonDB(this);
            Cursor cursor = commonDB.getReadableDatabase().rawQuery(
                    " SELECT " +
                            USER_NAME + ", " +
                            USER_EMAIL + ", " +
                            USER_PROFILE_PIC + ", " +
                            USER_PHONE_NUMBER + ", " +
                            USER_PROFESSION + ", " +
                            USER_ABOUT + ", " +
                            USER_PRIMARY_ID +
                        " FROM " + UserDetailsConstant.USER_DETAILS_TABLE_NAME + " WHERE " + UserDetailsConstant.USER_PRIMARY_ID + "=" + "\"" + FIREBASE_USER.getUid() + "\"",
                    null
            );

            cursor.moveToFirst();

            Map<String, Object> rowMap = new HashMap<>();

            for (int i = 0; i < cursor.getColumnCount(); i++) {
                if (Objects.equals(cursor.getString(i), "null")) continue;
                Log.d(TAG, "getAccountDetails: " + cursor.getColumnName(i) + " = " + cursor.getString(i));
                rowMap.put(cursor.getColumnName(i), cursor.getString(i));
            }

            setUserData(rowMap);
            setUserDataToUI();

            circularProgress.setVisibility(GONE);

            cursor.close();
            commonDB.close();

        }

        else {

            Map<String, Object> map = new HashMap<>();
            map.put(FirebaseConstants.DEVICE_MESSAGE_TOKEN, FirebaseMessaging.getInstance().getToken().getResult());

            FIREBASE_FUNCTION.getHttpsCallable(GET_ACCOUNT_DATA)
                    .call(new Gson().toJson(map))
                    .addOnSuccessListener(httpsCallableResult -> {
                        // set account data
                        dataFoundInFirebase = true;
                        Log.d(TAG, "getAccountDetails: " + httpsCallableResult.getData());
                        setAccountDataToSQLite((Map<?, ?>) httpsCallableResult.getData());
                        setUserData((Map<?, ?>) ((Map<?, ?>) httpsCallableResult.getData()).get(PROFILE_DETAILS));
                        setUserDataToUI();
                    })
                    .addOnFailureListener(e -> {
                        // get user data
                        Log.d(TAG, "getAccountDetails: " + e.getMessage());

                        Map<String, String> map1 = new HashMap<>();
                        map1.put(USER_NAME, "User-"+ Calendar.getInstance().getTimeInMillis());
                        if (GET_SIGN_IN_TYPE.equals(SIGN_IN_TYPE_GOOGLE)) {
                            map1.put(USER_EMAIL, FIREBASE_USER.getEmail());
                        }
                        else {
                            map1.put(USER_PHONE_NUMBER, FIREBASE_USER.getPhoneNumber());
                        }

                        if (FIREBASE_USER.getPhotoUrl() != null) {
                            map1.put(USER_PROFILE_PIC, FIREBASE_USER.getPhotoUrl().toString());
                        }

                        setUserData();
                        //setProfileDataToSQLite(map1);
                        setUserDataToUI();
                    })
                    .addOnCompleteListener(task -> {
                        // do required stuffs that are supposed to be done after the response from server
                        circularProgress.setVisibility(GONE);
                    });


        }





    }



    private void setUserData() {
        if (FIREBASE_USER.getDisplayName() != null) userDetailsData.setName(FIREBASE_USER.getDisplayName());
        if (FIREBASE_USER.getEmail() != null) userDetailsData.setEmail(FIREBASE_USER.getEmail());
        if (FIREBASE_USER.getPhoneNumber() != null) userDetailsData.setPhoneNumber(FIREBASE_USER.getPhoneNumber());
        if (FIREBASE_USER.getPhotoUrl() != null) userDetailsData.setProfilePic(String.valueOf(FIREBASE_USER.getPhotoUrl()));
        userDetailsData.setUserPrimaryId(FIREBASE_USER.getUid());
    }



    private void setUserData(Map<?, ?> profileDetails) {

        //Map<String,String> profileDetails = (Map<String,String>) data.get(FirebaseConstants.PROFILE_DETAILS);
        if (profileDetails != null) {
            if (profileDetails.get(USER_NAME) != null) userDetailsData.setName((String) profileDetails.get(USER_NAME));
            if (profileDetails.get(USER_EMAIL) != null) userDetailsData.setEmail((String) profileDetails.get(USER_EMAIL));
            if (profileDetails.get(USER_PHONE_NUMBER) != null) userDetailsData.setPhoneNumber((String) profileDetails.get(USER_PHONE_NUMBER));
            if (profileDetails.get(USER_PROFESSION) != null) userDetailsData.setProfession((String) profileDetails.get(USER_PROFESSION));
            if (profileDetails.get(USER_ABOUT) != null) userDetailsData.setAbout((String) profileDetails.get(USER_ABOUT));
            if (profileDetails.get(USER_PROFILE_PIC) != null) userDetailsData.setProfilePic((String) profileDetails.get(USER_PROFILE_PIC));
            userDetailsData.setUserPrimaryId(FIREBASE_USER.getUid());
        }



    }



    private void setUserDataToUI() {

        if (GET_SIGN_IN_TYPE.equals(SIGN_IN_TYPE_GOOGLE)) setEmail.setEnabled(false);
        else setPhoneNumber.setEnabled(false);

        setName.setText(userDetailsData.getName());
        setEmail.setText(userDetailsData.getEmail());
        setProfession.setText(userDetailsData.getProfession());
        setPhoneNumber.setText(userDetailsData.getPhoneNumber());
        setAbout.setText(userDetailsData.getAbout());

        if (userDetailsData.getProfilePic() != null && !userDetailsData.getProfilePic().isEmpty()) {
            Glide.with(this)
                    .load(Uri.parse(userDetailsData.getProfilePic()))
                    .into(setProfilePic);
        }

    }



    private void setAccountDataToSQLite(Map<?, ?> data) {

        Log.d(TAG, "setAccountDataToSQLite: " + data);

        Map<?,?> profileDetails = (Map<?,?>) data.get(PROFILE_DETAILS);
        Map<?,?> requestDetails = (Map<?,?>) data.get(REQUESTS);
        Map<?,?> taskDetails = (Map<?,?>) data.get(TASKS);
        ArrayList<String> blockedContactsDetails = (ArrayList<String>) data.get(BLOCKED_CONTACTS);

        if (profileDetails != null) setProfileDataToSQLite(profileDetails);
        else startActivity(new Intent(this, EditAccountInfo.class));

        if (requestDetails != null) setRequestDataToSQLite(requestDetails);
        if (taskDetails != null) taskSubCollections(taskDetails);
        if (blockedContactsDetails != null) setBlockedContactDataToSQLite(blockedContactsDetails);

        getOtherUserDetailsFromFirebase();

    }



    private void setProfileDataToSQLite(Map<?,?> profileDetails) {

        Log.d(TAG, "setProfileData: --->>");

        boolean isAccountPrivate = false;

        try {
            isAccountPrivate = Boolean.parseBoolean(String.valueOf(profileDetails.get(FirebaseConstants.IS_ACCOUNT_PRIVATE)));

        }
        catch (Exception ignored) {
            Log.w(TAG, "setProfileDataToSQLite: NO ACCOUNT PRIVACY FOUND");
        }

        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        editor.putBoolean(getString(R.string.isAccountPrivate), isAccountPrivate);
        editor.apply();

        String userName = String.valueOf(profileDetails.get(USER_NAME));
        String userEmail = String.valueOf(profileDetails.get(USER_EMAIL));
        String phoneNumber = String.valueOf(profileDetails.get(USER_PHONE_NUMBER));
        String userPrimaryId = FIREBASE_USER.getUid();
        String userAbout = String.valueOf(profileDetails.get(USER_ABOUT));
        String userProfession = String.valueOf(profileDetails.get(USER_PROFESSION));
        String userProfilePic = String.valueOf(profileDetails.get(USER_PROFILE_PIC));

        Log.d(TAG, "setProfileDataToSQLite: " + phoneNumber);

        if (userProfession.isEmpty() || userName.isEmpty()) {
            startActivity(new Intent(this, EditAccountInfo.class));
        }
        else {
            UserDetailsData data = new UserDetailsData();
            data.setName(userName);
            data.setEmail(userEmail);
            data.setProfilePic(userProfilePic);
            data.setUserPrimaryId(userPrimaryId);
            data.setProfession(userProfession);
            data.setAbout(userAbout);
            data.setPhoneNumber(phoneNumber);

            UserDetailsDB.insertUser(this, data);
        }



    }



    private void setRequestDataToSQLite(Map<?,?> requestDetails) {

        Log.d(TAG, "setRequestDataToSQLite: " + requestDetails);

        // --------- Shared preference part ------ //

        boolean receiveRequest = true;

        try {
            receiveRequest = (boolean) requestDetails.get(RECEIVE_REQUEST);
            requestDetails.remove(RECEIVE_REQUEST);

        } catch (Exception ignored) {}

        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        editor.putBoolean(getString(R.string.receiveRequest), receiveRequest);
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

        RequestsDB.insertORUpdateMultipleRequest(EditAccountInfo.this, RequestConstants.REQUEST_TABLE_NAME, requestDataArrayList);



    }



    private void taskSubCollections(Map<?,?> taskDetails) {

        Map<?,?> taskData = (Map<?, ?>) taskDetails.get(TASKS);
        Map<?,?> taskSent = (Map<?, ?>) taskDetails.get(TASK_SENT);
        Map<?,?> taskReceived = (Map<?, ?>) taskDetails.get(TASK_RECEIVED);
        Map<?,?> taskStatus = (Map<?, ?>) taskDetails.get(TASK_STATUS);

        if (taskData != null) setTaskDataToSQLite(taskData);
        if (taskSent != null) setTaskSentDataToSQLite(taskSent);
        if (taskReceived != null) setTaskReceivedDataToSQLite(taskReceived);
        if (taskStatus != null) setTaskStatusDataToSQLite(taskStatus);

        // --------- Shared preference part ------ //

        boolean receiveTask = true;
        boolean taskAutoDownload = false;

        try {
            receiveTask = (boolean) taskDetails.get(FirebaseConstants.RECEIVE_TASK);
        } catch (Exception ignored) {}

        try {
            taskAutoDownload = (boolean) taskDetails.get(FirebaseConstants.TASK_AUTO_DOWNLOAD);
        } catch (Exception ignored) {}

        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        editor.putBoolean(getString(R.string.receiveTask), receiveTask);
        editor.putBoolean(getString(R.string.taskAutoDownload), taskAutoDownload);
        editor.apply();

        // --------- --------------------- ------ //

    }



    private void setUserDetailsToSQLite(Map<?,?> userDetails) {

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
        UserDetailsDB.insertOrUpdateMultipleUsers(this, usersData);
    }



    private void setTaskDataToSQLite(Map<?,?> taskDetails) {

        ArrayList<TaskData> taskDataData = new ArrayList<>();

        taskDetails.forEach((key, object) -> {

            Map<?,?> map = (Map<?, ?>) object;
            String taskWebId = (String) key;
            String topic = (String) map.get(TOPIC);
            String description = (String) map.get(DESCRIPTION);
            long alarmDate = (long) map.get(FirebaseConstants.ALARM_DATE);
            byte repeatStatus = map.containsKey(REPEAT_STATUS) ? (byte) map.get(REPEAT_STATUS) : REPEAT_STATUS_NO_REPEAT;

            ArrayList<Integer> dateArrayList = null;
            String dateArray;
            try {
                dateArrayList = (ArrayList<Integer>) taskDetails.get(FirebaseConstants.DATE_ARRAY);
                dateArray = new JSONArray(dateArrayList).toString();
            }  catch (Exception e) {
                dateArray = TaskConstants.DAYS_OF_WEEK;
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
                            TaskConstants.PRIVATE_NO,
                            topic,
                            description,
                            alarmDate,
                            repeatStatus,
                            dateArray,
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
                            taskWebId.split("_")[0],
                            TaskConstants.PRIORITY_NORMAL,
                            taskWebId
                    )
            );
        });

        TasksDB.insertMultipleTask(this, taskDataData);

    }



    private void setTaskSentDataToSQLite(Map<?,?> taskSentDetails) {



    }



    private void setTaskReceivedDataToSQLite(Map<?,?> taskReceivedDetails) {



    }



    private void setTaskStatusDataToSQLite(Map<?,?> taskStatusDetails) {



    }



    private void setBlockedContactDataToSQLite(ArrayList<String> blockedContactDetails) {
        BlockedContactsDB.insertMultipleBlockedContact(this, blockedContactDetails);
    }



    private void getOtherUserDetailsFromFirebase() {
        circularProgress.setVisibility(VISIBLE);
        Map<String, ArrayList<String>> map = new HashMap<>();
        map.put(USER_PRIMARY_ID_LIST, userPrimaryIdList);

        FIREBASE_FUNCTION.getHttpsCallable(GET_USER_PROFILE_DATA)
                .call(new Gson().toJson(map))
                .addOnSuccessListener(httpsCallableResult -> {

                    if (httpsCallableResult.getData() != null) {
                        setUserDetailsToSQLite((Map<?, ?>) httpsCallableResult.getData());
                    }
                })
                .addOnFailureListener(e -> AccountSettings.signOut(this, isSignedOut -> {
                    if (isSignedOut) finish();
                }))
                .addOnCompleteListener(task -> {
                    circularProgress.setVisibility(GONE);
                });
    }



    private void updateProfileDataFirebase() {
        String userName = "";
        String userProfession = "";
        String userAbout = "";
        String userPhoneNumber = "";
        String userEmail = "";

        if (setName.getText() != null) {
            userName = setName.getText().toString().trim();
        }

        if (setPhoneNumber.getText() != null) {
            userPhoneNumber = setPhoneNumber.getText().toString().trim();
        }

        if (setProfession.getText() != null) {
            userProfession = setProfession.getText().toString().trim();
        }

        if (setAbout.getText() != null) {
            userAbout = setAbout.getText().toString().trim();
        }

        if (setEmail.getText() != null) {
            userEmail = setEmail.getText().toString().trim();
        }



        if (userEmail.trim().isEmpty() && GET_SIGN_IN_TYPE.equals(SIGN_IN_TYPE_GOOGLE)) {
            alertDialogueForAll.showAlert("Please enter your email", "Ok");
            return;
        }

        if (userName.isEmpty()) {
            alertDialogueForAll.showAlert("Please enter your name", "Ok");
            return;
        }

        if (userPhoneNumber.isEmpty() && GET_SIGN_IN_TYPE.equals(SIGN_IN_TYPE_PHONE)){
            alertDialogueForAll.showAlert("Please enter your phone number", "Ok");
            return;
        }

        if (userProfession.isEmpty()) {
            alertDialogueForAll.showAlert("Please enter your profession", "Ok");
            return;
        }



        if (userName.length() > 1) {
            userName = userName.substring(0, 1).toUpperCase() + userName.substring(1);
        }

        if (userProfession.length() > 1) {
            userProfession = userProfession.substring(0, 1).toUpperCase() + userProfession.substring(1);
        }

        if (userAbout.length() > 1) {
            userAbout = userAbout.substring(0, 1).toUpperCase() + userAbout.substring(1);
        }




        Map<String, String> map = new HashMap<>();
        if (dataFoundInFirebase) {

            Log.d(TAG, "updateProfileDataFirebase: TRIGGERED AS FOUND");

            if (!userName.equals(userDetailsData.getName())) {
                map.put(USER_NAME, userName);
            }

            if (!userPhoneNumber.equals(userDetailsData.getPhoneNumber())) {
                map.put(USER_PHONE_NUMBER, userPhoneNumber);
            }

            if (!userProfession.equals(userDetailsData.getProfession())) {
                map.put(USER_PROFESSION, userProfession);
            }

            if (!userAbout.equals(userDetailsData.getAbout())) {
                map.put(USER_ABOUT, userAbout);
            }

            if (!userEmail.equals(userDetailsData.getEmail())) {
                map.put(USER_EMAIL, userEmail);
            }

        }
        else {

            Log.d(TAG, "updateProfileDataFirebase: TRIGGERED AS NOT FOUND");

            map.put(USER_NAME, userName);

            if (!userEmail.isEmpty()) map.put(USER_EMAIL, userEmail);

            if (!userPhoneNumber.isEmpty()) map.put(USER_PHONE_NUMBER, userPhoneNumber);

            map.put(USER_PROFESSION, userProfession);

            if (FIREBASE_USER.getPhotoUrl() != null) map.put(USER_PROFILE_PIC, FIREBASE_USER.getPhotoUrl().toString());

            if (!userAbout.isEmpty()) map.put(USER_ABOUT, userAbout);

        }


        circularProgress.setVisibility(VISIBLE);


        Log.d(TAG, "updateProfileData: ------------------" );
        Log.d(TAG, "updateProfileData: " + userName);
        Log.d(TAG, "updateProfileData: " + userDetailsData.getName());
        Log.d(TAG, "updateProfileData: ");
        Log.d(TAG, "updateProfileData: " + userProfession);
        Log.d(TAG, "updateProfileData: " + userDetailsData.getProfession());
        Log.d(TAG, "updateProfileData: ");
        Log.d(TAG, "updateProfileData: " + userAbout);
        Log.d(TAG, "updateProfileData: " + userDetailsData.getAbout());
        Log.d(TAG, "updateProfileData: ");
        Log.d(TAG, "updateProfileData: " + userPhoneNumber);
        Log.d(TAG, "updateProfileData: " + userDetailsData.getPhoneNumber());
        Log.d(TAG, "updateProfileData: ");
        Log.d(TAG, "updateProfileData: " + userEmail);
        Log.d(TAG, "updateProfileData: " + userDetailsData.getEmail());
        Log.d(TAG, "updateProfileData: ------------------");


        if (!map.isEmpty()) {

            Log.d(TAG, "updateProfileDataFirebase: KEYS = " + map.keySet());

            FIREBASE_FUNCTION.getHttpsCallable(SET_ACCOUNT_DATA)
                    .call(new Gson().toJson(map))
                    .addOnSuccessListener(httpsCallableResult ->
                            new Thread(() -> {
                                Log.d(TAG, "updateProfileDataFirebase: ** PROFILE_UPDATED **");
                                ContentValues values = new ContentValues();
                                for (String key : map.keySet()) {
                                    Log.d(TAG, "updateProfileDataFirebase: " + key+ " = " + map.get(key));
                                    values.put(key, map.get(key));
                                }
                                values.put(USER_PRIMARY_ID, userDetailsData.getUserPrimaryId());
                                UserDetailsDB.insertOrUpdateSingleUser(this, values);

                                SharedPreferences manager = PreferenceManager.getDefaultSharedPreferences(this);
                                SharedPreferences.Editor editor = manager.edit();
                                editor.putBoolean(getString(R.string.user_data_found_in_sqlite), true);
                                editor.apply();

                                new Handler(getMainLooper()).post(() -> {
                                    Toast.makeText(EditAccountInfo.this, "Profile updated", Toast.LENGTH_SHORT).show();
                                    circularProgress.setVisibility(GONE);
                                    finish();
                                });
                            }).start())
                    .addOnFailureListener(e -> {
                        // Sign out
                        Log.d(TAG, "updateProfileDataFirebase: "  + e);
                        circularProgress.setVisibility(GONE);
                        AccountSettings.signOut(this, isSignedOut -> finish());

                    })
                    .addOnCompleteListener(task -> Log.d(TAG, "onComplete: **RESPONSE RECEIVED**"));


        }
        else {

            SharedPreferences manager = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = manager.edit();
            editor.putBoolean(getString(R.string.user_data_found_in_sqlite), true);
            editor.apply();

            finish();

        }



    }





}
