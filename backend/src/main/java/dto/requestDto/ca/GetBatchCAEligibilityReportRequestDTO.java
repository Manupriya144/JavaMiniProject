package dto.requestDto.ca;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GetBatchCAEligibilityReportRequestDTO {
    @JsonProperty("batch")
    private String batch;

    @JsonProperty("course_id")
    private String courseId;

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }
}
