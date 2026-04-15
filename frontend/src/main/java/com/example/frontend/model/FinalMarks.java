package com.example.frontend.model;

public class FinalMarks {
    private String studentId;
    private String courseId;
    private double finalMarks;

    public FinalMarks() {}

    public FinalMarks(String studentId, String courseId, double finalMarks) {
        this.studentId = studentId;
        this.courseId = courseId;
        this.finalMarks = finalMarks;
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

    public double getFinalMarks() {
        return finalMarks;
    }

    public void setFinalMarks(double finalMarks) {
        this.finalMarks = finalMarks;
    }
}

