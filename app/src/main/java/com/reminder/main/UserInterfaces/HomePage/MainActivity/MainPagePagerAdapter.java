package com.reminder.main.UserInterfaces.HomePage.MainActivity;



import static com.reminder.main.UserInterfaces.HomePage.MainActivity.MainActivity.FIREBASE_AUTH;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.google.firebase.auth.FirebaseAuth;
import com.reminder.main.Custom.CustomInterfaces;
import com.reminder.main.SqLite.Tasks.TaskData;
import com.reminder.main.SqLite.UserDetails.UserDetailsData;
import com.reminder.main.UserInterfaces.HomePage.Account.AccountPage;
import com.reminder.main.UserInterfaces.HomePage.TaskInbox.TaskInbox;
import com.reminder.main.UserInterfaces.HomePage.Tasks.NavBarDateTemplate;
import com.reminder.main.UserInterfaces.HomePage.Tasks.Tasks;
import com.reminder.main.UserInterfaces.HomePage.Tasks.TasksAdapter;
import com.reminder.main.UserInterfaces.LoginRegisterButton.LoginRegisterButton;

import java.util.ArrayList;
import java.util.Map;



public class MainPagePagerAdapter extends FragmentStateAdapter implements
        CustomInterfaces.TaskSQLInterface,
        CustomInterfaces.TaskInboxPeopleInterface,
        CustomInterfaces.AccountInterface,
        CustomInterfaces.RefreshLayout {
    private final Tasks task = new Tasks(this);
    private final TaskInbox taskInbox = new TaskInbox(this);
    private final AccountPage accountPage = new AccountPage();
    public final HomePageSqlData homePageSqlData;
    private final LoginRegisterButton loginRegisterButton = new LoginRegisterButton();
    private final boolean setPage;



    public MainPagePagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
        this.setPage = FirebaseAuth.getInstance().getCurrentUser() != null;
        homePageSqlData = new HomePageSqlData(fragmentActivity.getApplicationContext(), this);
        homePageSqlData.getAllData(setPage);
    }







    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (setPage) {
            switch (position) {
                case 1:
                    return taskInbox;
                case 2:
                    return accountPage;
                default:
                    return task;
            }
        }
        else {
            if (position == 1) {
                return loginRegisterButton;
            }
            return task;

        }


    }



    @Override
    public int getItemCount() {
        return setPage ? 3 : 2;
    }


    @Override
    public void getAccountData(Map<String, Object> accountData) {
        accountPage.setAccountData(accountData);
    }


    @Override
    public void getTaskInboxPeopleData(ArrayList<UserDetailsData> taskInboxPeopleData) {
        taskInbox.setTaskInboxDataToClass(taskInboxPeopleData);
    }


    @Override
    public void setMainTaskData(ArrayList<TaskData> taskData) {
        Log.d("TAG", "setMainTaskData: ");
        task.setMainTaskDataToClass(taskData);
    }


    @Override
    public void setUpComingTask(TaskData tasks, int position) {
        task.setUpcomingTaskToClass(tasks, position);
    }


    @Override
    public void setNavDateTask(ArrayList<NavBarDateTemplate> navDateArray) {
        Log.d("TAG", "setNavDateTask: ");
        task.setNavDateTaskToClass(navDateArray);
    }


    @Override
    public void setFilteredTask(ArrayList<ArrayList<TaskData>> navDateArray) {
        task.setFilteredTaskToClass(navDateArray);
    }


    @Override
    public void setPinnedTaskOnTop(boolean pinTaskOnTop, boolean pinnedTaskAvailable) {
        task.setPinningDataToClass(pinTaskOnTop, pinnedTaskAvailable);
    }



    @Override
    public void refreshLayout(Class<?> cls) {

        if (cls == TasksAdapter.class) {
            homePageSqlData.getTaskData();
        }
        else {
            homePageSqlData.getAllData(FIREBASE_AUTH.getCurrentUser() != null);
        }

    }



}
