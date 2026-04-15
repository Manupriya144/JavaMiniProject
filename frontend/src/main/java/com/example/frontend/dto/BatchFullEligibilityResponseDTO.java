package com.example.frontend.dto;

public class BatchFullEligibilityResponseDTO {
    private String studentId;
    private double attendance;
    private double caMarks;
    private boolean eligible;

    public BatchFullEligibilityResponseDTO() {}

    public String getStudentId() {
        return studentId;
    }

    public double getAttendance() {
        return attendance;
    }

    public double getCaMarks() {
        return caMarks;
    }

    public boolean isEligible() {
        return eligible;
    }
}
