package model;

public class Attendance {
    private Integer attendanceId;
    private String studentId;
    private Integer sessionId;
    private String status;
    private Double hoursAttended;

    public Attendance() {
    }

    public Attendance(Integer attendanceId, String studentId, Integer sessionId, String status, Double hoursAttended) {
        this.attendanceId = attendanceId;
        this.studentId = studentId;
        this.sessionId = sessionId;
        this.status = status;
        this.hoursAttended = hoursAttended;
    }

    public Integer getAttendanceId() {
        return attendanceId;
    }

    public void setAttendanceId(Integer attendanceId) {
        this.attendanceId = attendanceId;
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
