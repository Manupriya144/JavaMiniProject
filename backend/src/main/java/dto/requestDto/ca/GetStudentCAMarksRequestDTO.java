package dto.requestDto.ca;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GetStudentCAMarksRequestDTO {
    @JsonProperty("student_id")
    private String studentId;

    @JsonProperty("course_id")
    private String courseId;

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }
}
