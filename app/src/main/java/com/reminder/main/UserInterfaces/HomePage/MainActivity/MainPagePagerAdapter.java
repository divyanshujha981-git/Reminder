package com.reminder.main.UserInterfaces.HomePage.MainActivity;



import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.google.firebase.auth.FirebaseAuth;
import com.reminder.main.UserInterfaces.HomePage.Account.AccountPage;
import com.reminder.main.UserInterfaces.HomePage.TaskInbox.TaskInbox;
import com.reminder.main.UserInterfaces.HomePage.Tasks.Tasks;
import com.reminder.main.UserInterfaces.LoginRegisterButton.LoginRegisterButton;


public class MainPagePagerAdapter extends FragmentStateAdapter {
    private final Tasks taskEventPage = new Tasks();
    private final TaskInbox taskInbox = new TaskInbox();
    private final AccountPage accountPage = new AccountPage();
    private final LoginRegisterButton loginRegisterButton = new LoginRegisterButton();
    private final boolean setPage;



    public MainPagePagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
        this.setPage = FirebaseAuth.getInstance().getCurrentUser() != null;
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
                    return taskEventPage;
            }
        }
        else {
            if (position == 1) {
                return loginRegisterButton;
            }
            return taskEventPage;

        }


    }







    @Override
    public int getItemCount() {
        return setPage ? 3 : 2;
    }





}
