package com.reminder.main.UserInterfaces.SettingsPage.AccountSettings.BlockedContacts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;
import com.reminder.main.Other.AlertDialogueForAll;
import com.reminder.main.R;
import com.reminder.main.SqLite.BlockedContact.BlockedContactData;

import java.util.ArrayList;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.google.reminder/dex-files/0.dex */
public class BlockedContactsAdapter extends RecyclerView.Adapter<BlockedContactsAdapter.ViewHolder> {
    private AlertDialogueForAll alertDialogueForAll;
    private final ArrayList<BlockedContactData> blockedContacts;
    private Context context;
    private int currentPosition;
    private String currentUid;
    private View currentView;

    public interface BlockContactListener {
        void onBlockedContactChanged(int i);
    }

    public BlockedContactsAdapter(ArrayList<BlockedContactData> blockedContacts) {
        this.blockedContacts = blockedContacts;
    }

    @NonNull
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(this.context);
        return new ViewHolder(inflater.inflate(R.layout.block_user_card, parent, false));
    }

    public void onBindViewHolder(final ViewHolder holder, int position) {

    }


    public int getItemCount() {
        return this.blockedContacts.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final MaterialCardView cardView;
        public final TextView setName;
        public final TextView setProfession;
        public final ShapeableImageView setProfilePic;

        public ViewHolder(View itemView) {
            super(itemView);
            this.setName = (TextView) itemView.findViewById(R.id.setName);
            this.setProfession = (TextView) itemView.findViewById(R.id.setProfession);
            this.setProfilePic = itemView.findViewById(R.id.setProfilePic);
            this.cardView = itemView.findViewById(R.id.editButton);
        }
    }
}
