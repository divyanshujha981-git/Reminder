package com.reminder.main.SqLite.TaskShared;

public class TaskSharedData {
    private String userPrimaryId;
    private String taskWebID;




    private byte sharedType;


     public TaskSharedData() {

    }

    public TaskSharedData(String userPrimaryId, String taskWebID) {
        this.userPrimaryId = userPrimaryId;
        this.taskWebID = taskWebID;
    }

    public String getUserPrimaryId() {
        return userPrimaryId;
    }

    public void setUserPrimaryId(String userPrimaryId) {
        this.userPrimaryId = userPrimaryId;
    }

    public String getTaskWebID() {
        return taskWebID;
    }

    public void setTaskWebID(String taskWebID) {
        this.taskWebID = taskWebID;
    }

    public byte getSharedType() {
        return sharedType;
    }

    public void setSharedType(byte sharedType) {
        this.sharedType = sharedType;
    }



}
