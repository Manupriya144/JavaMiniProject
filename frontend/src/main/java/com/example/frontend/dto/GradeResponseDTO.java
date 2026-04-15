package com.example.frontend.dto;

public class GradeResponseDTO {
    private String studentId;
    private String courseId;
    private double totalMarks;
    private String grade;

    public GradeResponseDTO() {}

    public String getStudentId() {
        return studentId;
    }

    public String getCourseId() {
        return courseId;
    }

    public double getTotalMarks() {
        return totalMarks;
    }

    public String getGrade() {
        return grade;
    }
}
