package com.reminder.main.Custom;

import android.content.Context;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.reminder.main.R;

import java.util.Calendar;
import java.util.Random;
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
                Calendar.getInstance().getTimeInMillis()+ "",
                UUID.randomUUID().toString().substring(0, 8).replace("-", "X").replace("_", "X")
        );
    }


    private static String getRandomChars(String input) {
        Random random = new Random();
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < 8; i++) {
            int index = random.nextInt(input.length());
            result.append(input.charAt(index));
        }

        return result.toString();
    }


    /**
     * @param taskID pass pre-stored [TASK_ID]
     * @return {@link String} in the format [CHAR]+[-]+[CHAR]+[-]+[CHAR]
     * */
    private static String generateNewTaskWebID(Context context, String taskID) {

        return

                context.getString(
                        R.string.generate_task_web_id,
                        getRandomChars(taskID.replace("-","")).replace("-", "X").replace("_", "X"),
                        getRandomChars(UUID.randomUUID().toString().substring(0, 15)).replace("-", "X").replace("_", "X"),
                        getRandomChars(FirebaseAuth.getInstance().getUid()).replace("-", "X").replace("_", "X")
                );


    }


    public static String getTaskWebID(Context context, String taskID, String taskWebID) {
        if (taskWebID == null || taskID.isEmpty()) return generateNewTaskWebID(context, taskID);
        else return taskWebID;
    }


    public static String getTaskWebID(Context context, String taskID) {
         return generateNewTaskWebID(context, taskID);
    }


    public static int getIdFromTaskID(String id) {

        String digits = id.split("_")[0];
        Log.d("TAG", "getNumberOfID: " + digits);
        long l = Long.parseLong(digits);
        return (int) l;
    }



}
