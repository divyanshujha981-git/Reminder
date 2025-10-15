package com.reminder.main.UserInterfaces.PeoplePage.Search;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.reminder.main.SqLite.UserDetails.UserDetailsConstant.USER_EMAIL;
import static com.reminder.main.SqLite.UserDetails.UserDetailsConstant.USER_NAME;
import static com.reminder.main.SqLite.UserDetails.UserDetailsConstant.USER_PHONE_NUMBER;
import static com.reminder.main.SqLite.UserDetails.UserDetailsConstant.USER_PRIMARY_ID;
import static com.reminder.main.SqLite.UserDetails.UserDetailsConstant.USER_PROFESSION;
import static com.reminder.main.SqLite.UserDetails.UserDetailsConstant.USER_PROFILE_PIC;

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
import com.reminder.main.SqLite.Request.RequestData;
import com.reminder.main.SqLite.Request.RequestsDB;
import com.reminder.main.SqLite.UserDetails.UserDetailsDB;
import com.reminder.main.UserInterfaces.PeoplePage.MainActivity.PeoplePagerAdapter;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class PeopleSearchAdapter extends RecyclerView.Adapter<PeopleSearchAdapter.ViewHolder> implements
        ApplicationCustomInterfaces.RequestStatusChangeCallback {

    private Context context;
    private final Map<String, RequestData> requestData;
    private final ArrayList<PeopleSearchData> peopleSearchData;
    private PeopleSearchData currentPeopleData;
    private ViewHolder currentViewHolder;
    private View currentViewOrButton;
    private final ApplicationCustomInterfaces.RefreshLayout refreshCallback = PeoplePagerAdapter.PEOPLE_PAGER_ADAPTER_CONTEXT;



    public PeopleSearchAdapter (ArrayList<PeopleSearchData> peopleSearchData, Map<String, RequestData> requestData) {
        this.peopleSearchData = peopleSearchData;
        this.requestData = requestData;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        return new ViewHolder(inflater.inflate(R.layout.people_search_card, parent, false));
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        primaryViewBinding(holder, peopleSearchData.get(position));
    }


    private void primaryViewBinding(ViewHolder holder, PeopleSearchData model) {

        holder.setName.setText(model.getName());
        holder.setProfession.setText(model.getProfession());

        if (model.getProfilePic() != null) {
            Glide.with(context)
                    .load(Uri.parse(model.getProfilePic()))
                    .into((holder.setProfilePic));
        }

        RequestData data = requestData.get(model.getUserPrimaryId());
        if (data == null) {
            holder.connect.setVisibility(VISIBLE);
            holder.accepted.setVisibility(GONE);
            holder.accept.setVisibility(GONE);
            holder.reject.setVisibility(GONE);
            holder.rejectOnly.setVisibility(GONE);
        }
        else if (data.getStatus() == RequestConstants.STATUS_ACCEPTED_BYTE) {
            holder.connect.setVisibility(GONE);
            holder.accepted.setVisibility(VISIBLE);
            holder.accept.setVisibility(GONE);
            holder.reject.setVisibility(GONE);
            holder.rejectOnly.setVisibility(GONE);
        }
        else if (data.getStatus() == RequestConstants.STATUS_PENDING_BYTE) {
            if (data.getRequestType() == RequestConstants.REQUEST_SENT_BYTE) {
                holder.connect.setVisibility(GONE);
                holder.accepted.setVisibility(GONE);
                holder.accept.setVisibility(GONE);
                holder.reject.setVisibility(GONE);
                holder.rejectOnly.setVisibility(VISIBLE);
            }
            else {
                holder.connect.setVisibility(GONE);
                holder.accepted.setVisibility(GONE);
                holder.accept.setVisibility(VISIBLE);
                holder.reject.setVisibility(VISIBLE);
                holder.rejectOnly.setVisibility(GONE);
            }
        }

        setClickListeners (holder, model);

    }


    private void setClickListeners (ViewHolder holder, PeopleSearchData model) {

        holder.connect.setOnClickListener(view -> {
            currentViewOrButton = view;
            currentPeopleData = model;
            currentViewHolder = holder;
            updateRequestToFirebase(true);
        });
        holder.accepted.setOnClickListener(view -> {
            currentViewOrButton = view;
            currentPeopleData = model;
            currentViewHolder = holder;
            updateRequestToFirebase(true);
        });
        holder.accept.setOnClickListener(view -> {
            currentViewOrButton = view;
            currentPeopleData = model;
            currentViewHolder = holder;
            updateRequestToFirebase(true);
        });
        holder.reject.setOnClickListener(view -> {
            currentViewOrButton = view;
            currentPeopleData = model;
            currentViewHolder = holder;
            updateRequestToFirebase(false);
        });
        holder.rejectOnly.setOnClickListener(view -> {
            currentViewOrButton = view;
            currentPeopleData = model;
            currentViewHolder = holder;
            updateRequestToFirebase(false);
        });

    }


    private void updateRequestDataToSQLite(byte status, byte requestType, PeopleSearchData peopleSearchData) {

        ContentValues values = new ContentValues();
        values.put(RequestConstants.STATUS, status);
        values.put(RequestConstants.REQUEST_TYPE_SENT_OR_RECEIVED, requestType);
        values.put(RequestConstants.USER_PRIMARY_ID, peopleSearchData.getUserPrimaryId());
        RequestsDB.insertORUpdateSingleRequest(context, RequestConstants.REQUEST_TABLE_NAME, values);

        values.clear();

        values.put(USER_PRIMARY_ID, peopleSearchData.getUserPrimaryId());
        values.put(USER_NAME, peopleSearchData.getName());
        values.put(USER_EMAIL, peopleSearchData.getEmail());
        values.put(USER_PHONE_NUMBER, peopleSearchData.getPhoneNumber());
        values.put(USER_PROFILE_PIC, peopleSearchData.getProfilePic());
        values.put(USER_PROFESSION, peopleSearchData.getProfession());
        UserDetailsDB.insertOrUpdateSingleUser(context, values);

    }


    /**
     * @param status pass whatever integer except 1 and 2 if you want to delete the request
     *               else pass constants of FirebaseConstants or RequestConstants
     * @param requestType pass anything if nothing is given, accepts {@link RequestConstants#REQUEST_SENT_BYTE} or {@link RequestConstants#REQUEST_RECEIVED_BYTE}
     */
    private void enableButtonsAfterClick(View previousView, ViewHolder holder, byte status, byte requestType) {
        previousView.setEnabled(true);
        previousView.setVisibility(GONE);
        if (status == RequestConstants.STATUS_PENDING_BYTE) {
            if (requestType == RequestConstants.REQUEST_RECEIVED_BYTE) {
                holder.accept.setVisibility(VISIBLE);
                holder.reject.setVisibility(VISIBLE);
            }
            else {
                holder.rejectOnly.setVisibility(VISIBLE);
            }
        }
        else if (status == RequestConstants.STATUS_ACCEPTED_BYTE) {
            holder.accepted.setVisibility(VISIBLE);
        }
        else {
            holder.connect.setVisibility(VISIBLE);
        }
    }


    /**
    * @param status pass false if you want to delete the request, else pass true
    */
    private void updateRequestToFirebase(boolean status) {
        currentViewOrButton.setEnabled(false);

        PeoplePagerAdapter.updateRequestStatus(
                currentPeopleData.getUserPrimaryId(),
                status,
                this
        );
    }


    @Override
    public int getItemCount() {
        return peopleSearchData.size();
    }

    @Override
    public void requestStatusChangeCallback(byte status, byte requestType) {

        try {
            Objects.requireNonNull(requestData.get(currentPeopleData.getUserPrimaryId())).setRequestType(requestType);
            Objects.requireNonNull(requestData.get(currentPeopleData.getUserPrimaryId())).setStatus(status);
        } catch (NullPointerException ignored) {}

        enableButtonsAfterClick(currentViewOrButton, currentViewHolder, status, requestType);
        updateRequestDataToSQLite(status, requestType, currentPeopleData);

        refreshCallback.refreshLayout();

    }

    @Override
    public void deleteRequest() {
        requestData.remove(currentPeopleData.getUserPrimaryId());
        enableButtonsAfterClick(currentViewOrButton, currentViewHolder, (byte) 5, RequestConstants.REQUEST_SENT_BYTE);
        RequestsDB.deleteRequest(context, RequestConstants.REQUEST_TABLE_NAME, currentPeopleData.getUserPrimaryId());
        refreshCallback.refreshLayout();
    }

    @Override
    public void doNotChangeRequest() {
        currentViewOrButton.setEnabled(true);
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView setName, setProfession;
        public ImageView setProfilePic;
        public MaterialButton connect, accepted, accept, reject, rejectOnly;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            setName = itemView.findViewById(R.id.setName);
            setProfession = itemView.findViewById(R.id.setProfession);
            setProfilePic = itemView.findViewById(R.id.setProfilePic);
            connect = itemView.findViewById(R.id.connect);
            accepted = itemView.findViewById(R.id.accepted);
            accept = itemView.findViewById(R.id.acceptButton);
            reject = itemView.findViewById(R.id.rejectButton);
            rejectOnly = itemView.findViewById(R.id.rejectButtonOnly);
        }
    }


}
