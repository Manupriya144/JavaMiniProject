package dto.requestDto.attendance;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AddAttendanceRequestDTO {

    @JsonProperty("student_id")
    private String studentId;

    @JsonProperty("session_id")
    private Integer sessionId;

    @JsonProperty("status")
    private String status;

    @JsonProperty("hours_attended")
    private Double hoursAttended;

    public AddAttendanceRequestDTO() {
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public Integer getSessionId() {
        return sessionId;
    }

    public void setSessionId(Integer sessionId) {
        this.sessionId = sessionId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getHoursAttended() {
        return hoursAttended;
    }

    public void setHoursAttended(Double hoursAttended) {
        this.hoursAttended = hoursAttended;
    }
}
