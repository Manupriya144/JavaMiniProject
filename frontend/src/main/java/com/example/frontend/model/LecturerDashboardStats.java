package com.example.frontend.model;

public class LecturerDashboardStats {
    private int myCourses;
    private int myStudents;
    private int eligibleStudents;
    private int pendingMarks;

    public LecturerDashboardStats() {}

    public int getMyCourses() { return myCourses; }
    public void setMyCourses(int myCourses) { this.myCourses = myCourses; }

    public int getMyStudents() { return myStudents; }
    public void setMyStudents(int myStudents) { this.myStudents = myStudents; }

    public int getEligibleStudents() { return eligibleStudents; }
    public void setEligibleStudents(int eligibleStudents) { this.eligibleStudents = eligibleStudents; }

    public int getPendingMarks() { return pendingMarks; }
    public void setPendingMarks(int pendingMarks) { this.pendingMarks = pendingMarks; }
}