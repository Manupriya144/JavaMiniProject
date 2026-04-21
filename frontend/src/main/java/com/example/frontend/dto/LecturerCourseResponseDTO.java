package com.example.frontend.dto;

public class LecturerCourseResponseDTO {
    private boolean success;
    private String message;

    public LecturerCourseResponseDTO() {
    }

    public LecturerCourseResponseDTO(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}