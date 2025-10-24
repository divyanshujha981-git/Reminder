package com.reminder.main.UserInterfaces.UserTaskInbox.TaskReceived;

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

public class TaskReceived extends Fragment {
    private ArrayList<UserTaskInboxData> taskReceivedList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ImageView taskReceivedImageView;





    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.task_received, container, false);
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
        taskReceivedImageView = view.findViewById(R.id.taskSentImage);
    }


    private void setActions() {
        try {
            setDataToUI();
        }
        catch (Exception e) {
            Log.w("TAG", "setTaskReceivedToClass: ", e);
        }
    }


    public void setTaskReceivedToClass(ArrayList<UserTaskInboxData> taskReceivedList) {
        this.taskReceivedList = taskReceivedList;
        try {
            setDataToUI();
        }
        catch (Exception e) {
            Log.w("TAG", "setTaskReceivedToClass: ", e);
        }
    }


    private void setDataToUI() {
        if (!taskReceivedList.isEmpty()) {
            taskReceivedImageView.setVisibility(GONE);
            recyclerView.setAdapter(new TaskInboxReceivedAdapter(taskReceivedList));
        }

    }




}
