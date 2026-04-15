package com.example.frontend.dto;

public class FinalMarksResponseDTO {
    private String studentId;
    private String courseId;
    private double marks;

    public FinalMarksResponseDTO() {}

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
