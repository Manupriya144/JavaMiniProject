package com.example.frontend.dto;

public class AddLectureSessionResponseDTO {
    private boolean success;
    private String message;
    private Integer sessionId;
    private String courseId;
    private String sessionDate;
    private Double sessionHours;
    private String type;

    public AddLectureSessionResponseDTO() {
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getSessionId() {
        return sessionId;
    }

    public void setSessionId(Integer sessionId) {
        this.sessionId = sessionId;
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

    public Double getSessionHours() {
        return sessionHours;
    }

    public void setSessionHours(Double sessionHours) {
        this.sessionHours = sessionHours;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}