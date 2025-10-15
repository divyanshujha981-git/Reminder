package com.reminder.main.UserInterfaces.HomePage.TaskInbox;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.reminder.main.Other.ApplicationCustomInterfaces;
import com.reminder.main.R;

public class TaskInbox extends Fragment {

    private Context context;
    private RecyclerView recyclerView;
    private ApplicationCustomInterfaces.BottomNavItemCheck navItemCheck;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstance){
        context = requireContext();
        navItemCheck = (ApplicationCustomInterfaces.BottomNavItemCheck) context;
        return inflater.inflate(R.layout.default_recycler_adapter, container, false);
    }


    @Override
    public void onResume() {
        super.onResume();
        navItemCheck.navItemChecked(1);
    }











}
