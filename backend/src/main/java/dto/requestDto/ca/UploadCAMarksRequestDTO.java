package dto.requestDto.ca;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UploadCAMarksRequestDTO {
    @JsonProperty("student_id")
    private String studentId;

    @JsonProperty("assessment_type_id")
    private Integer assessmentTypeId;

    @JsonProperty("marks")
    private Double marks;

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public Integer getAssessmentTypeId() {
        return assessmentTypeId;
    }

    public void setAssessmentTypeId(Integer assessmentTypeId) {
        this.assessmentTypeId = assessmentTypeId;
    }

    public Double getMarks() {
        return marks;
    }

    public void setMarks(Double marks) {
        this.marks = marks;
    }
}
