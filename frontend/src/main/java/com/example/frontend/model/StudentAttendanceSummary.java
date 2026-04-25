package com.example.frontend.model;

public class StudentAttendanceSummary {
    private String courseId;
    private String courseCode;
    private String courseName;
    private int totalSessions;
    private double totalHours;
    private double attendedHours;
    private double attendancePercentage;

    public StudentAttendanceSummary() {}

    public String getCourseId() { return courseId; }
    public String getCourseCode() { return courseCode; }
    public String getCourseName() { return courseName; }
    public int getTotalSessions() { return totalSessions; }
    public double getTotalHours() { return totalHours; }
    public double getAttendedHours() { return attendedHours; }
    public double getAttendancePercentage() { return attendancePercentage; }
}