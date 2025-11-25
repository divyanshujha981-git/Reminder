package com.reminder.main.Custom;

import androidx.fragment.app.FragmentManager;

import com.google.android.material.timepicker.MaterialTimePicker;

import java.util.Calendar;

public class CustomTimePicker {
    private int[] time = null;
    private final FragmentManager fragmentManager;

    public CustomTimePicker(FragmentManager fragmentManager, CustomInterfaces.DateTime dateTime) {
        this.fragmentManager = fragmentManager;

        materialTimePicker.addOnPositiveButtonClickListener(v -> {
            time = new int[]{materialTimePicker.getHour(), materialTimePicker.getMinute()};
            dateTime.setTime(time, (byte) (materialTimePicker.getHour() > 11 ? Calendar.PM : Calendar.AM));
        });
    }

    MaterialTimePicker materialTimePicker = new MaterialTimePicker.Builder()
            .setHour(Calendar.getInstance().get(Calendar.HOUR_OF_DAY))
            .setMinute(Calendar.getInstance().get(Calendar.MINUTE) + 5)
            .build();

    public void timeFunction() {
        materialTimePicker.show(fragmentManager, "fragmentManager");
    }


}
