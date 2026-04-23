package com.example.frontend.dto;

public class AddLectureSessionRequestDTO {
    private String courseId;
    private String sessionDate;
    private double sessionHours;
    private String type;

    public AddLectureSessionRequestDTO() {
    }

    public AddLectureSessionRequestDTO(String courseId, String sessionDate, double sessionHours, String type) {
        this.courseId = courseId;
        this.sessionDate = sessionDate;
        this.sessionHours = sessionHours;
        this.type = type;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getSessionDate() {
        return sessionDate;
    }

    public void setSessionDate(String sessionDate) {
        this.sessionDate = sessionDate;
    }

    public double getSessionHours() {
        return sessionHours;
    }

    public void setSessionHours(double sessionHours) {
        this.sessionHours = sessionHours;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}