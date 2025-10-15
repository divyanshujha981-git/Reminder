package com.reminder.main.UserInterfaces.PeoplePage.Accepted;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.reminder.main.Other.ApplicationCustomInterfaces;
import com.reminder.main.R;
import com.reminder.main.SqLite.Request.RequestConstants;
import com.reminder.main.SqLite.Request.RequestsDB;
import com.reminder.main.UserInterfaces.PeoplePage.MainActivity.PeoplePagerAdapter;
import com.reminder.main.UserInterfaces.PeoplePage.MainActivity.PeoplePendingOrAcceptedData;

import java.util.ArrayList;

public class PeopleAcceptedAdapter extends RecyclerView.Adapter<PeopleAcceptedAdapter.ViewHolder> implements
        ApplicationCustomInterfaces.RequestStatusChangeCallback {

    private Context context;
    private final ArrayList<PeoplePendingOrAcceptedData> peopleData;
    private PeoplePendingOrAcceptedData currentPeopleData;
    //private ViewHolder currentViewHolder;
    private View currentViewOrButton;
    private final ApplicationCustomInterfaces.RefreshLayout refreshCallback = PeoplePagerAdapter.PEOPLE_PAGER_ADAPTER_CONTEXT;




    public PeopleAcceptedAdapter(ArrayList<PeoplePendingOrAcceptedData> peopleData) {
        this.peopleData = peopleData;
        setHasStableIds(true);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        return new ViewHolder(inflater.inflate(R.layout.people_accepted_card, parent, false));
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        primaryViewBinding(holder, peopleData.get(position));
    }

    @Override
    public int getItemCount() {
        return peopleData.size();
    }


    private void primaryViewBinding(ViewHolder holder, PeoplePendingOrAcceptedData model) {

        holder.setName.setText(model.USER_DATA.getName());
        holder.setProfession.setText(model.USER_DATA.getProfession());
        if (model.USER_DATA.getProfilePic() != null) {
            Glide.with(context)
                    .load(Uri.parse(model.USER_DATA.getProfilePic()))
                    .into((holder.setProfilePic));
        }

        setClickListener(holder, model);

    }


    private void setClickListener(ViewHolder holder, PeoplePendingOrAcceptedData model) {


        //currentViewHolder = holder;

        holder.accepted.setOnClickListener(view -> {
            currentViewOrButton = view;
            currentPeopleData = model;
            updateRequestToFirebase();
        });
    }


    private void updateRequestToFirebase() {
        currentViewOrButton.setEnabled(false);

        PeoplePagerAdapter.updateRequestStatus(
                currentPeopleData.REQUEST_DATA.getUserPrimaryId(),
                false,
                this
        );

    }

    @Override
    public void requestStatusChangeCallback(byte status, byte requestType) {
        updateRequestDataToSQLite(status, requestType, currentPeopleData);
        refreshCallback.refreshLayout();
    }

    @Override
    public void deleteRequest() {
        RequestsDB.deleteRequest(context, RequestConstants.REQUEST_TABLE_NAME, currentPeopleData.REQUEST_DATA.getUserPrimaryId());
        refreshCallback.refreshLayout();
    }

    @Override
    public void doNotChangeRequest() {
        currentViewOrButton.setEnabled(true);
    }


    private void updateRequestDataToSQLite(byte status, byte requestType, PeoplePendingOrAcceptedData peopleSearchData) {

        ContentValues values = new ContentValues();
        values.put(RequestConstants.STATUS, status);
        values.put(RequestConstants.REQUEST_TYPE_SENT_OR_RECEIVED, requestType);
        values.put(RequestConstants.USER_PRIMARY_ID, peopleSearchData.REQUEST_DATA.getUserPrimaryId());
        RequestsDB.insertORUpdateSingleRequest(context, RequestConstants.REQUEST_TABLE_NAME, values);

        values.clear();

//        values.put(USER_PRIMARY_ID, peopleSearchData.USER_DATA.getUserPrimaryId());
//        values.put(USER_NAME, peopleSearchData.USER_DATA.getName());
//        values.put(USER_EMAIL, peopleSearchData.USER_DATA.getEmail());
//        values.put(USER_PHONE_NUMBER, peopleSearchData.USER_DATA.getPhoneNumber());
//        values.put(USER_PROFILE_PIC, peopleSearchData.USER_DATA.getProfilePic());
//        values.put(USER_PROFESSION, peopleSearchData.USER_DATA.getProfession());
//        UserDetailsDB.insertOrUpdateSingleUser(context, values);

    }



    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView setName, setProfession;
        public ImageView setProfilePic;
        public MaterialButton accepted;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            setName = itemView.findViewById(R.id.setName);
            setProfession = itemView.findViewById(R.id.setProfession);
            setProfilePic = itemView.findViewById(R.id.setProfilePic);
            accepted = itemView.findViewById(R.id.accepted);
        }
    }


}
