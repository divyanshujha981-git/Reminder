package com.reminder.main.UserInterfaces.UserTaskInbox.MainActivity;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.reminder.main.Custom.CustomInterfaces;
import com.reminder.main.UserInterfaces.UserTaskInbox.TaskReceived.TaskReceived;
import com.reminder.main.UserInterfaces.UserTaskInbox.TaskSent.TaskSent;

import java.util.ArrayList;

public class UserTaskInboxPagerAdapter extends FragmentStateAdapter
    implements
        CustomInterfaces.RefreshLayout,
        CustomInterfaces.TaskInboxInterface {

    private final TaskSent taskSent = new TaskSent();
    private final TaskReceived taskReceived = new TaskReceived();
    private final UserTaskInboxSQLData userTaskInboxSQLData;

    public UserTaskInboxPagerAdapter(@NonNull FragmentActivity fragmentActivity, String userPrimaryId) {
        super(fragmentActivity);
        userTaskInboxSQLData = new UserTaskInboxSQLData(fragmentActivity.getApplicationContext(), userPrimaryId, this);
        userTaskInboxSQLData.getTaskInboxData();
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return position == 0 ? taskSent : taskReceived;
    }

    @Override
    public int getItemCount() {
        return 2;
    }

    @Override
    public void refreshLayout(Class<?> cls) {
        userTaskInboxSQLData.getTaskInboxData();
    }

    @Override
    public void setTaskSentList(ArrayList<UserTaskInboxData> taskSentList) {
        taskSent.setTaskSentToClass(taskSentList);
    }

    @Override
    public void setTaskReceivedList(ArrayList<UserTaskInboxData> taskReceivedList) {
        taskReceived.setTaskReceivedToClass(taskReceivedList);
    }


}
