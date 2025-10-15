package com.reminder.main.Other;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

public class AlertDialogueForAll {

    private final AlertDialog.Builder builder;
    public final AlertCallBack alertCallBack;

    public interface AlertCallBack {
        void alertCallBack(boolean value);
    }




    public AlertDialogueForAll(@NonNull Context context, AlertCallBack alertCallBack) {
        this.builder= new AlertDialog.Builder(context);
        this.alertCallBack = alertCallBack;
    }

    public AlertDialogueForAll(@NonNull Context context) {
        this.builder= new AlertDialog.Builder(context);
        this.alertCallBack = null;
    }




    public void showAlert(String title, String message, String positiveButtonText, String negativeButtonText) {

        if (title != null) builder.setTitle(title);
        if (message != null) builder.setMessage(message);

        builder.setPositiveButton(positiveButtonText, (dialog, which) -> {
            if (alertCallBack != null) alertCallBack.alertCallBack(true);
        });

        if (negativeButtonText != null) {
            builder.setNegativeButton(negativeButtonText, (dialog, which) -> {
                if (alertCallBack != null) alertCallBack.alertCallBack(false);
            });
        }

        AlertDialog alertDialog = builder.create();
        alertDialog.show();


    }

    public void showAlert(String title, String message, String positiveButtonText) {

        if (title != null) builder.setTitle(title);
        if (message != null) builder.setMessage(message);

        builder.setPositiveButton(positiveButtonText, (dialog, which) -> {
            if (alertCallBack != null) alertCallBack.alertCallBack(true);
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();


    }

    public void showAlert(String message, String positiveButtonText) {

        if (message != null) builder.setMessage(message);

        builder.setPositiveButton(positiveButtonText, (dialog, which) -> {
            if (alertCallBack != null) alertCallBack.alertCallBack(true);
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();


    }



}
