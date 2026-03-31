package com.example.frontend.model;

public class BatchAttendanceSummaryRow {
    private String studentId;
    private String regNo;
    private String studentName;
    private int totalSessions;
    private int presentCount;
    private int absentCount;
    private double attendancePercentage;
    private double totalHoursAttended;

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getRegNo() {
        return regNo;
    }

    public void setRegNo(String regNo) {
        this.regNo = regNo;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public int getTotalSessions() {
        return totalSessions;
    }

    public void setTotalSessions(int totalSessions) {
        this.totalSessions = totalSessions;
    }

    public int getPresentCount() {
        return presentCount;
    }

    public void setPresentCount(int presentCount) {
        this.presentCount = presentCount;
    }

    public int getAbsentCount() {
        return absentCount;
    }

    public void setAbsentCount(int absentCount) {
        this.absentCount = absentCount;
    }

    public double getAttendancePercentage() {
        return attendancePercentage;
    }

    public void setAttendancePercentage(double attendancePercentage) {
        this.attendancePercentage = attendancePercentage;
    }

    public double getTotalHoursAttended() {
        return totalHoursAttended;
    }

    public void setTotalHoursAttended(double totalHoursAttended) {
        this.totalHoursAttended = totalHoursAttended;
    }
}

