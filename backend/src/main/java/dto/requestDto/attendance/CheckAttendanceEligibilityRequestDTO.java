package dto.requestDto.attendance;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CheckAttendanceEligibilityRequestDTO {
    @JsonProperty("student_id")
    private String studentId;

    @JsonProperty("view_type")
    private String viewType;

    public CheckAttendanceEligibilityRequestDTO() {
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getViewType() {
        return viewType;
    }

    public void setViewType(String viewType) {
        this.viewType = viewType;
    }
}
