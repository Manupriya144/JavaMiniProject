package com.example.frontend.model;

public class Grade {
    private String studentId;
    private String courseId;
    private double totalMarks;
    private String grade;

    public Grade() {}

    public Grade(String studentId, String courseId, double totalMarks, String grade) {
        this.studentId = studentId;
        this.courseId = courseId;
        this.totalMarks = totalMarks;
        this.grade = grade;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public double getTotalMarks() {
        return totalMarks;
    }

    public void setTotalMarks(double totalMarks) {
        this.totalMarks = totalMarks;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }
}
