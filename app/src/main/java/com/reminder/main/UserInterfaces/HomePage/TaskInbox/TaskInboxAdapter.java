package com.reminder.main.UserInterfaces.HomePage.TaskInbox;


import static com.reminder.main.SqLite.TaskShared.TaskSharedConstants.USER_PRIMARY_ID;
import static com.reminder.main.SqLite.UserDetails.UserDetailsConstant.USER_NAME;
import static com.reminder.main.SqLite.UserDetails.UserDetailsConstant.USER_PROFESSION;
import static com.reminder.main.SqLite.UserDetails.UserDetailsConstant.USER_PROFILE_PIC;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;
import com.reminder.main.Custom.CustomInterfaces;
import com.reminder.main.R;
import com.reminder.main.SqLite.UserDetails.UserDetailsData;
import com.reminder.main.UserInterfaces.UserTaskInbox.MainActivity.UserTaskInbox;

import java.util.ArrayList;


public class TaskInboxAdapter extends RecyclerView.Adapter<TaskInboxAdapter.ViewHolder> {
    private Context context;
    private final ArrayList<UserDetailsData> taskSharedList;
    private final CustomInterfaces.RefreshLayout refreshLayout;
    private Intent intent;



    public TaskInboxAdapter(ArrayList<UserDetailsData> taskSharedList, CustomInterfaces.RefreshLayout refreshLayout) {
        this.taskSharedList = taskSharedList;
        this.refreshLayout = refreshLayout;
        setHasStableIds(true);
    }




    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        context = parent.getContext();
        intent = new Intent(context, UserTaskInbox.class);
        LayoutInflater inflater = LayoutInflater.from(context);
        return new ViewHolder(inflater.inflate(R.layout.task_inbox_card, parent, false));

    }






    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {


        primaryViewBinding(holder, position);


    }



    private void primaryViewBinding(ViewHolder holder, int position) {

        UserDetailsData data = taskSharedList.get(position);
        holder.setName.setText(data.getName());
        holder.setProfession.setText(data.getProfession());
        if (data.getProfilePic() != null && !data.getProfilePic().isEmpty()) {
            Glide.with(context)
                    .load(Uri.parse(data.getProfilePic()))
                    .into(holder.setProfilePic);
        }

        holder.cardView.setOnClickListener(view -> {
            intent.putExtra(USER_PRIMARY_ID, data.getUserPrimaryId());
            intent.putExtra(USER_NAME, data.getName());
            intent.putExtra(USER_PROFESSION, data.getProfession());
            intent.putExtra(USER_PROFILE_PIC, data.getProfilePic());
            context.startActivity(intent);
        });

    }







    @Override
    public int getItemCount() {
        return taskSharedList.size();
    }










    public static class ViewHolder extends RecyclerView.ViewHolder {

        public final TextView setName, setProfession;
        public final ShapeableImageView setProfilePic;
        public final MaterialCardView cardView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.setName = itemView.findViewById(R.id.setName);
            this.setProfession = itemView.findViewById(R.id.setProfession);
            this.setProfilePic = itemView.findViewById(R.id.setProfilePic);
            this.cardView = itemView.findViewById(R.id.editButton);
        }


    }








}
