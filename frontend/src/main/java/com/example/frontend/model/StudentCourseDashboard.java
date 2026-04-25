package com.example.frontend.model;

public class StudentCourseDashboard {
    private String courseId;
    private String courseCode;
    private String courseName;
    private int courseCredit;
    private String grade;

    public StudentCourseDashboard() {}

    public String getCourseId() { return courseId; }
    public String getCourseCode() { return courseCode; }
    public String getCourseName() { return courseName; }
    public int getCourseCredit() { return courseCredit; }
    public String getGrade() { return grade; }

    public void setCourseId(String courseId) { this.courseId = courseId; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    public void setCourseCredit(int courseCredit) { this.courseCredit = courseCredit; }
    public void setGrade(String grade) { this.grade = grade; }
}