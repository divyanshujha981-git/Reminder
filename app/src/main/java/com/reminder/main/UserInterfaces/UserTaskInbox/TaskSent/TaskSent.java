package com.reminder.main.UserInterfaces.UserTaskInbox.TaskSent;

import static android.view.View.GONE;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;


import com.reminder.main.R;
import com.reminder.main.UserInterfaces.UserTaskInbox.MainActivity.UserTaskInboxData;

import java.util.ArrayList;





public class TaskSent extends Fragment {

    private ArrayList<UserTaskInboxData> taskSentList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ImageView taskSentImageView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.task_sent, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        declare(view);
    }

    @Override
    public void onResume() {
        super.onResume();

        setActions();

    }


    private void declare(View view) {
        recyclerView = view.findViewById(R.id.recycler_view);
        taskSentImageView = view.findViewById(R.id.taskSentImage);
    }


    private void setActions() {
        try {
            setDataToUI();
        }
        catch (Exception e) {
            Log.w("TAG", "setTaskReceivedToClass: ", e);
        }
    }


    public void setTaskSentToClass(ArrayList<UserTaskInboxData> taskSentList) {
        Log.d("TAG", "setTaskSentToClass: " + taskSentList.size());
        this.taskSentList = taskSentList;
        try {
            setDataToUI();
        }
        catch (Exception e) {
            Log.w("TAG", "setTaskReceivedToClass: ", e);
        }
    }


    private void setDataToUI() {
        if (!taskSentList.isEmpty()) {
            taskSentImageView.setVisibility(GONE);
            recyclerView.setAdapter(new TaskInboxSentAdapter(taskSentList));
        }

    }



}
