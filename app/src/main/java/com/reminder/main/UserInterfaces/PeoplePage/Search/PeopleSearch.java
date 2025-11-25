package com.reminder.main.UserInterfaces.PeoplePage.Search;

import static com.reminder.main.Firebase.FirebaseConstants.IS_ACCOUNT_PRIVATE;
import static com.reminder.main.Firebase.FirebaseConstants.USERS;
import static com.reminder.main.Firebase.FirebaseConstants.USER_EMAIL;
import static com.reminder.main.Firebase.FirebaseConstants.USER_NAME;
import static com.reminder.main.Firebase.FirebaseConstants.USER_PHONE_NUMBER;
import static com.reminder.main.Firebase.FirebaseConstants.USER_PROFESSION;
import static com.reminder.main.Firebase.FirebaseConstants.USER_PROFILE_PIC;
import static com.reminder.main.UserInterfaces.HomePage.MainActivity.MainActivity.FIREBASE_DATABASE;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.reminder.main.R;
import com.reminder.main.SqLite.Request.RequestData;

import java.util.ArrayList;
import java.util.Map;


public class PeopleSearch extends Fragment {
    private EditText searchUser;
    private Map<String, RequestData> requestData;
    private DatabaseReference ref;
    private RecyclerView recyclerView;
    private final FirebaseUser FIREBASE_USER = FirebaseAuth.getInstance().getCurrentUser();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.people_search, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        declare(view);
        setActions();
    }


    private void declare(View view) {
        searchUser = view.findViewById(R.id.searchUser);
        recyclerView = view.findViewById(R.id.recycler_view);
        ref = FIREBASE_DATABASE.getReference(USERS);
    }


    private void setActions() {

        //getRequestData();
        searchUser.setEnabled(false);

        searchUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {

            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String latestText = charSequence.toString().trim();
                if (!latestText.isEmpty()) {
                    searchUsers(latestText);
                    //setSearchUser(latestText);
                }

            }

        });


    }



    public void setPeoplePendingAndAcceptedDataToClass(Map<String, RequestData> requestData) {
        searchUser.setEnabled(true);
        this.requestData = requestData;
    }


    private void searchUsers(String searchText) {

        Query query = ref
                .orderByChild(USER_NAME)
                .startAt(searchText)
                .endAt(searchText + "\uf8ff"); // prefix match

        Log.d("TAG", "searchUsers: ");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Log.d("TAG", "onDataChange: " + dataSnapshot);

                ArrayList<PeopleSearchData> resultList = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    PeopleSearchData data = new PeopleSearchData();
                    data.setAccountPrivate(Boolean.TRUE.equals(snapshot.child(IS_ACCOUNT_PRIVATE).getValue(Boolean.class)));
                    data.setUserPrimaryId(snapshot.getKey());
                    Log.d("TAG", "onDataChange: " + snapshot.getKey());

                    if (data.isAccountPrivate() || data.getUserPrimaryId().equals(FIREBASE_USER != null ? FIREBASE_USER.getUid() : null)) continue;

                    data.setName(snapshot.child(USER_NAME).getValue(String.class));
                    data.setEmail(snapshot.child(USER_EMAIL).getValue(String.class));
                    data.setProfilePic(snapshot.child(USER_PROFILE_PIC).getValue(String.class));
                    data.setPhoneNumber(snapshot.child(USER_PHONE_NUMBER).getValue(String.class));
                    data.setProfession(snapshot.child(USER_PROFESSION).getValue(String.class));
                    resultList.add(data);
                    Log.d("TAG", "onDataChange: " + data.getName());

                }

                recyclerView.setAdapter(new PeopleSearchAdapter(resultList, requestData));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("TAG", "onCancelled: " + databaseError);
            }
        });

    }



}
