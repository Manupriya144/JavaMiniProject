package dto.requestDto.medical;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GetMedicalEligibleCoursesRequestDTO {
    @JsonProperty("student_id")
    private String studentId;

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }
}
