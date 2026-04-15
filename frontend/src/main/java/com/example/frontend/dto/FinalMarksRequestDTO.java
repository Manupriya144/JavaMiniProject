package com.example.frontend.dto;


public class FinalMarksRequestDTO {
    private String studentId;
    private String courseId;
    private double marks;

    public FinalMarksRequestDTO() {}

    public FinalMarksRequestDTO(String studentId, String courseId, double marks) {
        this.studentId = studentId;
        this.courseId = courseId;
        this.marks = marks;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getCourseId() {
        return courseId;
    }

    public double getMarks() {
        return marks;
    }
}
