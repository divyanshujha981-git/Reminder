package com.reminder.main.UserInterfaces.HomePage.Account;

import static com.reminder.main.SqLite.UserDetails.UserDetailsConstant.USER_EMAIL;
import static com.reminder.main.SqLite.UserDetails.UserDetailsConstant.USER_NAME;
import static com.reminder.main.SqLite.UserDetails.UserDetailsConstant.USER_PHONE_NUMBER;
import static com.reminder.main.SqLite.UserDetails.UserDetailsConstant.USER_PROFESSION;
import static com.reminder.main.SqLite.UserDetails.UserDetailsConstant.USER_PROFILE_PIC;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.reminder.main.Custom.CustomInterfaces;
import com.reminder.main.R;
import com.reminder.main.SqLite.UserDetails.UserDetailsData;

import java.text.DecimalFormat;
import java.util.Map;

public class AccountPage extends Fragment {
    private final DecimalFormat format = new DecimalFormat("00");
    private final UserDetailsData userData = new UserDetailsData();
    private TextView setPending, setCompleted, setSent, setReceived;
    private TextView setName, setEmail, setProfession;
    private ShapeableImageView setProfilePic;
    private final String TAG = "TAG";
    private int
            taskSentCount = 0,
            taskReceivedCount = 0,
            taskCompletedCount = 0,
            taskPendingCount = 0;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle){
        //getData();
        return inflater.inflate(R.layout.personal_account_page, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        declare(view);
    }

    @Override
    public void onStart() {
        super.onStart();
        fetchDataToUI();
    }

    @Override
    public void onResume() {
        super.onResume();
        ((CustomInterfaces.BottomNavItemCheck) requireContext()).navItemChecked(2);
    }


    @SuppressWarnings("ConstantConditions")
    public void setAccountData(Map<String, Object> accountData) {

        userData.setName((String) accountData.get(USER_NAME));
        userData.setEmail((String) accountData.get(USER_EMAIL));
        userData.setProfession((String) accountData.get(USER_PROFESSION));
        userData.setProfilePic((String) accountData.get(USER_PROFILE_PIC));
        userData.setPhoneNumber((String) accountData.get(USER_PHONE_NUMBER));

        taskSentCount = (int) accountData.get("Sent");
        taskReceivedCount = (int) accountData.get("Received");
        taskCompletedCount = (int) accountData.get("Completed");
        taskPendingCount = (int) accountData.get("Pending");

        try {
            fetchDataToUI();
        }
        catch (Exception e) {
            Log.w(TAG, "setAccountData: ", e);
        }

    }



    private void declare(View view) {

        setPending = view.findViewById(R.id.pending);
        setCompleted = view.findViewById(R.id.completed);
        setSent = view.findViewById(R.id.sent);
        setReceived = view.findViewById(R.id.received);

        setName = view.findViewById(R.id.setName);
        setEmail = view.findViewById(R.id.setEmail);
        setProfession =  view.findViewById(R.id.setProfession);
        setProfilePic = view.findViewById(R.id.setProfilePic);

    }





    private void fetchDataToUI() {

        setPending.setText(format.format(taskPendingCount));
        setCompleted.setText(format.format(taskCompletedCount));
        setSent.setText(format.format(taskSentCount));
        setReceived.setText(format.format(taskReceivedCount));

        Log.d("TAG", "fetchDataToUI: " +  userData.getPhoneNumber());

        setName.setText(userData.getName());
        setEmail.setText(userData.getEmail() != null && !userData.getEmail().equals("null") && !userData.getEmail().trim().isEmpty() ? userData.getEmail() : userData.getPhoneNumber());
        setProfession.setText(userData.getProfession());
        if (userData.getProfilePic() != null) {
            Glide.with(requireContext())
                    .load(Uri.parse(userData.getProfilePic()))
                    .into((setProfilePic));
        }


    }






}
