package com.reminder.main.SqLite.TaskStatus;

public class TaskStatusData {

    private String taskWebId;
    private String comment;
    private String userPrimaryId;
    private byte downloaded;
    private byte percentageComplete;



    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getTaskWebId() {
        return taskWebId;
    }

    public void setTaskWebId(String taskWebId) {
        this.taskWebId = taskWebId;
    }

    public byte getPercentageComplete() {
        return percentageComplete;
    }

    public void setPercentageComplete(byte percentageComplete) {
        this.percentageComplete = percentageComplete;
    }


    public String getUserPrimaryId() {
        return userPrimaryId;
    }


    public void setUserPrimaryId(String userPrimaryId) {
        this.userPrimaryId = userPrimaryId;
    }

    public byte getDownloaded() {
        return downloaded;
    }

    public void setDownloaded(byte downloaded) {
        this.downloaded = downloaded;
    }


}
