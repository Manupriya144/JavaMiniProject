package dto.requestDto.attendance;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GetAttendanceByIdRequestDTO {

    @JsonProperty("attendance_id")
    private Integer attendanceId;

    public GetAttendanceByIdRequestDTO() {
    }

    public Integer getAttendanceId() {
        return attendanceId;
    }

    public void setAttendanceId(Integer attendanceId) {
        this.attendanceId = attendanceId;
    }
}
