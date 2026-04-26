package dto.requestDto.ca;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GetCourseCAReferenceRequestDTO {
    @JsonProperty("course_id")
    private String courseId;

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }
}
