package com.example.frontend.dto;

public class StudentCoursesRequestDTO {
    private String userId;

    public StudentCoursesRequestDTO() {}

    public StudentCoursesRequestDTO(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}

