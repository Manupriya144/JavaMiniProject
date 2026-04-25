package com.example.frontend.model;

public class LecturerCourseItem {
    private String courseId;
    private String courseCode;
    private String courseName;
    private int courseCredit;

    public LecturerCourseItem() {
    }

    public LecturerCourseItem(String courseId, String courseCode, String courseName) {
        this.courseId = courseId;
        this.courseCode = courseCode;
        this.courseName = courseName;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public int getCourseCredit() {
        return courseCredit;
    }

    public void setCourseCredit(int courseCredit) {
        this.courseCredit = courseCredit;
    }

    @Override
    public String toString() {
        if (courseCode != null && courseName != null) {
            return courseCode + " - " + courseName;
        }
        return courseName != null ? courseName : courseId;
    }
}