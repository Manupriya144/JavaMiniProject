package model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class CourseMaterial {

    private Integer materialId;
    private String courseId;
    private String lecturerId;
    private String title;
    private String filePath;
    private LocalDate deadline;
    private LocalDateTime uploadedAt;

    public CourseMaterial() {
    }

    public CourseMaterial(Integer materialId, String courseId, String lecturerId, String title, String filePath, LocalDate deadline, LocalDateTime uploadedAt) {
        this.materialId = materialId;
        this.courseId = courseId;
        this.lecturerId = lecturerId;
        this.title = title;
        this.filePath = filePath;
        this.deadline = deadline;
        this.uploadedAt = uploadedAt;
    }

    public CourseMaterial(String courseId, String lecturerId, String title, String filePath, LocalDate deadline) {
        this.courseId = courseId;
        this.lecturerId = lecturerId;
        this.title = title;
        this.filePath = filePath;
        this.deadline = deadline;
    }

    public Integer getMaterialId() {
        return materialId;
    }

    public void setMaterialId(Integer materialId) {
        this.materialId = materialId;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getLecturerId() {
        return lecturerId;
    }

    public void setLecturerId(String lecturerId) {
        this.lecturerId = lecturerId;
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

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }
}