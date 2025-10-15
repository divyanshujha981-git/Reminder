package com.reminder.main.UserInterfaces.SettingsPage.AccountSettings;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.reminder.main.Firebase.FirebaseConstants.UPDATE_ACCOUNT_PRIVACY;
import static com.reminder.main.Firebase.FirebaseConstants.UPDATE_STATUS_FOR_RECEIVE_REQUEST;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.reminder.main.UserInterfaces.SettingsPage.AccountSettings.BlockedContacts.BlockedContacts;

import java.util.Map;
import java.util.Objects;


public class AccountSettingsPreferenceFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceClickListener  {
    private CoordinatorLayout CIRCULAR_LOADING;

    private final FirebaseFunctions FIREBASE_FUNCTIONS = MainActivity.FIREBASE_FUNCTIONS;

    private String
            accountPrivateString,
            logoutString,
            blockedContactString,
            receiveRequestString;
            //receiveJobRequestString,
            //receiveEmployeeRequestString;




    private boolean
            previousPrivateValue,
            previousReceiveRequestValue;
            //previousReceiveJobRequestValue,
            //previousReceiveEmployeeRequestValue;

    private SwitchPreference accountPrivate, receiveRequest;//receiveJobRequest, receiveEmployeeRequest;
    private Preference logOut, blockedContacts;



    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        setPreferencesFromResource(R.xml.account_settings, rootKey);

        assignVariables();

