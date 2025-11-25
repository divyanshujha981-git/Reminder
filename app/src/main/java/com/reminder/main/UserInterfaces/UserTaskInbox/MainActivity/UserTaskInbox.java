package com.reminder.main.UserInterfaces.UserTaskInbox.MainActivity;

import static com.reminder.main.SqLite.Tasks.TaskConstants.USER_PRIMARY_ID;
import static com.reminder.main.SqLite.UserDetails.UserDetailsConstant.USER_NAME;
import static com.reminder.main.SqLite.UserDetails.UserDetailsConstant.USER_PROFILE_PIC;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.reminder.main.R;
import com.reminder.main.SqLite.UserDetails.UserDetailsData;
import com.reminder.main.UserInterfaces.SendTaskPage.SendTask;

public class UserTaskInbox extends AppCompatActivity {

    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private final UserDetailsData userData = new UserDetailsData();
    private TextView setUserName;
    private ImageView setUserProfilePic;
    private FloatingActionButton sendTaskButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_task_inbox);

        declare();


    }


    private void declare() {

        setUserName = findViewById(R.id.setName);
        setUserProfilePic = findViewById(R.id.setProfilePic);

        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.default_tab_layout);

        userData.setName(getIntent().getStringExtra(USER_NAME));
        userData.setProfilePic(getIntent().getStringExtra(USER_PROFILE_PIC));
        userData.setUserPrimaryId(getIntent().getStringExtra(USER_PRIMARY_ID));

        Log.d("TAG", "declare: " + userData.getName());

        sendTaskButton = findViewById(R.id.sendTaskButton);

    }


    @Override
    protected void onStart() {
        super.onStart();
        setActions();
        setDataToUI();
    }


    private void setActions() {

        findViewById(R.id.backButton).setOnClickListener(view -> finish());

        viewPager.setAdapter(new UserTaskInboxPagerAdapter(this, userData.getUserPrimaryId()));
        new TabLayoutMediator(tabLayout, viewPager, (tab, i) -> tab.setText(getString(i == 0 ? R.string.sent : R.string.received))).attach();

        sendTaskButton.setOnClickListener(view -> {
            Intent intent = new Intent(UserTaskInbox.this, SendTask.class);
            intent.putExtra(USER_NAME, userData.getName());
            intent.putExtra(USER_PROFILE_PIC, userData.getProfilePic());
            intent.putExtra(USER_PRIMARY_ID, userData.getUserPrimaryId());

            startActivity(intent);
        });

    }


    private void setDataToUI() {

        Log.d("TAG", "setDataToUI: " + getIntent().getStringExtra(USER_NAME));

        setUserName.setText(userData.getName());

        if (userData.getProfilePic() != null && !userData.getProfilePic().isEmpty()) {
            Glide.with(this)
                    .load(userData.getProfilePic())
                    .into(setUserProfilePic);
        }

    }


}
