package com.reminder.main.UserInterfaces.Ppp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.reminder.main.Other.ApplicationCustomInterfaces;
import com.reminder.main.R;
import com.reminder.main.SqLite.Tasks.TaskConstants;
import com.reminder.main.UserInterfaces.PrivatePage.PrivatePages;
import com.reminder.main.UserInterfaces.SettingsPage.AllSettings.SettingsPage;
import com.reminder.main.UserInterfaces.SettingsPage.LockSettings.LockData;
import com.reminder.main.UserInterfaces.SettingsPage.LockSettings.LockSettings;
import com.reminder.main.UserInterfaces.SettingsPage.LockSettings.PasswordLock.PasswordLock;
import com.reminder.main.UserInterfaces.SettingsPage.LockSettings.PatternLock.PatternLock;
import com.reminder.main.UserInterfaces.SettingsPage.LockSettings.PinLock.PinLock;
import com.reminder.main.UserInterfaces.TaskViewPage.TaskViewMain;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class Ppp extends AppCompatActivity implements ApplicationCustomInterfaces.AllowUserToNavigate {
    private Pattern pattern;
    private Pin pin;
    private Password password;
    private String pinKey, passwordKey, patternKey;
    private final byte PATTERN = 0;
    private final byte PIN = 1;
    private final byte PASSWORD = 2;
    private MaterialButton patternBTN, pinBTN, passwordBTN;

    public static final String
            FOR_PAGE = "fp",
            LOCKED_PAGE = "lp",
            SETTINGS_PAGE = "sp",
            TASK_MAIN_PAGE = "tmp",
            TASK_VIEW_PAGE = "tvp";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ppp);


        patternBTN = findViewById(R.id.patternBTN);
        pinBTN = findViewById(R.id.pinBTN);
        passwordBTN = findViewById(R.id.passwordBTN);

        patternBTN.setOnClickListener(v -> setPage(PATTERN));
        pinBTN.setOnClickListener(v -> setPage(PIN));
        passwordBTN.setOnClickListener(v -> setPage(PASSWORD));

    }


    @Override
    protected void onResume() {
        super.onResume();

        setKeys();

        pattern = new Pattern(patternKey);
        pin = new Pin(pinKey);
        password = new Password(passwordKey);

        if (pinKey == null && passwordKey == null && patternKey == null) {
            setPage((byte) -1);
        } else if (patternKey != null) {
            setPage(PATTERN);
        } else if (pinKey != null) {
            setPage(PIN);
        } else {
            setPage(PASSWORD);
        }


    }

    private void setKeys() {
        try {
            FileInputStream inputStream = openFileInput(LockSettings.LOCK_FILE);
            InputStreamReader reader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String line = bufferedReader.readLine();
            StringBuilder builder = new StringBuilder();
            while (line != null) {
                builder.append(line);
                line = bufferedReader.readLine();
            }
            JSONObject jsonObject = new JSONObject(builder.toString());
            jsonObject = jsonObject.getJSONObject(LockData.KEYS);

            try {
                pinKey = jsonObject.getString(PinLock.PIN);
            } catch (JSONException ignored) {
            }
            try {
                passwordKey = jsonObject.getString(PasswordLock.PASSWORD);
            } catch (JSONException ignored) {
            }
            try {
                patternKey = jsonObject.getString(PatternLock.PATTERN);
            } catch (JSONException ignored) {
            }


        } catch (IOException | JSONException e) {
            //throw new RuntimeException(e);
        }
    }


    public static Map<String, String> getKeys(Context context) {
        Map<String, String> map = new HashMap<>();

        try {
            FileInputStream inputStream = context.openFileInput(LockSettings.LOCK_FILE);
            InputStreamReader reader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String line = bufferedReader.readLine();
            StringBuilder builder = new StringBuilder();
            while (line != null) {
                builder.append(line);
                line = bufferedReader.readLine();
            }
            JSONObject jsonObject = new JSONObject(builder.toString());
            jsonObject = jsonObject.getJSONObject(LockData.KEYS);

            try {
                map.put(PinLock.PIN, jsonObject.getString(PinLock.PIN));
            } catch (JSONException ignored) {
            }
            try {
                map.put(PinLock.PIN, jsonObject.getString(PasswordLock.PASSWORD));
            } catch (JSONException ignored) {
            }
            try {
                map.put(PinLock.PIN, jsonObject.getString(PatternLock.PATTERN));
            } catch (JSONException ignored) {
            }


        } catch (IOException | JSONException e) {
            //throw new RuntimeException(e);
        }

        return map.isEmpty() ? null : map;
    }


    private void setPage(byte key) {
        switch (key) {
            case PATTERN:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.pppFrame, pattern)
                        .commit();
                patternBTN.setVisibility(View.GONE);
                pinBTN.setVisibility(View.VISIBLE);
                passwordBTN.setVisibility(View.VISIBLE);
                break;
            case PIN:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.pppFrame, pin)
                        .commit();
                pinBTN.setVisibility(View.GONE);
                patternBTN.setVisibility(View.VISIBLE);
                passwordBTN.setVisibility(View.VISIBLE);
                break;
            case PASSWORD:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.pppFrame, password)
                        .commit();
                passwordBTN.setVisibility(View.GONE);
                patternBTN.setVisibility(View.VISIBLE);
                pinBTN.setVisibility(View.VISIBLE);
                break;
            default:
                startActivity(new Intent(this, PPP_Process_Button_Layout.class));
        }

    }


    @Override
    public void authorized() {
        String page = getIntent().getStringExtra(FOR_PAGE);
        switch (Objects.requireNonNull(page)) {
            case SETTINGS_PAGE:
                startActivity(new Intent(this, SettingsPage.class));
                break;
            case LOCKED_PAGE:
                startActivity(new Intent(this, PrivatePages.class));
                break;
            case TASK_MAIN_PAGE:
                String taskID = getIntent().getStringExtra(TaskConstants.TASK_ID);
                if (taskID == null) {
                    taskID = String.valueOf(getIntent().getLongExtra(TaskConstants.TASK_ID, 0));
                }
                startActivity(new Intent(this, TaskViewMain.class).putExtra(TaskConstants.TASK_ID, taskID));
                break;
        }

        finish();


    }


}
