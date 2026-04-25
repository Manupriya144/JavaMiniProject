package com.example.frontend.model;

public class StudentRegisteredCourse {
    private String courseId;
    private String courseCode;
    private String courseName;
    private int courseCredit;

    public StudentRegisteredCourse() {}

    public String getCourseId() { return courseId; }
    public String getCourseCode() { return courseCode; }
    public String getCourseName() { return courseName; }
    public int getCourseCredit() { return courseCredit; }

    @Override
    public String toString() {
        return courseCode + " - " + courseName;
    }
}