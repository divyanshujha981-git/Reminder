package com.reminder.main.UserInterfaces.PeoplePage.MainActivity;



import static com.reminder.main.Firebase.FirebaseConstants.REQUEST_RECEIVED_BYTE;
import static com.reminder.main.Firebase.FirebaseConstants.REQUEST_SENT;
import static com.reminder.main.Firebase.FirebaseConstants.REQUEST_SENT_BYTE;
import static com.reminder.main.Firebase.FirebaseConstants.REQUEST_TYPE;
import static com.reminder.main.Firebase.FirebaseConstants.STATUS_ACCEPTED;
import static com.reminder.main.Firebase.FirebaseConstants.STATUS_ACCEPTED_BYTE;
import static com.reminder.main.Firebase.FirebaseConstants.STATUS_PENDING_BYTE;
import static com.reminder.main.Firebase.FirebaseConstants.UPDATE_REQUEST_STATUS;
import static com.reminder.main.SqLite.Request.RequestConstants.STATUS;
import static com.reminder.main.SqLite.Request.RequestConstants.USER_PRIMARY_ID;
import static com.reminder.main.UserInterfaces.HomePage.MainActivity.MainActivity.FIREBASE_FUNCTIONS;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.google.gson.Gson;
import com.reminder.main.Custom.CustomInterfaces;
import com.reminder.main.SqLite.Request.RequestData;
import com.reminder.main.UserInterfaces.PeoplePage.Accepted.PeopleAccepted;
import com.reminder.main.UserInterfaces.PeoplePage.Pending.PeoplePending;
import com.reminder.main.UserInterfaces.PeoplePage.Search.PeopleSearch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class PeoplePagerAdapter extends FragmentStateAdapter implements
        CustomInterfaces.PeopleData,
        CustomInterfaces.RefreshLayout {
    private final PeoplePending peoplePending = new PeoplePending();
    private final PeopleAccepted peopleAccepted = new PeopleAccepted();
    private final PeopleSearch peopleSearch = new PeopleSearch();
    private final PeopleSQLData peopleSQLData;
    public static PeoplePagerAdapter PEOPLE_PAGER_ADAPTER_CONTEXT;



    public PeoplePagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
        PEOPLE_PAGER_ADAPTER_CONTEXT = this;
        peopleSQLData = new PeopleSQLData(fragmentActivity.getApplicationContext(), this);
    }







    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1:
                return peopleAccepted;
            case 2:
                return peoplePending;
            default:
                peopleSQLData.getPeopleData();
                return peopleSearch;
        }

    }







    @Override
    public int getItemCount() {
        return 3;
    }




    /**
     * @param userPrimaryId others uid
     * @param statusChangeTo pass false if you want to delete the request, else pass true
     * @param requestStatusChangeCallback callback
     */
    public static void updateRequestStatus(String userPrimaryId, boolean statusChangeTo, CustomInterfaces.RequestStatusChangeCallback requestStatusChangeCallback) {

        Map<String, Object> map = new HashMap<>();
        map.put(USER_PRIMARY_ID, userPrimaryId);
        map.put(STATUS, statusChangeTo);

        FIREBASE_FUNCTIONS
                .getHttpsCallable(UPDATE_REQUEST_STATUS)
                .call(new Gson().toJson(map))
                .addOnSuccessListener(httpsCallableResult -> {
                    Map<?, ?> data = (Map<?, ?>) httpsCallableResult.getData();
                    Log.d("TAG", "updateRequestStatus: " + data);
                    assert data != null;
                    if (data.containsKey(STATUS) && data.containsKey(REQUEST_TYPE)) {
                        Log.d("TAG", "updateRequestStatus: " + data.containsKey(STATUS) + " " + data.containsKey(STATUS));
                        boolean status = (boolean) data.get(STATUS);
                        boolean requestType = (boolean) data.get(REQUEST_TYPE);
                        requestStatusChangeCallback.requestStatusChangeCallback(
                                status == STATUS_ACCEPTED ? STATUS_ACCEPTED_BYTE : STATUS_PENDING_BYTE,
                                requestType == REQUEST_SENT ? REQUEST_SENT_BYTE : REQUEST_RECEIVED_BYTE
                        );
                    }
                    else {
                        requestStatusChangeCallback.deleteRequest();
                    }

                })
                .addOnFailureListener(e -> requestStatusChangeCallback.doNotChangeRequest())
                .addOnCompleteListener(task -> {

                });

    }


    @Override
    public void setPeoplePendingData(ArrayList<PeoplePendingOrAcceptedData> requestData) {
        Log.d("TAG", "setPeoplePendingData: ");
        peoplePending.setPeoplePendingDataToClass(requestData);
    }


    @Override
    public void setPeopleAcceptedData(ArrayList<PeoplePendingOrAcceptedData> requestData) {
        Log.d("TAG", "setPeopleAcceptedData: ");
        peopleAccepted.setPeopleAcceptedDataToClass(requestData);
    }

    @Override
    public void setPeoplePendingAndAcceptedData(Map<String, RequestData> requestData) {
        Log.d("TAG", "setPeoplePendingAndAcceptedData: ");
        peopleSearch.setPeoplePendingAndAcceptedDataToClass(requestData);
    }


    @Override
    public void refreshLayout(Class<?> cls) {
        Log.d("TAG", "refreshLayout: ");
        peopleSQLData.getPeopleData();
    }


}
