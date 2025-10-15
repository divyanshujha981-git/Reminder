package com.reminder.main.SqLite.Request;

public class RequestData {
    private String userPrimaryId;
    private byte status;
    private byte requestType;

    public RequestData() {
;
    }

    public RequestData(String userPrimaryId, byte requestType, byte status) {
        this.userPrimaryId = userPrimaryId;
        this.requestType = requestType;
        this.status = status;
    }


    public String getUserPrimaryId() {
        return userPrimaryId;
    }

    public void setUserPrimaryId(String userPrimaryId) {
        this.userPrimaryId = userPrimaryId;
    }

    public byte getRequestType() {
        return requestType;
    }

    public void setRequestType(byte requestType) {
        this.requestType = requestType;
    }

    public byte getStatus() {
        return status;
    }

    public void setStatus(byte status) {
        this.status = status;
    }





}
