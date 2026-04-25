package com.example.frontend.model;

public class StudentEligibilityRes {
    private String courseId;
    private String courseCode;
    private String courseName;

    private double totalHours;
    private double attendedHours;
    private double medicalHours;
    private double finalAttendanceHours;
    private double attendancePercentage;
    private String attendanceStatus;

    private double caMaxMarks;
    private double caMarks;
    private double caPercentage;
    private String caStatus;

    private String finalEligibilityStatus;

    public StudentEligibilityRes() {}

    public String getCourseId() { return courseId; }
    public String getCourseCode() { return courseCode; }
    public String getCourseName() { return courseName; }
    public double getTotalHours() { return totalHours; }
    public double getAttendedHours() { return attendedHours; }
    public double getMedicalHours() { return medicalHours; }
    public double getFinalAttendanceHours() { return finalAttendanceHours; }
    public double getAttendancePercentage() { return attendancePercentage; }
    public String getAttendanceStatus() { return attendanceStatus; }
    public double getCaMaxMarks() { return caMaxMarks; }
    public double getCaMarks() { return caMarks; }
    public double getCaPercentage() { return caPercentage; }
    public String getCaStatus() { return caStatus; }
    public String getFinalEligibilityStatus() { return finalEligibilityStatus; }
}