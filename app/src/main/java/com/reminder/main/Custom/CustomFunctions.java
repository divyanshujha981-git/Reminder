package com.reminder.main.Custom;

import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;
import com.reminder.main.R;

import java.util.Calendar;
import java.util.Objects;
import java.util.UUID;

public class CustomFunctions {
    public static int dpToPx(Context context, int dp) {

        // Get the screen's density scale
        float density = context.getResources().getDisplayMetrics().density;

        // Add 0.5f for proper rounding to the nearest integer
        return (int) ((dp * density) + 0.5f);

    }


    /**
     * @return {@link String} in the format [{@link Calendar#getTimeInMillis()}]+[-]+[{@link UUID#randomUUID()}]
     * */
    public static String generateNewTaskID(Context context) {
        return context.getString(
                R.string.generate_task_id,
                Calendar.getInstance().getTimeInMillis()+"",
                UUID.randomUUID().toString().substring(0, 8)
        );
    }




    /**
     * @param taskID pass pre-stored [TASK_ID]
     * @return {@link String} in the format [TASK_ID]+[-]+[FirebaseUID]
     * */
    private static String generateNewTaskWebID(Context context, String taskID) {
        return context.getString(
                R.string.generate_task_web_id,
                taskID,
                FirebaseAuth.getInstance().getUid()
        );
    }


    public static String getTaskWebID(Context context, String taskID, String taskWebID) {

        if (taskWebID == null || taskID.isEmpty()) return generateNewTaskWebID(context, taskID);
        else return taskWebID;

    }


    public static String getTaskIdFromTaskWebId(String taskWebId) {
        return taskWebId.replace("-"+taskWebId.split("-")[2], "");
    }





}
