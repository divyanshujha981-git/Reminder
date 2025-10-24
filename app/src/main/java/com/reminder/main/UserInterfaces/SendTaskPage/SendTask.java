package com.reminder.main.UserInterfaces.SendTaskPage;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.reminder.main.SqLite.Tasks.TaskConstants.USER_PRIMARY_ID;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.functions.FirebaseFunctions;
import com.reminder.main.Other.ApplicationCustomInterfaces;
import com.reminder.main.R;
import com.reminder.main.SqLite.Tasks.TaskData;
import com.reminder.main.UserInterfaces.HomePage.MainActivity.MainActivity;
import com.reminder.main.UserInterfaces.HomePage.Tasks.NavBarDateTemplate;

import java.util.ArrayList;

public class SendTask extends AppCompatActivity implements
        ApplicationCustomInterfaces.TaskSQLInterface,
        ApplicationCustomInterfaces.ContextualActionBar {
    private final FirebaseFunctions FIREBASE_FUNCTION = MainActivity.FIREBASE_FUNCTIONS;
    private String userPrimaryId;
    private FloatingActionButton sendTaskButton;
    private MaterialCardView searchCard;
    private RecyclerView recyclerView;
    private ApplicationCustomInterfaces.ContextualActionBarCallback adapterTaskSelectCallback;
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
    }


    private void setAction() {
        setSupportActionBar(toolbar);
        sendTaskButton.setOnClickListener(this::sendTask);
        taskSQLData.getAllData();
    }


    private void sendTask(View v) {

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


    @Override
    public void setMainTaskData(ArrayList<TaskData> taskData) {
        recyclerView.setAdapter(new TasksAdapter(taskData, this));
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
    public void setContextualActionBarVisible(ApplicationCustomInterfaces.ContextualActionBarCallback contextualActionBarCallback, ApplicationCustomInterfaces.ManipulateTask manipulateTask) {
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



}
