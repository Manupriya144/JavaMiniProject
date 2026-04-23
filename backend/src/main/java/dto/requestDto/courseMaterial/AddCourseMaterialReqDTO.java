package dto.requestDto.courseMaterial;

public class AddCourseMaterialReqDTO {
    private String courseId;
    private String title;
    private String filePath;
    private String lecturerId;

    public AddCourseMaterialReqDTO() {
    }

    public AddCourseMaterialReqDTO(String courseId, String title, String filePath, String lecturerId) {
        this.courseId = courseId;
        this.title = title;
        this.filePath = filePath;
        this.lecturerId = lecturerId;
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

    public String getLecturerId() {
        return lecturerId;
    }

    public void setLecturerId(String lecturerId) {
        this.lecturerId = lecturerId;
    }
}