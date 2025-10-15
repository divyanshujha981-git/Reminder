package com.reminder.main.UserInterfaces.HomePage.Account;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import com.google.firebase.auth.FirebaseUser;
import com.reminder.main.Other.ApplicationCustomInterfaces;
import com.reminder.main.R;
import com.reminder.main.SqLite.CommonDB.CommonDB;
import com.reminder.main.SqLite.TaskShared.TaskSharedConstants;
import com.reminder.main.SqLite.Tasks.TaskConstants;
import com.reminder.main.SqLite.UserDetails.UserDetailsConstant;
import com.reminder.main.SqLite.UserDetails.UserDetailsData;
import com.reminder.main.UserInterfaces.HomePage.MainActivity.MainActivity;

import java.text.DecimalFormat;

public class AccountPage extends Fragment {
    private final FirebaseUser FIREBASE_USER = MainActivity.FIREBASE_AUTH.getCurrentUser();
    private final DecimalFormat format = new DecimalFormat("00");
    private final UserDetailsData userData = new UserDetailsData();
    private TextView setPending, setCompleted, setSent, setReceived;
    private TextView setName, setEmail, setProfession;
    private ShapeableImageView setProfilePic;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private int
            taskSentCount = 0,
            taskReceivedCount = 0,
            taskCompletedCount = 0,
            taskPendingCount = 0;
    private ApplicationCustomInterfaces.BottomNavItemCheck navItemCheck;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle){
        getData();
        navItemCheck = (ApplicationCustomInterfaces.BottomNavItemCheck) requireContext();
        return inflater.inflate(R.layout.personal_account_page, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        declare(view);
    }

    @Override
    public void onResume() {
        super.onResume();
        navItemCheck.navItemChecked(2);



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



    private void getData() {

        new Thread(() -> {

            CommonDB commonDB = new CommonDB(requireContext());
            SQLiteDatabase database = commonDB.getReadableDatabase();

            assert FIREBASE_USER != null;
            Cursor cursor = database.rawQuery(
                    " SELECT " +
                        " subQuery1.c AS taskSent, " +                  // 0
                        " subQuery2.c AS taskReceived, " +              // 1
                        " subQuery3.c AS completed, " +                 // 2
                        " subQuery4.c AS pending, " +                   // 3
                        " subQuery5.uName AS uName, " +                 // 4
                        " subQuery5.uEmail AS uEmail, " +               // 5
                        " subQuery5.uPhone AS uPhone, " +               // 6
                        " subQuery5.uProfession AS uProfession, " +     // 7
                        " subQuery5.uPic AS uPic " +                    // 8

                        " FROM " +

                        " (SELECT count(*) AS c FROM (" +
                            " SELECT " + TaskSharedConstants.TASK_WEB_ID + " FROM " + TaskSharedConstants.TASK_SENT_TABLE_NAME + " GROUP BY " + TaskSharedConstants.TASK_WEB_ID +
                        ")) AS subQuery1, " +

                        " (SELECT count(*) AS c FROM (" +
                            " SELECT " + TaskConstants.TASK_WEB_ID + " FROM " + TaskSharedConstants.TASK_RECEIVED_TABLE_NAME + " GROUP BY " + TaskConstants.TASK_WEB_ID +
                        ")) AS subQuery2, " +

                        " (SELECT count(*) AS c FROM (" +
                            " SELECT twi FROM (" +
                                " SELECT " + TaskSharedConstants.TASK_SENT_TABLE_NAME+"."+TaskSharedConstants.TASK_WEB_ID + " AS twi FROM " + TaskSharedConstants.TASK_SENT_TABLE_NAME +
                                " LEFT JOIN " + TaskConstants.TASK_TABLE_NAME +
                                " ON " + TaskSharedConstants.TASK_SENT_TABLE_NAME+"."+TaskSharedConstants.TASK_WEB_ID + "=" + TaskConstants.TASK_TABLE_NAME+"."+TaskSharedConstants.TASK_WEB_ID +
                                " WHERE " + TaskConstants.TASK_TABLE_NAME+"."+TaskConstants.ALREADY_DONE + "=" + TaskConstants.ALREADY_DONE_YES_BYTE
                                    + " UNION ALL " +
                                " SELECT " + TaskSharedConstants.TASK_RECEIVED_TABLE_NAME+"."+TaskSharedConstants.TASK_WEB_ID + " AS twi FROM " + TaskSharedConstants.TASK_RECEIVED_TABLE_NAME +
                                " LEFT JOIN " + TaskConstants.TASK_TABLE_NAME +
                                " ON " + TaskSharedConstants.TASK_RECEIVED_TABLE_NAME+"."+TaskSharedConstants.TASK_WEB_ID + "=" + TaskConstants.TASK_TABLE_NAME+"."+TaskSharedConstants.TASK_WEB_ID +
                                " WHERE " + TaskConstants.TASK_TABLE_NAME+"."+TaskConstants.ALREADY_DONE + "=" + TaskConstants.ALREADY_DONE_YES_BYTE +
                            ") AS subSubQuery3 GROUP BY twi " +
                        ")) AS subQuery3, " +

                        " (SELECT count(*) AS c FROM (" +
                            " SELECT twi FROM (" +
                                " SELECT " + TaskSharedConstants.TASK_SENT_TABLE_NAME+"."+TaskSharedConstants.TASK_WEB_ID + " AS twi FROM " + TaskSharedConstants.TASK_SENT_TABLE_NAME +
                                " LEFT JOIN " + TaskConstants.TASK_TABLE_NAME +
                                " ON " + TaskSharedConstants.TASK_SENT_TABLE_NAME+"."+TaskSharedConstants.TASK_WEB_ID + "=" + TaskConstants.TASK_TABLE_NAME+"."+TaskSharedConstants.TASK_WEB_ID +
                                " WHERE " + TaskConstants.TASK_TABLE_NAME+"."+TaskConstants.ALREADY_DONE + "=" + TaskConstants.ALREADY_DONE_YES_BYTE
                                    + " UNION ALL " +
                                " SELECT " + TaskSharedConstants.TASK_RECEIVED_TABLE_NAME+"."+TaskSharedConstants.TASK_WEB_ID + " AS twi FROM " + TaskSharedConstants.TASK_RECEIVED_TABLE_NAME + " " +
                                " LEFT JOIN " + TaskConstants.TASK_TABLE_NAME +
                                " ON " + TaskSharedConstants.TASK_RECEIVED_TABLE_NAME+"."+TaskSharedConstants.TASK_WEB_ID + "=" + TaskConstants.TASK_TABLE_NAME+"."+TaskSharedConstants.TASK_WEB_ID +
                                " WHERE " + TaskConstants.TASK_TABLE_NAME+"."+TaskConstants.ALREADY_DONE + "=" + TaskConstants.ALREADY_DONE_NO_BYTE +
                            ") AS subSubQuery4 GROUP BY twi " +
                        ")) AS subQuery4, " +

                        " ( SELECT " +
                                UserDetailsConstant.USER_NAME + " AS uName, " +
                                UserDetailsConstant.USER_EMAIL + " AS uEmail, " +
                                UserDetailsConstant.USER_PHONE_NUMBER + " AS uPhone, " +
                                UserDetailsConstant.USER_PROFESSION + " AS uProfession, " +
                                UserDetailsConstant.USER_PROFILE_PIC +" AS uPic " +
                            " FROM " + UserDetailsConstant.USER_DETAILS_TABLE_NAME +
                            " WHERE " + UserDetailsConstant.USER_PRIMARY_ID  + " = \"" + FIREBASE_USER.getUid() + "\"" +
                        ") AS subQuery5 "

                    , null);



            if (cursor.getCount() > 0) {

                cursor.moveToFirst();
                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    Log.d("TAG", "getData: " + cursor.getColumnName(i) + " = " + cursor.getString(i));
                }

                taskSentCount = cursor.getInt(0);
                taskReceivedCount = cursor.getInt(1);
                taskCompletedCount = cursor.getInt(2);
                taskPendingCount = cursor.getInt(3);

                userData.setName(cursor.getString(4));
                userData.setEmail(cursor.getString(5));
                userData.setPhoneNumber(cursor.getString(6));
                userData.setProfession(cursor.getString(7));
                userData.setProfilePic(cursor.getString(8));

                handler.post(this::fetchDataToUI);

                cursor.close();


                Log.d("TAG", "getTaskData: " + taskSentCount);
                Log.d("TAG", "getTaskData: " + taskReceivedCount);
                Log.d("TAG", "getTaskData: " + taskCompletedCount);
                Log.d("TAG", "getTaskData: " + taskPendingCount);

            }

            commonDB.close();

        }).start();


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
