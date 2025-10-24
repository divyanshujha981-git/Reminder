package com.reminder.main.UserInterfaces.HomePage.MainActivity;


import static com.reminder.main.UserInterfaces.Ppp.Ppp.FOR_PAGE;
import static com.reminder.main.UserInterfaces.Ppp.Ppp.LOCKED_PAGE;
import static com.reminder.main.UserInterfaces.Ppp.Ppp.SETTINGS_PAGE;

import android.Manifest;
import android.app.AlarmManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.preference.PreferenceManager;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.functions.FirebaseFunctions;
import com.reminder.main.BackgroundWorks.CheckNotificationService;
import com.reminder.main.Other.ApplicationCustomInterfaces;
import com.reminder.main.R;
import com.reminder.main.SqLite.Tasks.TaskConstants;
import com.reminder.main.SqLite.Tasks.TaskData;
import com.reminder.main.UserInterfaces.AboutPage.AboutPage;
import com.reminder.main.UserInterfaces.AddTaskPage.AddTask;
import com.reminder.main.UserInterfaces.PeoplePage.MainActivity.People;
import com.reminder.main.UserInterfaces.Ppp.Ppp;
import com.reminder.main.UserInterfaces.PrivatePage.PrivatePages;
import com.reminder.main.UserInterfaces.SettingsPage.AccountSettings.EditAccountInfo;
import com.reminder.main.UserInterfaces.SettingsPage.AllSettings.SettingsPage;


public class MainActivity extends AppCompatActivity implements
        ApplicationCustomInterfaces.NestedScroll,
        NavigationBarView.OnItemSelectedListener,
        ApplicationCustomInterfaces.BottomNavItemCheck,
        ApplicationCustomInterfaces.ContextualActionBar {


    private ViewPager2 viewPager2;
    private ActionMode actionMode;
    private boolean pinTaskOnTop;
    private NavigationBarView bottomNavigationView;
    public static FirebaseAuth FIREBASE_AUTH = FirebaseAuth.getInstance();
    public static FirebaseFunctions FIREBASE_FUNCTIONS = FirebaseFunctions.getInstance();
    public static FirebaseDatabase FIREBASE_DATABASE = FirebaseDatabase.getInstance();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        declare();

        setAction();

    }


    private void declare() {
        viewPager2 = findViewById(R.id.frameLayout);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
    }

    private void setAction() {

        viewPager2.setAdapter(new MainPagePagerAdapter(this));
        bottomNavigationView.setOnItemSelectedListener(MainActivity.this);

        findViewById(R.id.add_task_button).setOnClickListener(v -> startActivity(new Intent(this, AddTask.class)));

        Intent intent = new Intent("com.reminder.mini.APP_STARTED");
        intent.setComponent(new ComponentName("com.reminder.mini", "com.reminder.mini.BackgroundWorks.TaskWork.SystemRescheduleTaskAfterAlarmTrigger"));
        sendBroadcast(intent);

        //checkPermissions();

        uiCorrection();

    }

    @Override
    protected void onStart() {
        super.onStart();

        pinTaskOnTop = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("pinTaskOnTop", true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setFirebase();
        viewPager2.setAdapter(new MainPagePagerAdapter(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_file, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menuSettings) {
            if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean(getString(R.string.get_settings_lock_state), true)) {
                startActivity(new Intent(this, Ppp.class).putExtra(FOR_PAGE, SETTINGS_PAGE));
                //startActivity(new Intent(this, SettingsPage.class));
            } else {
                startActivity(new Intent(this, SettingsPage.class));
            }
        }
        else if (id == R.id.menuPeople) {
            startActivity(new Intent(this, People.class));
        }
        else if (id == R.id.menuPrivate) {
            if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean(getString(R.string.get_private_lock_and_message_lock_state), true)) {
                startActivity(new Intent(this, Ppp.class).putExtra(FOR_PAGE, LOCKED_PAGE));
            } else {
                startActivity(new Intent(this, PrivatePages.class));
            }
        }
        else if (id == R.id.menuAbout) {
            startActivity(new Intent(this, AboutPage.class));
        }


        return true;
    }


    private void setFirebase() {

        FIREBASE_AUTH = FirebaseAuth.getInstance();
        FIREBASE_FUNCTIONS = FirebaseFunctions.getInstance();
        FirebaseDatabase FIREBASE_DATABASE = FirebaseDatabase.getInstance();

        try {
            FIREBASE_AUTH.useEmulator("10.0.2.2", 9099);
        }
        catch (IllegalStateException e) {
            Log.e("TAG", "setFirebase: ** FIREBASE_AUTH **", e);
        }

        try {
            FIREBASE_FUNCTIONS.useEmulator("10.0.2.2", 5001);
        }
        catch (IllegalStateException e) {
            Log.e("TAG", "setFirebase: ** FIREBASE_FUNCTIONS **", e);
        }

        try {
            FIREBASE_DATABASE.useEmulator("10.0.2.2", 9000);
        }
        catch (IllegalStateException e) {
            Log.e("TAG", "setFirebase: ** FIREBASE_DATABASE **", e);
        }

        if (FIREBASE_AUTH.getCurrentUser() != null && !userFoundInSQLite(this)) {
            startActivity(new Intent(this, EditAccountInfo.class));
        }

    }



    public static boolean userFoundInSQLite(Context context) {

        boolean foundInFirebase = FIREBASE_AUTH.getCurrentUser() != null;
        boolean foundInSQLite = PreferenceManager.getDefaultSharedPreferences(context).getBoolean(context.getString(R.string.user_data_found_in_sqlite), false);

        if (foundInFirebase) {
            return foundInSQLite;
        }

        return false;


    }


    private void checkPermissions() {

        if (Build.VERSION.SDK_INT >= 33) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_DENIED) {

                ActivityResultLauncher<String> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                    if (!isGranted) {
                        finish();
                    }
                });

                activityResultLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);

            }
        }

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        String packageName = getPackageName();

        if (!pm.isIgnoringBatteryOptimizations(packageName)) {
            Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + packageName));
            startActivity(intent);
        }

        startService(new Intent(this, CheckNotificationService.class));

        if (!Settings.canDrawOverlays(this)) {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivity(intent);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (!alarmManager.canScheduleExactAlarms()) {
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivity(intent);
            }
        }

    }


    @Override
    public void scrollIntercept(boolean con) {
        viewPager2.setUserInputEnabled(con);
    }


    public static void redirectToPage(TaskData template, Context context, Class<?> page) {
        Intent intent = new Intent(context, page);
        if (template != null) {
            intent.putExtra(TaskConstants.TOPIC, template.getTopic());
            intent.putExtra(TaskConstants.DESCRIPTION, template.getDescription());
            intent.putExtra(TaskConstants.REPEAT_STATUS, String.valueOf(template.getRepeatStatus()));
            intent.putExtra(TaskConstants.REPEATING_ALARM_DATE, template.getRepeatingAlarmDate());
            intent.putExtra(TaskConstants.DATE_ARRAY, String.valueOf(template.getDateArray()));
            intent.putExtra(TaskConstants.ALREADY_DONE, template.getAlreadyDone());
            intent.putExtra(TaskConstants.TASK_ID, template.getTaskId());
        }
        context.startActivity(intent);
    }


    public static void expandHideView(View button, View viewToHideOrShow) {

        button.animate()
                .rotationX(button.getRotationX() == 180f ? 0f : 180f)
                .setDuration(300)
                .start();

        viewToHideOrShow.setVisibility(viewToHideOrShow.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);

    }


    private void uiCorrection() {
        bottomNavigationView.setOnApplyWindowInsetsListener(null);
        bottomNavigationView.setPadding(0,0,0,0);

        findViewById(R.id.bottomAppBar).setBackgroundColor(getColor(R.color.windowBackground));

        ((BottomAppBar) findViewById(R.id.bottomAppBar)).setElevation(5f);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.bottomAppBar), (v, insets) -> {
            // Apply only bottom inset (system navigation bar)

            v.setPadding(
                    0,
                    0,
                    0,
                    0
            );

            return insets;

        });


        //MaterialToolbar appBarLayout = findViewById(R.id.toolbarLayout);
