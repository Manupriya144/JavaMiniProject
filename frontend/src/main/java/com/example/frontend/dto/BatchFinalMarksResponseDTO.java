package com.example.frontend.dto;

public class BatchFinalMarksResponseDTO {
    private String studentId;
    private String courseId;
    private double marks;

    public BatchFinalMarksResponseDTO() {}

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

