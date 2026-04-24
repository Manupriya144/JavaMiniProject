package dto.requestDto.courseMeterial;

public class AddCourseMaterialReqDTO {
    private String courseId;
    private String title;
    private String filePath;

    public AddCourseMaterialReqDTO() {}

    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
}