package com.reminder.main.UserInterfaces.UserTaskInbox.TaskReceived;


import static com.reminder.main.SqLite.TaskShared.TaskSharedConstants.TASK_RECEIVED_TABLE_NAME;
import static com.reminder.main.SqLite.TaskShared.TaskSharedDB.insertOrUpdateSingleTaskShared;
import static com.reminder.main.SqLite.TaskStatus.TaskStatusConstants.DOWNLOADED;
import static com.reminder.main.SqLite.TaskStatus.TaskStatusConstants.DOWNLOADED_YES_BYTE;
import static com.reminder.main.SqLite.Tasks.TaskConstants.TASK_WEB_ID;
import static com.reminder.main.UserInterfaces.HomePage.MainActivity.MainActivity.FIREBASE_FUNCTIONS;

import android.content.ContentValues;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.gson.Gson;
import com.reminder.main.R;
import com.reminder.main.UserInterfaces.UserTaskInbox.MainActivity.UserTaskInboxData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class TaskInboxReceivedAdapter extends RecyclerView.Adapter<TaskInboxReceivedAdapter.ViewHolder> {

    private final ArrayList<UserTaskInboxData> taskList;
    private Context context;
    private final Handler handler = new Handler(Looper.getMainLooper());


    public TaskInboxReceivedAdapter(ArrayList<UserTaskInboxData> list){
        this.taskList = list;
        setHasStableIds(true);
    }








    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
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
        TableRow tableRow = (TableRow) holder.itemView;

        primaryViewBinding(tableRow, data);


    }









    private void primaryViewBinding(TableRow tableRow, UserTaskInboxData data) {

        MaterialCardView cardView = (MaterialCardView) tableRow.getChildAt(1);
        ((TextView) cardView.getChildAt(0)).setText(data.TASK_DATA.getTopic());


        handler.post(() -> {
            MaterialButton button = (MaterialButton) cardView.getChildAt(2);
            if (data.TASK_STATUS_DATA.getDownloaded() == DOWNLOADED_YES_BYTE) {
                button.setVisibility(View.GONE);
            }
            else {
                button.setOnClickListener(v -> downloadTask(v, data.TASK_DATA.getTaskWebId()));
            }
        });


    }






    private void downloadTask(View v, String taskWebId) {

        v.setEnabled(false);
        Map<String, String> map = new HashMap<>();
        map.put(TASK_WEB_ID, taskWebId);


        FIREBASE_FUNCTIONS.getHttpsCallable("downloadTask")
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
                        v.setVisibility(View.GONE);
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



