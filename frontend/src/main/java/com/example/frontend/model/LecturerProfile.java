package com.example.frontend.model;

public class LecturerProfile {
    private String userId;
    private String username;
    private String email;
    private String contactNumber;
    private String profilePicture;
    private String specialization;
    private String designation;

    public LecturerProfile() {}

    public String getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getContactNumber() { return contactNumber; }
    public String getProfilePicture() { return profilePicture; }
    public String getSpecialization() { return specialization; }
    public String getDesignation() { return designation; }

    public void setProfilePicture(String profilePicture) { this.profilePicture = profilePicture; }
    public void setDesignation(String designation) { this.designation = designation; }
}