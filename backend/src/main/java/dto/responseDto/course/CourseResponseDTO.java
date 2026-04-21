package dto.responseDto.course;

public class CourseResponseDTO {
    private String courseId;
    private String courseCode;
    private String name;

    public CourseResponseDTO() {
    }

    public CourseResponseDTO(String courseId, String courseCode, String name) {
        this.courseId = courseId;
        this.courseCode = courseCode;
        this.name = name;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}