//
        //appBarLayout.setBackgroundColor(getColor(R.color.window_background));


    }


    @Override
    public void setContextualActionBarVisible(ApplicationCustomInterfaces.ContextualActionBarCallback contextualActionBarCallback, ApplicationCustomInterfaces.ManipulateTask manipulateTask) {

        actionMode = startSupportActionMode(new ActionMode.Callback() {
                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    mode.getMenuInflater().inflate(R.menu.select_task_menu, menu);
                    if (!pinTaskOnTop) menu.getItem(2).getSubMenu().getItem(2).setVisible(true);
                    menu.getItem(1).getIcon().mutate().setTint(ContextCompat.getColor(MainActivity.this, R.color.blue_violet));
                    return true;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return true;
                }

                @Override
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    int id = item.getItemId();

                    if (id == R.id.selectAllItem) {
                        item.setVisible(false);
                        actionMode.getMenu().getItem(1).setVisible(true);
                        if (contextualActionBarCallback != null) contextualActionBarCallback.selectAll();
                    }
                    else if (id == R.id.unSelectAllItem) {
                        item.setVisible(false);
                        actionMode.getMenu().getItem(0).setVisible(true);
                        if (contextualActionBarCallback != null) contextualActionBarCallback.unSelectAll(false);
                    }

                    else if (id == R.id.menu_lock) {
                        manipulateTask.lockTask();
                        actionMode.finish();
                    }
                    else if (id == R.id.menu_delete) {
                        manipulateTask.deleteTask();
                        actionMode.finish();
                    }
                    else if (id == R.id.menu_pin) {
                        manipulateTask.pinTask();
                        actionMode.finish();
                    }
                    else if (id == R.id.menu_unpin) {
                        manipulateTask.unPinTask();
                        actionMode.finish();
                    }
                    else if (id == R.id.menu_mark_done) {
                        manipulateTask.markTaskDone();
                        actionMode.finish();
                    }
                    else if (id == R.id.menu_mark_not_done) {
                        manipulateTask.markTaskUnDone();
                        actionMode.finish();
                    }



                    return true;
                }

                @Override
                public void onDestroyActionMode(ActionMode mode) {
                    if (contextualActionBarCallback != null) contextualActionBarCallback.unSelectAll(true);
                }

            });

    }


    @Override
    public void setContextualActionBarInVisible() {
        actionMode.finish();
    }


    @Override
    public void changeTitle(String value) {
        actionMode.setTitle(value);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.HOME_item1){
            viewPager2.setCurrentItem(0);
        }
        else if (id == R.id.TASKINBOX_item2){
            viewPager2.setCurrentItem(1);
        }
        else if (id == R.id.ACCOUNT_item3){
            viewPager2.setCurrentItem(2);
        }
        else {
            viewPager2.setCurrentItem(0);
        }
        return true;
    }


    @Override
    public void navItemChecked(int position) {
        switch (position){
            case 0:
                bottomNavigationView.setSelectedItemId(R.id.HOME_item1);
                break;
            case 1:
                bottomNavigationView.setSelectedItemId(R.id.TASKINBOX_item2);
                break;
            case 2:
                bottomNavigationView.setSelectedItemId(R.id.ACCOUNT_item3);
        }
    }




}