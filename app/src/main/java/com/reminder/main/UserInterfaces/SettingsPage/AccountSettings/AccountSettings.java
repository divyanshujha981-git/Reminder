package com.reminder.main.UserInterfaces.SettingsPage.AccountSettings;

import static com.reminder.main.Firebase.FirebaseConstants.USER_NAME;
import static com.reminder.main.Firebase.FirebaseConstants.USER_PROFESSION;
import static com.reminder.main.Firebase.FirebaseConstants.USER_PROFILE_PIC;
import static com.reminder.main.UserInterfaces.HomePage.MainActivity.MainActivity.FIREBASE_AUTH;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.credentials.ClearCredentialStateRequest;
import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.exceptions.ClearCredentialException;
import androidx.preference.PreferenceManager;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.reminder.main.Custom.CustomInterfaces;
import com.reminder.main.R;
import com.reminder.main.SqLite.BlockedContact.BlockedContactConstant;
import com.reminder.main.SqLite.CommonDB.CommonDB;
import com.reminder.main.SqLite.Request.RequestConstants;
import com.reminder.main.SqLite.TaskShared.TaskSharedConstants;
import com.reminder.main.SqLite.Tasks.TaskConstants;
import com.reminder.main.SqLite.UserDetails.UserDetailsConstant;
import com.reminder.main.SqLite.UserDetails.UserDetailsData;

import java.util.concurrent.Executors;


public class AccountSettings extends AppCompatActivity {

    private MaterialCardView editProfile;
    private final FirebaseUser FIREBASE_USER = FIREBASE_AUTH.getCurrentUser();
    private final UserDetailsData userData = new UserDetailsData();
    private TextView setName, setProfession;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_setting_activity);



    }


    @Override
    protected void onStart() {
        super.onStart();
        declare();
        addActions();
        getAccountDataFromSQLite();
    }


    @Override
    protected void onResume() {
        super.onResume();

    }


    private void declare() {
        editProfile = findViewById(R.id.editButton);
        setName = findViewById(R.id.setName);
        setProfession = findViewById(R.id.setProfession);
    }


    private void addActions() {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.settingFrame, new AccountSettingsPreferenceFragment())
                .commit();


        editProfile.setOnClickListener(view -> startActivity(new Intent(AccountSettings.this, EditAccountInfo.class)));
    }


    public static void signOut(Context context, CustomInterfaces.SignOut signOut) {


        String[] tableNames = {
                BlockedContactConstant.BLOCKED_CONTACT_TABLE_NAME,
                RequestConstants.REQUEST_TABLE_NAME,
                TaskSharedConstants.TASK_SENT_TABLE_NAME,
                TaskSharedConstants.TASK_RECEIVED_TABLE_NAME,
                UserDetailsConstant.USER_DETAILS_TABLE_NAME
        };

        CommonDB commonDB = new CommonDB(context);
        SQLiteDatabase database = commonDB.getWritableDatabase();
        database.beginTransaction();

        for (String tableName : tableNames) {
            database.delete(tableName, "1", null);
        }
        database.delete(TaskConstants.TASK_TABLE_NAME, TaskConstants.TASK_WEB_ID + " IS NOT NULL ", null);

        database.setTransactionSuccessful();
        database.endTransaction();
        database.close();
        commonDB.close();


        // firebase part

        FirebaseAuth.getInstance().signOut();

        ClearCredentialStateRequest clearRequest = new ClearCredentialStateRequest();
        CredentialManager credentialManager = CredentialManager.create(context);

        credentialManager.clearCredentialStateAsync(
                clearRequest,
                new CancellationSignal(),
                Executors.newSingleThreadExecutor(),
                new CredentialManagerCallback<>() {
                    @Override
                    public void onResult(Void unused) {
                        Log.d("TAG", "onResult: **LOGGED OUT**");
                        SharedPreferences manager = PreferenceManager.getDefaultSharedPreferences(context);
                        SharedPreferences.Editor editor = manager.edit();
                        editor.putBoolean(context.getString(R.string.user_data_found_in_sqlite), false);
                        editor.apply();

                        signOut.signOut(true);
                    }

                    @Override
                    public void onError(@NonNull ClearCredentialException e) {
                        Log.e("TAG", "Couldn't clear user credentials: " + e.getLocalizedMessage());
                        signOut.signOut(false);
                    }
                }
        );




    }


    private void getAccountDataFromSQLite() {

        //new Thread(() -> {

        CommonDB commonDB = new CommonDB(this);
        Cursor cursor = commonDB.getReadableDatabase().rawQuery(
                " SELECT " +
                        USER_NAME + ", " +
                        USER_PROFILE_PIC + ", " +
                        USER_PROFESSION +
                        " FROM " + UserDetailsConstant.USER_DETAILS_TABLE_NAME + " WHERE " + UserDetailsConstant.USER_PRIMARY_ID + "=" + "\"" + FIREBASE_USER.getUid() + "\"",
                null
        );

        cursor.moveToFirst();


        Log.d("TAG", "getAccountDataFromSQLite: " + cursor.getString(1));

        userData.setName(cursor.getString(0));
        userData.setProfilePic(cursor.getString(1));
        userData.setProfession(cursor.getString(2));
        userData.setUserPrimaryId(FIREBASE_USER.getUid());



        //handler.post(this::setUserDataToUI);

        setUserDataToUI();

        cursor.close();
        commonDB.close();

        //}).start();

    }


    private void setUserDataToUI() {

        if (userData.getProfilePic() != null) {
            Glide.with(this)
                    .load(Uri.parse(userData.getProfilePic()))
                    .into((ShapeableImageView) findViewById(R.id.setProfilePic));
        }
        //Log.d("TAG", "getAccountDataFromSQLite: " + userData.getName());
        //Log.d("TAG", "getAccountDataFromSQLite: " + userData.getProfilePic());
        //Log.d("TAG", "getAccountDataFromSQLite: " + userData.getProfession());

        setName.setText(userData.getName());
        setProfession.setText(userData.getProfession());

    }






}