        setMethodActions();

    }



    private void assignVariables() {

        CIRCULAR_LOADING = requireActivity().findViewById(R.id.circular_progress_middle_view);

        accountPrivateString = getString(R.string.isAccountPrivate);
        logoutString = getString(R.string.logout);
        blockedContactString = getString(R.string.blockedAccount);
        receiveRequestString = getString(R.string.receiveRequest);
        //receiveJobRequestString = getString(R.string.receiveJobRequest);
        //receiveEmployeeRequestString = getString(R.string.receiveEmployeeRequest);

        previousPrivateValue = PreferenceManager.getDefaultSharedPreferences(requireContext()).getBoolean(accountPrivateString, false);
        previousReceiveRequestValue = PreferenceManager.getDefaultSharedPreferences(requireContext()).getBoolean(receiveRequestString, true);
        //previousReceiveJobRequestValue = PreferenceManager.getDefaultSharedPreferences(requireContext()).getBoolean(receiveJobRequestString, true);
        //previousReceiveEmployeeRequestValue = PreferenceManager.getDefaultSharedPreferences(requireContext()).getBoolean(receiveEmployeeRequestString, true);

        accountPrivate =  findPreference(accountPrivateString);
        receiveRequest =  findPreference(receiveRequestString);
        //receiveJobRequest =  findPreference(receiveJobRequestString);
        //receiveEmployeeRequest =  findPreference(receiveEmployeeRequestString);
        logOut = findPreference(logoutString);
        blockedContacts = findPreference(blockedContactString);

    }




    private void setMethodActions() {

        accountPrivate.setOnPreferenceClickListener(this);
        receiveRequest.setOnPreferenceClickListener(this);
        //receiveJobRequest.setOnPreferenceClickListener(this);
        //receiveEmployeeRequest.setOnPreferenceClickListener(this);
        logOut.setOnPreferenceClickListener(this);
        blockedContacts.setOnPreferenceClickListener(this);

        accountPrivate.setChecked(previousPrivateValue);
        receiveRequest.setChecked(previousReceiveRequestValue);
        //receiveJobRequest.setChecked(previousReceiveJobRequestValue);
        //receiveEmployeeRequest.setChecked(previousReceiveEmployeeRequestValue);

    }



    @Override
    public boolean onPreferenceClick(@NonNull Preference preference) {
        String key = preference.getKey();

        if (Objects.equals(key, logoutString)) {
            signOut();
        }

        else if (Objects.equals(key, blockedContactString)) {
            startActivity(new Intent(requireContext(), BlockedContacts.class));
        }

        else if (Objects.equals(key, accountPrivateString)) {
            updateAccountPrivate();
        }
        else if (Objects.equals(key, receiveRequestString)) {
            updateReceiveRequestStatus();
        }

//        else if (Objects.equals(key, receiveJobRequestString)) {
//            updateReceiveJobRequestStatus();
//        }
//
//        else if (Objects.equals(key, receiveEmployeeRequestString)) {
//            updateReceiveEmployeeRequestStatus();
//        }
        return true;
    }





    private void signOut() {

        CIRCULAR_LOADING.setVisibility(VISIBLE);

        AccountSettings.signOut(requireContext(), isSignedOut -> {
            if (isSignedOut) requireActivity().finish();
            else {
                CIRCULAR_LOADING.setVisibility(GONE);
                Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });


    }



    private void updateAccountPrivate() {
        CIRCULAR_LOADING.setVisibility(VISIBLE);

        FIREBASE_FUNCTIONS.getHttpsCallable(UPDATE_ACCOUNT_PRIVACY)
                .call()
                .addOnSuccessListener(httpsCallableResult -> {
                    Object data = httpsCallableResult.getData();
                    Log.d("TAG", "onComplete: " + httpsCallableResult.getData());
                    Map<?, ?> map = (Map<?, ?>) data;
                    if (map != null) {
                        boolean status = Boolean.parseBoolean(String.valueOf(map.get(FirebaseConstants.IS_ACCOUNT_PRIVATE)));
                        accountPrivate.setChecked(status);
                        SharedPreferences sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean(accountPrivateString, status);
                        editor.apply();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("TAG", "updateAccountPrivate: ", e);
                    Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                    accountPrivate.setChecked(previousPrivateValue);

                    SharedPreferences sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean(accountPrivateString, previousPrivateValue);
                    editor.apply();
                })
                .addOnCompleteListener(task -> {
                    CIRCULAR_LOADING.setVisibility(View.GONE);
                    accountPrivate.setEnabled(true);
                });
    }




    /*
    private void updateReceiveJobRequestStatus() {

        CIRCULAR_LOADING.setVisibility(VISIBLE);

        FIREBASE_FUNCTIONS.getHttpsCallable(FirebaseConstants.UPDATE_STATUS_FOR_RECEIVE_REQUEST_TO_SEND_TASK)
                .call()
                .addOnSuccessListener(httpsCallableResult -> {

                    Object data = httpsCallableResult.getData();
                    Map<?, ?> map1 = (Map<?, ?>) data;
                    Log.d("TAG", "onComplete: " + data);
                    if (map1 != null) {
                        boolean status = Boolean.parseBoolean(String.valueOf(map1.get(FirebaseConstants.RECEIVE_EMPLOYEE_REQUEST)));
                        receiveJobRequest.setChecked(status);
                        SharedPreferences sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean(receiveJobRequestString, status);
                        editor.apply();
                    }

                })
                .addOnFailureListener(e -> {

                    Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                    receiveJobRequest.setChecked(previousReceiveJobRequestValue);

                    SharedPreferences sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean(receiveJobRequestString, previousReceiveJobRequestValue);
                    editor.apply();

                })
                .addOnCompleteListener(task -> {

                    CIRCULAR_LOADING.setVisibility(View.GONE);
                    receiveJobRequest.setEnabled(true);

                });


    }





    private void updateReceiveEmployeeRequestStatus() {

        CIRCULAR_LOADING.setVisibility(VISIBLE);



        FIREBASE_FUNCTIONS.getHttpsCallable(UPDATE_STATUS_FOR_RECEIVE_REQUEST_TO_RECEIVE_TASK)
                .call()
                .addOnSuccessListener(httpsCallableResult -> {

                    Object data = httpsCallableResult.getData();
                    Map<?, ?> map1 = (Map<?, ?>) data;
                    Log.d("TAG", "onComplete: " + data);
                    if (map1 != null) {
                        boolean status = Boolean.parseBoolean(String.valueOf(map1.get(FirebaseConstants.RECEIVE_JOB_REQUEST)));
                        receiveEmployeeRequest.setChecked(status);
                        SharedPreferences sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean(receiveEmployeeRequestString, status);
                        editor.apply();
                    }
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                    receiveEmployeeRequest.setChecked(previousReceiveEmployeeRequestValue);

                    SharedPreferences sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean(receiveEmployeeRequestString, previousReceiveEmployeeRequestValue);
                    editor.apply();

                })
                .addOnCompleteListener(task -> {

                    CIRCULAR_LOADING.setVisibility(View.GONE);
                    receiveEmployeeRequest.setEnabled(true);

                });


    }
    */




    private void updateReceiveRequestStatus() {

        CIRCULAR_LOADING.setVisibility(VISIBLE);


        FIREBASE_FUNCTIONS.getHttpsCallable(UPDATE_STATUS_FOR_RECEIVE_REQUEST)
                .call()
                .addOnSuccessListener(httpsCallableResult -> {

                    Object data = httpsCallableResult.getData();
                    Map<?, ?> map1 = (Map<?, ?>) data;
                    Log.d("TAG", "onComplete: " + data);
                    if (map1 != null) {
                    boolean status = Boolean.parseBoolean(String.valueOf(map1.get(FirebaseConstants.RECEIVE_REQUEST)));
                        receiveRequest.setChecked(status);
                        SharedPreferences sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean(receiveRequestString, status);
                        editor.apply();
                    }
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                    receiveRequest.setChecked(previousReceiveRequestValue);

                    SharedPreferences sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean(receiveRequestString, previousReceiveRequestValue);
                    editor.apply();

                })
                .addOnCompleteListener(task -> {

                    CIRCULAR_LOADING.setVisibility(View.GONE);
                    receiveRequest.setEnabled(true);

                });


    }




}
