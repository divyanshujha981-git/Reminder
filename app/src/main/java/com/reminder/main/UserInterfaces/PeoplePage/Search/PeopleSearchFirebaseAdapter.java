package com.reminder.main.UserInterfaces.PeoplePage.Search;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

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
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.button.MaterialButton;
import com.reminder.main.R;
import com.reminder.main.SqLite.Request.RequestConstants;
import com.reminder.main.SqLite.Request.RequestData;

import java.util.Map;

public class PeopleSearchFirebaseAdapter extends FirebaseRecyclerAdapter<PeopleSearchData, PeopleSearchFirebaseAdapter.ViewHolder> {

    private Context context;
    private final Map<String, RequestData> requestData;

    public PeopleSearchFirebaseAdapter(@NonNull FirebaseRecyclerOptions<PeopleSearchData> options, Map<String, RequestData> requestData) {
        super(options);
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
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull PeopleSearchData model) {
        primaryViewBinding(holder, model);
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
