package com.reminder.main.UserInterfaces.HomePage.Tasks;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.reminder.main.Other.ApplicationCustomInterfaces;
import com.reminder.main.R;
import com.reminder.main.SqLite.Tasks.TaskData;
import com.reminder.main.UserInterfaces.HomePage.MainActivity.MainActivity;
import com.reminder.main.UserInterfaces.HomePage.MainActivity.MainPagePagerAdapter;
import com.reminder.main.UserInterfaces.PinnedTaskPage.PinnedTask;
import com.reminder.main.UserInterfaces.TaskViewPage.TaskViewMain;

import java.text.DecimalFormat;
import java.util.ArrayList;


public class Tasks extends Fragment implements
        ApplicationCustomInterfaces.FilterTask {

    private RecyclerView taskRecyclerView;
    private ApplicationCustomInterfaces.NestedScroll nestedScroll;
    private TaskData upComingTaskData;
    private final DecimalFormat decimalFormat = new DecimalFormat("00");
    private int upComingTaskPosition;
    private MaterialButton clearFilterBtn;
    private ArrayList<TaskData> taskData = new ArrayList<>();
    private ArrayList<NavBarDateTemplate> navDateTaskData = new ArrayList<>();
    private ArrayList<ArrayList<TaskData>> filteredTask;
    private boolean pinTaskOnTop, pinnedTaskAvailable;
    private MaterialButton pinned;
    private ImageView noTaskImage;
    private LinearLayout unComingTaskLayout;
    private RecyclerView navBarRecyclerView;
    private final MainPagePagerAdapter MAIN_PAGE_PAGER_ADAPTER;
    private ApplicationCustomInterfaces.BottomNavItemCheck navItemCheck;



    public Tasks(MainPagePagerAdapter MAIN_PAGE_PAGER_ADAPTER) {
        this.MAIN_PAGE_PAGER_ADAPTER = MAIN_PAGE_PAGER_ADAPTER;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.eventtask_page, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        declare(view);
    }


    @Override
    public void onStart() {
        super.onStart();
        setActions();
        setDataToUI();
    }


    @Override
    public void onResume() {
        super.onResume();
        //getSqlData();
        navItemCheck.navItemChecked(0);
    }




    private void declare(View view) {

        navBarRecyclerView = view.findViewById(R.id.navBarDate_eventTaskPage);

        unComingTaskLayout = view.findViewById(R.id.unComingTask_taskEventPage);

        taskRecyclerView = view.findViewById(R.id.task_recycler_view);
        clearFilterBtn = view.findViewById(R.id.clearFilterButton);

        pinTaskOnTop = PreferenceManager.getDefaultSharedPreferences(requireContext()).getBoolean("pinTaskOnTop", true);
        navItemCheck = (ApplicationCustomInterfaces.BottomNavItemCheck) requireContext();
        pinned = view.findViewById(R.id.seePinned);

        nestedScroll = (ApplicationCustomInterfaces.NestedScroll) requireContext();
        noTaskImage = view.findViewById(R.id.noTaskFoundImage);

    }


    private void setActions() {

        taskRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        taskRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                nestedScroll.scrollIntercept(true);
                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
        clearFilterBtn.setOnClickListener(v -> {
            setMainTaskDataToUI();
            v.setVisibility(View.GONE);
        });

        navBarRecyclerView.requestDisallowInterceptTouchEvent(true);
        navBarRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                nestedScroll.scrollIntercept(false);
                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });

        pinned.setOnClickListener(v -> startActivity(new Intent(requireContext(), PinnedTask.class)));

    }


    public void setDataToUI() {
        setMainTaskDataToUI();
        setUpcomingTaskToUI();
        setNavDateTaskToUI();
        setPinnedDataToUI();
    }




    // ---------- ------------ ---------- //

    public void setMainTaskDataToClass(ArrayList<TaskData> taskData) {

        this.taskData = taskData;

        try {
            setMainTaskDataToUI();
        }
        catch (Exception e) {
            Log.w("TAG", "setMainTaskDataToClass: ", e);
        }


    }


    private void setMainTaskDataToUI() {

        if (taskData.isEmpty()) {
            noTaskImage.setVisibility(View.VISIBLE);
            taskRecyclerView.setVisibility(View.GONE);
        }
        else {
            taskRecyclerView.setAdapter(new TasksAdapter(taskData, getActivity(), MAIN_PAGE_PAGER_ADAPTER));
            noTaskImage.setVisibility(View.GONE);
            taskRecyclerView.setVisibility(View.VISIBLE);
        }

    }


    // ---------- ------------ ---------- //

    public void setUpcomingTaskToClass(TaskData tasks, int position) {
        upComingTaskData = tasks;
        upComingTaskPosition = position;

        try {
            setUpcomingTaskToUI();
        }
        catch (Exception e) {
            Log.w("TAG", "setUpcomingTaskToUI: ", e);
        }
    }


    private void setUpcomingTaskToUI() {

        if (upComingTaskData != null) {
            taskRecyclerView.scrollToPosition(upComingTaskPosition);
            String[] array = getResources().getStringArray(R.array.monthsInYearFull);

            //Calendar calendar = Calendar.getInstance();
            //calendar.setTimeInMillis(tasks.getRepeatingAlarmDate());

            //int date = calendar.get(Calendar.DAY_OF_MONTH);
            //int month = calendar.get(Calendar.MONTH);
            //int hour = calendar.get(Calendar.HOUR);
            //int minute = calendar.get(Calendar.MINUTE);

            int date = upComingTaskData.getDate();
            int month = upComingTaskData.getMonth();
            int hour = upComingTaskData.getHour();
            int minute = upComingTaskData.getMinute();


            ((TextView) ((TableRow) unComingTaskLayout.getChildAt(1)).getChildAt(0))
                    .setText(
                            getString(
                                    R.string.set_upcoming_task_format,
                                    date,
                                    array[month],
                                    hour == 0 ? 12 : hour,
                                    decimalFormat.format(minute),
                                    getResources().getStringArray(R.array.amPm)[upComingTaskData.getAmPm()]
                            )
                    );

            (((TableRow) unComingTaskLayout.getChildAt(1)).getChildAt(1)).setOnClickListener(v -> MainActivity.redirectToPage(upComingTaskData, getContext(), TaskViewMain.class));

        }
        else {
            unComingTaskLayout.getChildAt(0).setVisibility(View.GONE);
            unComingTaskLayout.getChildAt(1).setVisibility(View.GONE);
        }

    }


    // ---------- ------------ ---------- //

    public void setNavDateTaskToClass(ArrayList<NavBarDateTemplate> navDateTaskData) {

        this.navDateTaskData = navDateTaskData;

        try {
            setNavDateTaskToUI();
        }
        catch (Exception e) {
            Log.w("TAG", "setNavDateTaskToClass: ", e);
        }
    }


    public void setNavDateTaskToUI() {
        navBarRecyclerView.setAdapter(
                new NavBarDateAdapter(
                        navDateTaskData,
                        this,
                        index -> ((LinearLayoutManager) navBarRecyclerView.getLayoutManager()).scrollToPositionWithOffset(index, 0)
                )
        );
    }


    // ---------- ------------ ---------- //

    public void setFilteredTaskToClass(ArrayList<ArrayList<TaskData>> filteredTask) {
        if (this.filteredTask != null) this.filteredTask.clear();
        this.filteredTask = filteredTask;
    }


    @Override
    public void setFilteredTaskToUI(int adapterPosition) {
        taskRecyclerView.setAdapter(new TasksAdapter(filteredTask.get(adapterPosition), getActivity(), MAIN_PAGE_PAGER_ADAPTER));
        clearFilterBtn.setVisibility(View.VISIBLE);
    }


    // ---------- ------------ ---------- //

    public void setPinningDataToClass(boolean pinTaskOnTop, boolean pinnedTaskAvailable) {
        this.pinTaskOnTop = pinTaskOnTop;
        this.pinnedTaskAvailable = pinnedTaskAvailable;

        try {
            setPinnedDataToUI();
        }
        catch (Exception e) {
            Log.w("TAG", "setNavDateTaskToClass: ", e);
        }
    }


    private void setPinnedDataToUI() {
        if (pinTaskOnTop && pinnedTaskAvailable) {
            pinned.setVisibility(View.VISIBLE);
        }
        else {
            pinned.setVisibility(View.GONE);
        }
    }


    // ---------- ------------ ---------- //


}



