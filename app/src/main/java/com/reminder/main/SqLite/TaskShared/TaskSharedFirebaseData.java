package com.reminder.main.SqLite.TaskShared;

import java.util.ArrayList;

public class TaskSharedFirebaseData {
    long taskID;
    ArrayList<String> userPrimaryId;

    public TaskSharedFirebaseData(long taskID, ArrayList<String> userPrimaryId) {
        this.taskID = taskID;
        this.userPrimaryId = userPrimaryId;
    }


    public long getTaskID() {
        return taskID;
    }

    public void setTaskID(long taskID) {
        this.taskID = taskID;
    }

    public ArrayList<String> getUserPrimaryId() {
        return userPrimaryId;
    }

    public void setUserPrimaryId(ArrayList<String> userPrimaryId) {
        this.userPrimaryId = userPrimaryId;
    }
}
