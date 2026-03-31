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

    public List<Map<String, Object>> getStudentAttendance(String studentId, String viewType) {
        if (studentId == null || studentId.isBlank()) {
            return List.of();
        }
        return attendanceDAO.getStudentAttendance(studentId.trim(), normalizeViewType(viewType));
    }

    public List<Map<String, Object>> getBatchAttendance(String batch, String viewType) {
        if (batch == null || batch.isBlank()) {
            return List.of();
        }
        return attendanceDAO.getBatchAttendance(batch.trim(), normalizeViewType(viewType));
    }

    public Map<String, Object> getStudentAttendanceSummary(String studentId, String viewType) {
        if (studentId == null || studentId.isBlank()) {
            return null;
        }
        return attendanceDAO.getStudentAttendanceSummary(studentId.trim(), normalizeViewType(viewType));
    }

    public List<Map<String, Object>> getBatchAttendanceSummary(String batch, String viewType) {
        if (batch == null || batch.isBlank()) {
            return List.of();
        }
        return attendanceDAO.getBatchAttendanceSummary(batch.trim(), normalizeViewType(viewType));
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

    private String normalizeViewType(String viewType) {
        if (viewType == null || viewType.isBlank()) {
            return "Combined";
        }
        if ("Theory".equalsIgnoreCase(viewType.trim())) {
            return "Theory";
        }
        if ("Practical".equalsIgnoreCase(viewType.trim())) {
            return "Practical";
        }
        return "Combined";
    }
}
