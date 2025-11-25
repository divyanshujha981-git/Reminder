package com.reminder.main.UserInterfaces.SendTaskPage;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.reminder.main.Custom.CustomFunctions.getTaskWebID;
import static com.reminder.main.Firebase.FirebaseConstants.ALARM_DATE;
import static com.reminder.main.Firebase.FirebaseConstants.DATE_ARRAY;
import static com.reminder.main.Firebase.FirebaseConstants.DESCRIPTION;
import static com.reminder.main.Firebase.FirebaseConstants.DOWNLOADED;
import static com.reminder.main.Firebase.FirebaseConstants.DOWNLOADED_NO_BYTE;
import static com.reminder.main.Firebase.FirebaseConstants.DOWNLOADED_YES;
import static com.reminder.main.Firebase.FirebaseConstants.DOWNLOADED_YES_BYTE;
import static com.reminder.main.Firebase.FirebaseConstants.PERCENTAGE_COMPLETE;
import static com.reminder.main.Firebase.FirebaseConstants.REPEAT_STATUS;
import static com.reminder.main.Firebase.FirebaseConstants.SEND_TASK_REQUEST;
import static com.reminder.main.Firebase.FirebaseConstants.TASKS;
import static com.reminder.main.Firebase.FirebaseConstants.TASK_WEB_ID;
import static com.reminder.main.Firebase.FirebaseConstants.TOPIC;
import static com.reminder.main.Firebase.FirebaseConstants.USER_PRIMARY_ID_LIST;
import static com.reminder.main.SqLite.TaskShared.TaskSharedConstants.TASK_SENT_TABLE_NAME;
import static com.reminder.main.SqLite.Tasks.TaskConstants.TASK_ID;
import static com.reminder.main.SqLite.Tasks.TaskConstants.USER_PRIMARY_ID;

import android.content.ContentValues;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.gson.Gson;
import com.reminder.main.Custom.CustomInterfaces;
import com.reminder.main.R;
import com.reminder.main.SqLite.TaskShared.TaskSharedDB;
import com.reminder.main.SqLite.TaskStatus.TaskStatusDB;
import com.reminder.main.SqLite.Tasks.TaskData;
import com.reminder.main.SqLite.Tasks.TasksDB;
import com.reminder.main.UserInterfaces.HomePage.MainActivity.MainActivity;
import com.reminder.main.UserInterfaces.HomePage.Tasks.NavBarDateTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;




