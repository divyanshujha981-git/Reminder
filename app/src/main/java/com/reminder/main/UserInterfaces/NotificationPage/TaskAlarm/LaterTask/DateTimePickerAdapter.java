package com.reminder.main.UserInterfaces.NotificationPage.TaskAlarm.LaterTask;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class DateTimePickerAdapter extends FragmentStateAdapter {
    private final DatePickerA datePickerA;
    private final TimePickerA timePickerA;

    public DateTimePickerAdapter(FragmentActivity fragmentActivity) {
        super(fragmentActivity);
        this.datePickerA = new DatePickerA();
        this.timePickerA = new TimePickerA();
    }

    @NonNull
    public Fragment createFragment(int position) {
        return position == 1 ? timePickerA : datePickerA;
    }

    public int getItemCount() {
        return 2;
    }
}
