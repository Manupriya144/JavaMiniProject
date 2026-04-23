package model;

import java.time.LocalDate;

public class Session {
    private int sessionId;
    private String courseId;
    private LocalDate sessionDate;
    private double sessionHours;
    private String type;

    public Session() {}

    public Session(String courseId, LocalDate sessionDate, double sessionHours, String type) {
        this.courseId = courseId;
        this.sessionDate = sessionDate;
        this.sessionHours = sessionHours;
        this.type = type;
    }

    public Session(int sessionId, String courseId, LocalDate sessionDate, double sessionHours, String type) {
        this.sessionId = sessionId;
        this.courseId = courseId;
        this.sessionDate = sessionDate;
        this.sessionHours = sessionHours;
        this.type = type;
    }

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public LocalDate getSessionDate() {
        return sessionDate;
    }

    public void setSessionDate(LocalDate sessionDate) {
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