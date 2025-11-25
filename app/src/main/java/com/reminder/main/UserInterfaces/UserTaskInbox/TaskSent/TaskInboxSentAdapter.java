package com.reminder.main.UserInterfaces.UserTaskInbox.TaskSent;


import static com.reminder.main.SqLite.TaskStatus.TaskStatusConstants.DOWNLOADED_YES_BYTE;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.reminder.main.R;
import com.reminder.main.UserInterfaces.UserTaskInbox.MainActivity.UserTaskInboxData;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;


public class TaskInboxSentAdapter extends RecyclerView.Adapter<TaskInboxSentAdapter.ViewHolder> {

    private final ArrayList<UserTaskInboxData> taskList;
    private final DecimalFormat minuteFormat = new DecimalFormat("00");
    private final Calendar calendar = Calendar.getInstance();
    private Resources resources;
    private String[] amPm;

    private final int timeStringFormat =  R.string.task_card_time_format;



    public TaskInboxSentAdapter(ArrayList<UserTaskInboxData> list){
        this.taskList = list;
        setHasStableIds(true);
    }








    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        resources = context.getResources();
        amPm = resources.getStringArray(R.array.amPm);

        View view = inflater.inflate(R.layout.task_sent_card, parent, false);
        return new ViewHolder(view);
    }










    @Override
    public int getItemCount() {
        return taskList.size();
    }








    @Override
    public long getItemId(int position) {
        return position;
    }








    @Override
    public int getItemViewType(int position) {
        return position;
    }








    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        UserTaskInboxData data = taskList.get(position);
        LinearLayout linearLayout = (LinearLayout) holder.itemView;
        primaryViewBinding(linearLayout, data);

    }








    private void primaryViewBinding(LinearLayout tableRow, UserTaskInboxData data) {

        MaterialCardView cardView = (MaterialCardView) tableRow.getChildAt(1);
        ((TextView) cardView.getChildAt(0)).setText(data.TASK_DATA.getTopic());

        calendar.setTimeInMillis(data.TASK_DATA.getRepeatingAlarmDate());

        ((TextView) tableRow.getChildAt(0))
                .setText(
                        resources.getString(
                                timeStringFormat,
                                calendar.get(Calendar.HOUR) == 0 ? 12 : calendar.get(Calendar.HOUR),
                                minuteFormat.format(calendar.get(Calendar.MINUTE)),
                                amPm[calendar.get(Calendar.AM_PM)]
                        )
                );


       if (data.TASK_STATUS_DATA.getDownloaded() == DOWNLOADED_YES_BYTE) {
           cardView.getChildAt(2).setVisibility(View.GONE);
       }

    }









    public static class ViewHolder extends RecyclerView.ViewHolder{
        public View itemView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
        }
    }





}



