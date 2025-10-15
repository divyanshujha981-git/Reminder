package com.reminder.main.UserInterfaces.SettingsPage.TaskInboxSettings;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.reminder.main.Firebase.FirebaseConstants.UPDATE_STATUS_FOR_RECEIVE_TASK;
import static com.reminder.main.Firebase.FirebaseConstants.UPDATE_STATUS_FOR_TASK_AUTO_DOWNLOAD;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreference;

import com.google.firebase.functions.FirebaseFunctions;
import com.reminder.main.Firebase.FirebaseConstants;
import com.reminder.main.R;
import com.reminder.main.UserInterfaces.HomePage.MainActivity.MainActivity;

import java.util.Map;


public class TaskInboxSettingsPreferenceFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceClickListener  {
    private CoordinatorLayout CIRCULAR_LOADING;
    private final FirebaseFunctions FIREBASE_FUNCTION = MainActivity.FIREBASE_FUNCTIONS;
    private SwitchPreference taskAutoDownload, receiveTask;
    private String taskAutoDownloadString, receiveTaskString;
    private boolean previousAutoDownloadStatus, previousReceiveTaskStatus;

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        setPreferencesFromResource(R.xml.task_inbox_settings, rootKey);

        declare();
        addActions();

    }



    private void declare() {

        CIRCULAR_LOADING = requireActivity().findViewById(R.id.circular_progress_middle_view);

        taskAutoDownloadString = getString(R.string.taskAutoDownload);
        receiveTaskString = getString(R.string.receiveTask);

        previousAutoDownloadStatus = PreferenceManager.getDefaultSharedPreferences(requireContext()).getBoolean(taskAutoDownloadString, true);
        previousReceiveTaskStatus = PreferenceManager.getDefaultSharedPreferences(requireContext()).getBoolean(receiveTaskString, true);

        taskAutoDownload = findPreference(taskAutoDownloadString);
        receiveTask = findPreference(receiveTaskString);

    }



    private void addActions() {

        taskAutoDownload.setOnPreferenceClickListener(this);
        receiveTask.setOnPreferenceClickListener(this);

        taskAutoDownload.setChecked(previousAutoDownloadStatus);
        receiveTask.setChecked(previousReceiveTaskStatus);

    }

    @Override
    public boolean onPreferenceClick(@NonNull Preference preference) {

        String key = preference.getKey();

        if (key.equals(taskAutoDownloadString)) {
            updateTaskAutoDownloadStatus();
        }
        else if (key.equals(receiveTaskString)){
            updateReceiveTaskStatus();
        }

        return true;
    }






    private void updateReceiveTaskStatus() {

        CIRCULAR_LOADING.setVisibility(VISIBLE);

        FIREBASE_FUNCTION.getHttpsCallable(UPDATE_STATUS_FOR_RECEIVE_TASK)
                .call()
                .addOnSuccessListener(httpsCallableResult -> {
                    Object data = httpsCallableResult.getData();
                    Map<?, ?> map = (Map<?, ?>) data;
                    Log.d("TAG", "onComplete: " + data);
                    if (map != null) {
                        boolean status = Boolean.parseBoolean(String.valueOf(map.get(FirebaseConstants.RECEIVE_TASK)));
                        receiveTask.setChecked(status);
                        SharedPreferences sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean(receiveTaskString, status);
                        editor.apply();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                    receiveTask.setChecked(previousReceiveTaskStatus);

                    SharedPreferences sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean(receiveTaskString, previousReceiveTaskStatus);
                    editor.apply();
                })
                .addOnCompleteListener(task -> {

                    CIRCULAR_LOADING.setVisibility(GONE);
                    receiveTask.setEnabled(true);

                });
    }







    private void updateTaskAutoDownloadStatus() {

        CIRCULAR_LOADING.setVisibility(VISIBLE);

        FIREBASE_FUNCTION.getHttpsCallable(UPDATE_STATUS_FOR_TASK_AUTO_DOWNLOAD)
                .call()
                .addOnSuccessListener(httpsCallableResult -> {
                    Object data = httpsCallableResult.getData();
                    Map<?, ?> map = (Map<?, ?>) data;
                    Log.d("TAG", "onComplete: " + data);
                    if (map != null) {
                        boolean status = Boolean.parseBoolean(String.valueOf(map.get(FirebaseConstants.TASK_AUTO_DOWNLOAD)));
                        taskAutoDownload.setChecked(status);
                        SharedPreferences sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean(taskAutoDownloadString, status);
                        editor.apply();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                    taskAutoDownload.setChecked(previousAutoDownloadStatus);

                    SharedPreferences sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean(taskAutoDownloadString, previousAutoDownloadStatus);
                    editor.apply();
                })
                .addOnCompleteListener(task -> {

                    CIRCULAR_LOADING.setVisibility(GONE);
                    taskAutoDownload.setEnabled(true);

                });
    }








}
