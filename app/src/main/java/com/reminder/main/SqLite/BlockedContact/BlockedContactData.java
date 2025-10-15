package com.reminder.main.SqLite.BlockedContact;

public class BlockedContactData {

    private String userName;
    private String userEmail;
    private String userProfilePic;
    private String userProfession;
    private String userAbout;
    private String userPrimaryId;

    public BlockedContactData() {

    }

    public BlockedContactData(String userPrimaryId) {
        this.userPrimaryId = userPrimaryId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserProfilePic() {
        return userProfilePic;
    }

    public void setUserProfilePic(String userProfilePic) {
        this.userProfilePic = userProfilePic;
    }

    public String getUserProfession() {
        return userProfession;
    }

    public void setUserProfession(String userProfession) {
        this.userProfession = userProfession;
    }

    public String getUserAbout() {
        return userAbout;
    }

    public void setUserAbout(String userAbout) {
        this.userAbout = userAbout;
    }

    public void setUserPrimaryId(String userPrimaryId) {
        this.userPrimaryId = userPrimaryId;
    }



    public String getUserPrimaryId() {
        return userPrimaryId;
    }


}
