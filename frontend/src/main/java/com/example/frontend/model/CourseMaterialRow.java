package com.example.frontend.model;

public class CourseMaterialRow {
    private final int materialId;
    private final String title;
    private final String filePath;
    private final String deadline;
    private final String uploadedAt;

    public CourseMaterialRow(int materialId, String title, String filePath, String deadline, String uploadedAt) {
        this.materialId = materialId;
        this.title = title;
        this.filePath = filePath;
        this.deadline = deadline;
        this.uploadedAt = uploadedAt;
    }

    public int getMaterialId() {
        return materialId;
    }

    public String getTitle() {
        return title;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getDeadline() {
        return deadline;
    }

    public String getUploadedAt() {
        return uploadedAt;
    }
}