public class SendTask extends AppCompatActivity implements
        CustomInterfaces.TaskSQLInterface,
        CustomInterfaces.ContextualActionBar, TextWatcher {
    private final FirebaseFunctions FIREBASE_FUNCTION = MainActivity.FIREBASE_FUNCTIONS;
    private String userPrimaryId;
    private CoordinatorLayout circularProgress;
    private FloatingActionButton sendTaskButton;
    private TasksAdapter tasksAdapter;
    private MaterialCardView searchCard;
    private RecyclerView recyclerView;
    private CustomInterfaces.ContextualActionBarCallback adapterTaskSelectCallback;
    private Menu menu;
    private EditText searchUser;
    private TaskSQLData taskSQLData;
    private MaterialToolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_task);

        declare();
        setAction();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.send_tasks, menu);
        this.menu = menu;
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.searchTask) {
            startSearch(VISIBLE);
        }
        else if (id == R.id.closeSearch) {
            startSearch(GONE);
        }
        else if (id == R.id.selectAllItem) {
            selectItem(true);
        }
        else if (id == R.id.unSelectAllItem) {
            selectItem(false);
        }
        else if (id == android.R.id.home) {
            Log.d("TAG", "onOptionsItemSelected: CLICKED");
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);

    }


    private void declare() {
        userPrimaryId = getIntent().getStringExtra(USER_PRIMARY_ID);
        sendTaskButton = findViewById(R.id.sendTaskButton);
        searchCard = findViewById(R.id.searchCard);
        searchUser = findViewById(R.id.searchUser);
        recyclerView = findViewById(R.id.recycler_view);
        taskSQLData = new TaskSQLData(this);
        toolbar = findViewById(R.id.toolBar);
        circularProgress = findViewById(R.id.circular_progress_middle_view);
    }


    private void setAction() {
        setSupportActionBar(toolbar);
        sendTaskButton.setOnClickListener(this::sendTask);
        taskSQLData.getAllData();

        searchUser.addTextChangedListener(this);
    }


    private void sendTask(View v) {
        //animateView(v, GONE);
        circularProgress.setVisibility(VISIBLE);

        Map<String, Map<String, Object>> selectedTask = new HashMap<>();

        ArrayList<ContentValues> updatableTasks = new ArrayList<>();

        tasksAdapter.getSelectedTask().forEach((taskID, taskData) -> {
            Map<String, Object> subTask = new HashMap<>();

            subTask.put(TOPIC, taskData.getTopic());
            subTask.put(ALARM_DATE, taskData.getAlarmDate());
            subTask.put(REPEAT_STATUS, taskData.getRepeatStatus());
            subTask.put(DATE_ARRAY, taskData.getDateArray().toString());
            if (taskData.getDescription() != null && !taskData.getDescription().isEmpty()) {
                subTask.put(DESCRIPTION, taskData.getTopic());
            }

            String taskWebId = getTaskWebID(this, taskData.getTaskId(), taskData.getTaskWebId());
            Log.d("TAG", "sendTask: " + taskWebId);
            selectedTask.put(taskWebId, subTask);

            ContentValues values = new ContentValues();
            values.put(TASK_WEB_ID, taskWebId);
            values.put(TASK_ID, taskID);
            updatableTasks.add(values);

        });

        TasksDB.updateMultipleTask(this, updatableTasks);

        Map<String, Object> data = new HashMap<>();
        data.put(TASKS, selectedTask);
        data.put(USER_PRIMARY_ID_LIST, new ArrayList<>(Collections.singletonList(userPrimaryId)));

        FIREBASE_FUNCTION.getHttpsCallable(SEND_TASK_REQUEST)
                .call(new Gson().toJson(data))
                .addOnSuccessListener(httpsCallableResult -> {
                    new Thread(() -> {
                        assert httpsCallableResult.getData() != null;
                        fetchSentTaskToSQLite((Map<?, ?>) httpsCallableResult.getData());
                        new Handler(Looper.getMainLooper()).post(this::finish);
                    }).start();

                })
                .addOnFailureListener(e -> {



                })
                .addOnCompleteListener(task -> {
                    circularProgress.setVisibility(GONE);
                });

    }


    private void animateView(View v, int visibility) {

        if (visibility == VISIBLE) {
            v.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(100)
                    .withEndAction(() -> {
                        v.setVisibility(visibility);
                    })
                    .start();
        }
        else {
            v.animate()
                    .scaleX(0f)
                    .scaleY(0f)
                    .setDuration(100)
                    .withEndAction(() -> {
                        v.setVisibility(visibility);
                    })
                    .start();
        }


    }


    private void startSearch(int visible) {
        animateView(searchCard, visible);
        searchCard.setVisibility(visible);
        if (VISIBLE == visible) {
            menu.getItem(0).setVisible(false);
            menu.getItem(1).setVisible(true);
        }
        else {
            menu.getItem(0).setVisible(true);
            menu.getItem(1).setVisible(false);
        }
    }



    private void selectItem(boolean selectAll) {
        if (selectAll) {
            menu.getItem(2).setVisible(false);
            menu.getItem(3).setVisible(true);
            adapterTaskSelectCallback.selectAll();
        }
        else {
            menu.getItem(2).setVisible(true);
            menu.getItem(3).setVisible(false);
            adapterTaskSelectCallback.unSelectAll(false/*NO USE*/);
        }
    }



    private void fetchSentTaskToSQLite(Map<?,?> data) {

        ArrayList<ContentValues> taskSentData = new ArrayList<>();
        ArrayList<ContentValues>  taskStatusData = new ArrayList<>();

        data.forEach((uid, object) -> {

            Map<?,?> subTask = (Map<?,?>) object;

            ((Map<?,?>) Objects.requireNonNull(subTask.get(TASKS))).forEach((taskID, subTaskData) -> {

                Log.d("TAG", "fetchSentTaskToSQLite: " + uid);
                Log.d("TAG", "fetchSentTaskToSQLite: " + taskID);
                Log.d("TAG", "fetchSentTaskToSQLite: " + subTaskData);

                ContentValues taskSentDataContentValues = new ContentValues();
                ContentValues taskStatusDataContentValues = new ContentValues();

                boolean downloaded = Boolean.parseBoolean(String.valueOf(((Map<?,?>) subTaskData).get(DOWNLOADED)));
                taskSentDataContentValues.put(USER_PRIMARY_ID, String.valueOf(uid));
                taskSentDataContentValues.put(TASK_WEB_ID, String.valueOf(taskID));

                taskStatusDataContentValues.put(USER_PRIMARY_ID, String.valueOf(uid));
                taskStatusDataContentValues.put(TASK_WEB_ID, String.valueOf(taskID));
                taskStatusDataContentValues.put(DOWNLOADED, downloaded == DOWNLOADED_YES ? DOWNLOADED_YES_BYTE : DOWNLOADED_NO_BYTE);
                taskStatusDataContentValues.put(PERCENTAGE_COMPLETE, 0);

                taskSentData.add(taskSentDataContentValues);
                taskStatusData.add(taskStatusDataContentValues);

            });

        });

        TaskSharedDB.insertOrUpdateMultipleTaskShared(this, taskSentData, TASK_SENT_TABLE_NAME);
        TaskStatusDB.insertOrUpdateMultipleTaskStatus(this, taskStatusData);

    }


    @Override
    public void setMainTaskData(ArrayList<TaskData> taskData) {
        tasksAdapter = new TasksAdapter(taskData, this);
        recyclerView.setAdapter(tasksAdapter);
    }

    @Override
    public void setUpComingTask(TaskData tasks, int position) {

    }

    @Override
    public void setNavDateTask(ArrayList<NavBarDateTemplate> navDateArray) {

    }

    @Override
    public void setFilteredTask(ArrayList<ArrayList<TaskData>> navDateArray) {

    }

    @Override
    public void setPinnedTaskOnTop(boolean pinTaskOnTop, boolean isPinnedTaskAvailable) {

    }

    @Override
    public void setContextualActionBarVisible(CustomInterfaces.ContextualActionBarCallback contextualActionBarCallback, CustomInterfaces.ManipulateTask manipulateTask) {
        adapterTaskSelectCallback = contextualActionBarCallback;
    }

    @Override
    public void setContextualActionBarInVisible() {

    }

    @Override
    public void changeTitle(String value) {
        Log.d("TAG", "changeTitle: " + value);
        getSupportActionBar().setTitle(value);
    }


    @Override
    public void afterTextChanged(Editable editable) {

    }


    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }


    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }


}
