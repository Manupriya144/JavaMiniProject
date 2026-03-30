package service.attendance;

import dao.attendance.AttendanceDAO;
import model.Attendance;

import java.util.List;
import java.util.Map;

public class AttendanceService {
    private final AttendanceDAO attendanceDAO;

    public AttendanceService(AttendanceDAO attendanceDAO) {
        this.attendanceDAO = attendanceDAO;
    }

    public Attendance addAttendance(String studentId, Integer sessionId, String status, Double hoursAttended) {
        if (studentId == null || studentId.isBlank() || sessionId == null || !isValidStatus(status)) {
            return null;
        }
        Attendance attendance = new Attendance(
                null,
                studentId.trim(),
                sessionId,
                normalizeStatus(status),
                hoursAttended
        );
        return attendanceDAO.addAttendance(attendance);
    }

    public boolean updateAttendance(Integer attendanceId, String status, Double hoursAttended) {
        if (attendanceId == null || !isValidStatus(status)) {
            return false;
        }
        return attendanceDAO.updateAttendance(attendanceId, normalizeStatus(status), hoursAttended);
    }

    public boolean deleteAttendance(Integer attendanceId) {
        if (attendanceId == null) {
            return false;
        }
        return attendanceDAO.deleteAttendance(attendanceId);
    }

    public Attendance getAttendanceById(Integer attendanceId) {
        if (attendanceId == null) {
            return null;
        }
        return attendanceDAO.getAttendanceById(attendanceId);
    }

    public List<Map<String, Object>> getStudentOptions() {
        return attendanceDAO.getStudentOptions();
    }

    public List<Map<String, Object>> getSessionOptions() {
        return attendanceDAO.getSessionOptions();
    }

    private boolean isValidStatus(String status) {
        if (status == null) {
            return false;
        }
        String normalized = status.trim();
        return "Present".equalsIgnoreCase(normalized) || "Absent".equalsIgnoreCase(normalized);
    }

    private String normalizeStatus(String status) {
        return "Present".equalsIgnoreCase(status.trim()) ? "Present" : "Absent";
    }
}
