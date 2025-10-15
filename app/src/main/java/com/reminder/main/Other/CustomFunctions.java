package com.reminder.main.Other;

import android.content.Context;

public class CustomFunctions {
    public static int dpToPx(Context context, int dp) {
        // Get the screen's density scale
        float density = context.getResources().getDisplayMetrics().density;

        // Add 0.5f for proper rounding to the nearest integer
        return (int) ((dp * density) + 0.5f);
    }


}
