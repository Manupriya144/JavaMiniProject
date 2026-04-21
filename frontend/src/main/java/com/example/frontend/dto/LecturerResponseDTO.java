package com.example.frontend.dto;

public class LecturerResponseDTO {
    private String userId;
    private String username;
    private String email;

    public LecturerResponseDTO() {
    }

    public LecturerResponseDTO(String userId, String username, String email) {
        this.userId = userId;
        this.username = username;
        this.email = email;
    }

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return username + " (" + userId + ")";
    }
}