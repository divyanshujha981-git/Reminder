package com.reminder.main.UserInterfaces.SettingsPage.LockSettings;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.util.Log;

import com.reminder.main.UserInterfaces.SettingsPage.LockSettings.PasswordLock.PasswordLock;
import com.reminder.main.UserInterfaces.SettingsPage.LockSettings.PatternLock.PatternLock;
import com.reminder.main.UserInterfaces.SettingsPage.LockSettings.PinLock.PinLock;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class LockData {

    public static final String KEYS = "keys";

    public static void setPattern(Context context, String pattern) {
        Map<String, Object> parent = new HashMap<>();
        Map<String, String> child = new HashMap<>();
        child.put(PatternLock.PATTERN, pattern);
        parent.put(LockData.KEYS, child);


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
            jsonObject = new JSONObject(jsonObject.getJSONObject(LockData.KEYS).toString());


            try {
                Log.d("TAG", "setPattern: " + jsonObject);
                child.put(PinLock.PIN, jsonObject.getString(PinLock.PIN));
            } catch (JSONException ignored) {
            }
            try {
                child.put(PasswordLock.PASSWORD, jsonObject.getString(PasswordLock.PASSWORD));
            } catch (JSONException ignored) {
            }


        } catch (FileNotFoundException ignored) {
            new File(LockSettings.LOCK_FILE);
        } catch (IOException | JSONException e) {
            throw new RuntimeException(e);
        }


        JSONObject jsonObject = new JSONObject(parent);
        try {
            FileOutputStream file = context.openFileOutput(LockSettings.LOCK_FILE, MODE_PRIVATE);
            file.write((jsonObject.toString()).getBytes());
            file.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }


    public static void setPin(Context context, String pin) {
        Map<String, Object> parent = new HashMap<>();
        Map<String, String> child = new HashMap<>();
        child.put(PinLock.PIN, pin);
        parent.put(LockData.KEYS, child);


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
            jsonObject = new JSONObject(jsonObject.getJSONObject(LockData.KEYS).toString());

            try {
                Log.d("TAG", "setPin: " + jsonObject);
                child.put(PatternLock.PATTERN, jsonObject.getString(PatternLock.PATTERN));
            } catch (JSONException ignored) {
            }

            try {
                child.put(PasswordLock.PASSWORD, jsonObject.getString(PasswordLock.PASSWORD));
            } catch (JSONException ignored) {
            }


        } catch (FileNotFoundException ignored) {
            new File(LockSettings.LOCK_FILE);
        } catch (IOException | JSONException e) {
            //throw new RuntimeException(e);
        }


        JSONObject jsonObject = new JSONObject(parent);
        try {
            FileOutputStream file = context.openFileOutput(LockSettings.LOCK_FILE, MODE_PRIVATE);
            file.write((jsonObject.toString()).getBytes());
            file.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }


    public static void setPassword(Context context, String password) {
        Map<String, Object> parent = new HashMap<>();
        Map<String, String> child = new HashMap<>();
        child.put(PasswordLock.PASSWORD, password);
        parent.put(LockData.KEYS, child);


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
            jsonObject = new JSONObject(jsonObject.getJSONObject(LockData.KEYS).toString());

            try {
                Log.d("TAG", "setPin: " + jsonObject);
                child.put(PatternLock.PATTERN, jsonObject.getString(PatternLock.PATTERN));
            } catch (JSONException ignored) {
            }
            try {
                child.put(PinLock.PIN, jsonObject.getString(PinLock.PIN));
            } catch (JSONException ignored) {
            }


        } catch (FileNotFoundException ignored) {
            new File(LockSettings.LOCK_FILE);
        } catch (IOException | JSONException e) {
            //throw new RuntimeException(e);
        }


        JSONObject jsonObject = new JSONObject(parent);
        try {
            FileOutputStream file = context.openFileOutput(LockSettings.LOCK_FILE, MODE_PRIVATE);
            file.write((jsonObject.toString()).getBytes());
            file.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }


}
