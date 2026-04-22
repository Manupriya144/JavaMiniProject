package com.example.frontend.model;

public class TechOfficerDashboardStats {
    private int totalStudents;
    private int attendanceSessions;
    private int medicalRecords;
    private int pendingApprovals;

    public int getTotalStudents() {
        return totalStudents;
    }

    public void setTotalStudents(int totalStudents) {
        this.totalStudents = totalStudents;
    }

    public int getAttendanceSessions() {
        return attendanceSessions;
    }

    public void setAttendanceSessions(int attendanceSessions) {
        this.attendanceSessions = attendanceSessions;
    }

    public int getMedicalRecords() {
        return medicalRecords;
    }

    public void setMedicalRecords(int medicalRecords) {
        this.medicalRecords = medicalRecords;
    }

    public int getPendingApprovals() {
        return pendingApprovals;
    }

    public void setPendingApprovals(int pendingApprovals) {
        this.pendingApprovals = pendingApprovals;
    }
}
