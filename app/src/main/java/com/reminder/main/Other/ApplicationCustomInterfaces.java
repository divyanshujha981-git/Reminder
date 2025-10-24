package com.reminder.main.Other;

import android.widget.LinearLayout;

import com.google.android.material.card.MaterialCardView;
import com.reminder.main.SqLite.Request.RequestData;
import com.reminder.main.SqLite.Tasks.TaskData;
import com.reminder.main.SqLite.UserDetails.UserDetailsData;
import com.reminder.main.UserInterfaces.HomePage.Tasks.NavBarDateTemplate;
import com.reminder.main.UserInterfaces.PeoplePage.MainActivity.PeoplePendingOrAcceptedData;
import com.reminder.main.UserInterfaces.UserTaskInbox.MainActivity.UserTaskInboxData;

import java.util.ArrayList;
import java.util.Map;

public class ApplicationCustomInterfaces {

    public interface BottomNavItemCheck {
        void navItemChecked(int position);
    }


    public interface DateTime {
        void setTime(int[] timeArr, byte amPm);

        void setDate(int[] dateArr);
    }


    public interface DateTime2 {
        void setTime(int[] timeArr, byte amPm, EnableDateTimePicker context);

        void setDate(int[] dateArr, EnableDateTimePicker context);
    }


    public interface EnableDateTimePicker{
        void enable(boolean condition, long timeInMillis);
    }






    public interface BindView{
        void primaryViewBinding(LinearLayout view, TaskData itemView, int position);
        void secondaryViewBinding(MaterialCardView view, TaskData itemView, int position);
    }

    public interface RepeatStatus{
        void setRepeatStatus(byte status);
    }

    public interface FilterTask{
        void setFilteredTaskToUI(int adapterPosition);
    }




    public interface NestedScroll{
        void scrollIntercept(boolean con);
    }

    public interface DeleteTask{
        void deleteTask(int position);
    }



    public interface TaskSQLInterface {
        void setMainTaskData(ArrayList<TaskData> taskData);
        void setUpComingTask(TaskData tasks, int position);
        void setNavDateTask(ArrayList<NavBarDateTemplate> navDateArray);
        void setFilteredTask(ArrayList<ArrayList<TaskData>> navDateArray);
        void setPinnedTaskOnTop(boolean pinTaskOnTop, boolean isPinnedTaskAvailable);
    }


    public interface TaskInboxPeopleInterface {
        void getTaskInboxPeopleData(ArrayList<UserDetailsData> taskInboxPeopleData);
    }


    public interface AccountInterface {
        void getAccountData(Map<String, Object> accountData);
    }



    public interface AllowUserToNavigate {
        void authorized();
    }


    public interface ManipulateTask{
        void deleteTask();
        void pinTask();
        void markTaskDone();
        void lockTask();
        void reschedule();

        void unPinTask();
        void markTaskUnDone();
        void unlockTask();
    }





    public interface ContextualActionBar {

        void setContextualActionBarVisible(
                ContextualActionBarCallback contextualActionBarCallback,
                ManipulateTask manipulateTask
        );

        void setContextualActionBarInVisible();



        void changeTitle(String value);


    }

    public interface ContextualActionBarCallback {
        void selectAll();

        void unSelectAll(boolean actionBarDestroy);

        //void unSelectSome(ArrayList<Integer> indexes);
    }


    public interface StartSelection {
        void startSelection(int index, long taskID);
    }

    public interface CallBack {
        void callback(boolean value);
    }



    public interface PhoneAuthCallBack {
        void codeSent(boolean isCodeSent, String verificationId, String otp);
    }



    public interface SetCursorAt {
        void setCursorAt(int index);
    }

    public interface RefreshLayout {
        void refreshLayout(Class<?> cls);
    }


    public interface SignOut {
        void signOut(boolean isSignedOut);
    }

    public interface RequestStatusChangeCallback {
        void requestStatusChangeCallback(byte status, byte requestType);
        void deleteRequest();
        void doNotChangeRequest();
    }


    public interface PeopleData {
        void setPeoplePendingData(ArrayList<PeoplePendingOrAcceptedData> requestData);
        void setPeopleAcceptedData(ArrayList<PeoplePendingOrAcceptedData> requestData);
        void setPeoplePendingAndAcceptedData(Map<String, RequestData> requestData);
    }

    public interface TaskInboxInterface {
        void setTaskSentList(ArrayList<UserTaskInboxData> taskSentList);
        void setTaskReceivedList(ArrayList<UserTaskInboxData> taskSentList);
    }

}
