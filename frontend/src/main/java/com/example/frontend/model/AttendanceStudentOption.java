package com.example.frontend.model;

public class AttendanceStudentOption {
    private String userId;
    private String regNo;
    private String username;

    public AttendanceStudentOption(String userId, String regNo, String username) {
        this.userId = userId;
        this.regNo = regNo;
        this.username = username;
    }

    public String getUserId() {
        return userId;
    }

    public String getRegNo() {
        return regNo;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public String toString() {
        return regNo + " - " + username + " (" + userId + ")";
    }
}
