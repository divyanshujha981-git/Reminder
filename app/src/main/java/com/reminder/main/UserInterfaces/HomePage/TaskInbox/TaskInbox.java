package com.reminder.main.UserInterfaces.HomePage.TaskInbox;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.reminder.main.Custom.CustomInterfaces;
import com.reminder.main.R;
import com.reminder.main.SqLite.UserDetails.UserDetailsData;

import java.util.ArrayList;

public class TaskInbox extends Fragment {

    private RecyclerView recyclerView;
    private ArrayList<UserDetailsData> taskInboxPeopleData;
    private final String TAG = "TAG";
    private final CustomInterfaces.RefreshLayout mainPAgePagerAdapterContext;


    public TaskInbox(CustomInterfaces.RefreshLayout mainPAgePagerAdapterContext) {
        this.mainPAgePagerAdapterContext = mainPAgePagerAdapterContext;

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstance){
        return inflater.inflate(R.layout.default_recycler_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        declare(view);
    }


    @Override
    public void onStart() {
        super.onStart();
        setDataToUI();
    }

    @Override
    public void onResume() {
        super.onResume();
        ((CustomInterfaces.BottomNavItemCheck) requireContext()).navItemChecked(1);

    }

    private void declare(View view) {
        recyclerView = view.findViewById(R.id.recycler_view);
    }





    public void setTaskInboxDataToClass(ArrayList<UserDetailsData> taskInboxPeopleData) {

        this.taskInboxPeopleData = taskInboxPeopleData;
        try {
            setDataToUI();
        }
        catch (Exception e) {
            Log.w(TAG, "setTaskInboxDataToClass: ", e);
        }

    }


    private void setDataToUI() {
        recyclerView.setAdapter(new TaskInboxAdapter(taskInboxPeopleData, mainPAgePagerAdapterContext));
    }










}
