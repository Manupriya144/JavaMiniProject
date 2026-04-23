package com.example.frontend.dto;

public class AdminStatsResponseDTO {
    private boolean success;
    private String message;

    private int totalUsers;
    private int totalStudents;
    private int totalLecturers;
    private int totalTechOfficers;
    private int totalCourses;

    public AdminStatsResponseDTO() {
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public int getTotalUsers() {
        return totalUsers;
    }

    public int getTotalStudents() {
        return totalStudents;
    }

    public int getTotalLecturers() {
        return totalLecturers;
    }

    public int getTotalTechOfficers() {
        return totalTechOfficers;
    }

    public int getTotalCourses() {
        return totalCourses;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTotalUsers(int totalUsers) {
        this.totalUsers = totalUsers;
    }

    public void setTotalStudents(int totalStudents) {
        this.totalStudents = totalStudents;
    }

    public void setTotalLecturers(int totalLecturers) {
        this.totalLecturers = totalLecturers;
    }

    public void setTotalTechOfficers(int totalTechOfficers) {
        this.totalTechOfficers = totalTechOfficers;
    }

    public void setTotalCourses(int totalCourses) {
        this.totalCourses = totalCourses;
    }
}