package com.example.frontend.dto;

public class CourseMaterialRequestDTO {
    private String courseId;
    private String title;
    private String filePath;
    private String deadline;

    public CourseMaterialRequestDTO() {
    }

    public CourseMaterialRequestDTO(String courseId, String title, String filePath) {
        this.courseId = courseId;
        this.title = title;
        this.filePath = filePath;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }
}