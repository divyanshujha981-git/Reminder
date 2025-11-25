package com.reminder.main.UserInterfaces.UserTaskInbox.TaskReceived;


import static android.text.Html.FROM_HTML_MODE_LEGACY;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.reminder.main.Firebase.FirebaseConstants.DOWNLOADED_TASK;
import static com.reminder.main.SqLite.TaskShared.TaskSharedConstants.TASK_RECEIVED_TABLE_NAME;
import static com.reminder.main.SqLite.TaskShared.TaskSharedDB.insertOrUpdateSingleTaskShared;
import static com.reminder.main.SqLite.TaskStatus.TaskStatusConstants.DOWNLOADED;
import static com.reminder.main.SqLite.TaskStatus.TaskStatusConstants.DOWNLOADED_YES_BYTE;
import static com.reminder.main.SqLite.Tasks.TaskConstants.TASK_WEB_ID;
import static com.reminder.main.UserInterfaces.HomePage.MainActivity.MainActivity.FIREBASE_FUNCTIONS;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.gson.Gson;
import com.reminder.main.R;
import com.reminder.main.SqLite.Tasks.TaskConstants;
import com.reminder.main.UserInterfaces.TaskViewPage.TaskViewMain;
import com.reminder.main.UserInterfaces.UserTaskInbox.MainActivity.UserTaskInboxData;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class TaskInboxReceivedAdapter extends RecyclerView.Adapter<TaskInboxReceivedAdapter.ViewHolder> {

    private final ArrayList<UserTaskInboxData> taskList;
    private Context context;
    private String[] repeatType, amPm;
    private Resources resources;
    private Typeface typeface;
    private final int
            timeStringFormat = R.string.task_card_time_format,
            repeatStringFormat = R.string.task_card_repeat_status_format,
            setTopicStringFormat = R.string.set_topic_task_card_format;


    public TaskInboxReceivedAdapter(ArrayList<UserTaskInboxData> list){
        this.taskList = list;
        setHasStableIds(true);
    }








    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        this.resources = context.getResources();
        typeface = context.getResources().getFont(R.font.sans_serif_medium);
        repeatType = resources.getStringArray(R.array.repeatType);
        amPm = resources.getStringArray(R.array.amPm);

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.task_received_card, parent, false);
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
        LinearLayout tableRow = (LinearLayout) holder.itemView;

        primaryViewBinding(tableRow, data);


    }








    private int i = 0;
    private void primaryViewBinding(LinearLayout tableRow, UserTaskInboxData data) {

        ((TextView) tableRow.getChildAt(0)).setText(data.TASK_DATA.getTopic());

        MaterialCardView downloadTaskCardView = (MaterialCardView) tableRow.getChildAt(1);
        MaterialCardView mainTaskCardView = (MaterialCardView) tableRow.getChildAt(2);


        if (data.TASK_STATUS_DATA.getDownloaded() == DOWNLOADED_YES_BYTE) {
            mainTaskCardView.setVisibility(VISIBLE);
            downloadTaskCardView.setVisibility(GONE);

            ((TextView) mainTaskCardView.getChildAt(2)).setText(resources.getString(setTopicStringFormat, data.TASK_DATA.getTopic()));

            ((TextView) mainTaskCardView.getChildAt(3)).setText(
                    Html.fromHtml(
                            resources.getString(
                                    repeatStringFormat,
                                    repeatType[data.TASK_DATA.getRepeatStatus() - 1]),
                            FROM_HTML_MODE_LEGACY)

            );

            if (data.TASK_DATA.getRepeatStatus() != TaskConstants.REPEAT_STATUS_NO_REPEAT) {
                LinearLayout daysInWeek = (LinearLayout) mainTaskCardView.getChildAt(4);

                try {
                    // FOR E.G:- [1, 2, 4, 5, 7]
                    for (; i < data.TASK_DATA.getDateArray().length(); i++) {
                        TextView days = (TextView) daysInWeek.getChildAt((int) data.TASK_DATA.getDateArray().get(i) - 1);
                        days.setTypeface(typeface);
                        days.setEnabled(true);
                    }
                    i = 0;
                } catch (JSONException ignored) {
                }
            }

            if (data.TASK_DATA.getPinned() == TaskConstants.PINNED_YES) mainTaskCardView.getChildAt(0).setVisibility(VISIBLE);

            mainTaskCardView.setOnClickListener(v -> context.startActivity(new Intent(context, TaskViewMain.class).putExtra(TaskConstants.TASK_ID, data.TASK_DATA.getTaskId())));



            if (data.TASK_DATA.getAlreadyDone() == TaskConstants.ALREADY_DONE_YES_BYTE) {
                mainTaskCardView.getChildAt(1).setVisibility(VISIBLE);
            }


        }

        else {
            mainTaskCardView.setVisibility(GONE);
            downloadTaskCardView.setVisibility(VISIBLE);
            ((TextView) downloadTaskCardView.getChildAt(0)).setText(resources.getString(setTopicStringFormat, data.TASK_DATA.getTopic()));
            downloadTaskCardView.getChildAt(1).setOnClickListener(view -> downloadTask(view, data.TASK_DATA.getTaskWebId()));
        }


    }







    private void downloadTask(View v, String taskWebId) {

        v.setEnabled(false);
        Map<String, String> map = new HashMap<>();
        map.put(TASK_WEB_ID, taskWebId);


        FIREBASE_FUNCTIONS.getHttpsCallable(DOWNLOADED_TASK)
                .call(new Gson().toJson(map))
                .addOnSuccessListener(httpsCallableResult -> {

                })
                .addOnFailureListener(e -> {

                })
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ContentValues values = new ContentValues();
                        values.put(DOWNLOADED, true);
                        insertOrUpdateSingleTaskShared(context, values, TASK_RECEIVED_TABLE_NAME, taskWebId);
                        v.setVisibility(GONE);
                    }
                    v.setEnabled(true);
                });




    }






    public static class ViewHolder extends RecyclerView.ViewHolder{
        public View itemView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
        }
    }





}



