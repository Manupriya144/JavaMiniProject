package com.example.frontend.dto;

public class UserResponseDTO {
    private String userId;
    private String username;
    private String email;
    private String role;
    private String contactNo;
    private String profilePicture;

    public UserResponseDTO() {
    }

    public UserResponseDTO(String userId, String username, String email, String role, String contactNo, String profilePicture) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.role = role;
        this.contactNo = contactNo;
        this.profilePicture = profilePicture;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getContactNo() {
        return contactNo;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }
}