package model;



public class CourseMaterial {
    private int materialId;
    private String courseId;
    private String lecturerId;
    private String title;
    private String filePath;
    private String uploadedAt;

    public CourseMaterial() {}

    public CourseMaterial(int materialId, String courseId, String lecturerId,
                          String title, String filePath, String uploadedAt) {
        this.materialId = materialId;
        this.courseId = courseId;
        this.lecturerId = lecturerId;
        this.title = title;
        this.filePath = filePath;
        this.uploadedAt = uploadedAt;
    }

    public int getMaterialId() { return materialId; }
    public void setMaterialId(int materialId) { this.materialId = materialId; }

    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }

    public String getLecturerId() { return lecturerId; }
    public void setLecturerId(String lecturerId) { this.lecturerId = lecturerId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public String getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(String uploadedAt) { this.uploadedAt = uploadedAt; }
}