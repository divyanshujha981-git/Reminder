package com.reminder.main.UserInterfaces.PeoplePage.Accepted;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.reminder.main.R;
import com.reminder.main.SqLite.CommonDB.CommonDB;
import com.reminder.main.SqLite.UserDetails.UserDetailsConstant;
import com.reminder.main.UserInterfaces.PeoplePage.MainActivity.PeoplePendingOrAcceptedData;

import java.util.ArrayList;

public class PeopleAccepted extends Fragment {

    private RecyclerView recyclerView;
    private ArrayList<PeoplePendingOrAcceptedData> requestData;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.default_recycler_view, container, false);
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        declare(view);
        setAction();
    }


    private void declare(View view) {
        recyclerView = view.findViewById(R.id.recycler_view);
    }


    private void setAction() {
        //printUserDetails();
        if (requestData != null) recyclerView.setAdapter(new PeopleAcceptedAdapter(requestData));
    }


    @SuppressWarnings("unused")
    public void printUserDetails() {

        CommonDB commonDB = new CommonDB(requireContext());
        SQLiteDatabase db = commonDB.getReadableDatabase();
        String tableName = UserDetailsConstant.USER_DETAILS_TABLE_NAME;

        // Query all rows
        Cursor cursor = db.rawQuery("SELECT * FROM " + tableName, null);

        if (cursor.moveToFirst()) {
            do {
                // Example: print column values dynamically
                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    String columnName = cursor.getColumnName(i);
                    String value = cursor.getString(i);
                    System.out.println(columnName + ": " + value);
                }
                System.out.println("------");
            } while (cursor.moveToNext());
        } else {
            System.out.println("No data found in table: " + tableName);
        }

        commonDB.close();
        db.close();
        cursor.close();
    }





    public void setPeopleAcceptedDataToClass(ArrayList<PeoplePendingOrAcceptedData> requestData) {
        Log.d("TAG", "setPeopleAcceptedDataToClass: ");
        this.requestData = requestData;
        if (recyclerView != null) {
            recyclerView.setAdapter(new PeopleAcceptedAdapter(requestData));
        }

    }



}
