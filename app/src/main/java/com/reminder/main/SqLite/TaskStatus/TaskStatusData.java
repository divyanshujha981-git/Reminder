package com.reminder.main.SqLite.TaskStatus;

public class TaskStatusData {

    private String taskWebId;
    private String comment;
    private byte percentageComplete;


    public TaskStatusData() {

    }

    public TaskStatusData(String taskWebId, String comment, byte percentageComplete) {
        this.taskWebId = taskWebId;
        this.comment = comment;
        this.percentageComplete = percentageComplete;
    }




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


}
