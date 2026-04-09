package service.attendance;

import dao.attendance.AttendanceDAO;
import model.Attendance;

import java.util.ArrayList;
import java.util.HashMap;
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

    private static final double ELIGIBILITY_THRESHOLD = 80.0;
    private static final double MEDICAL_BONUS_PERCENT = 20.0;

    public Map<String, Object> checkAttendanceEligibility(String studentId, String viewType) {
        if (studentId == null || studentId.isBlank()) {
            return null;
        }
        String vt = normalizeViewType(viewType);
        Map<String, Object> summary = attendanceDAO.getStudentAttendanceSummary(studentId.trim(), vt);
        boolean hasMedical = attendanceDAO.hasAttendanceMedicalRecord(studentId.trim());
        if (summary == null) {
            Map<String, Object> empty = new HashMap<>();
            empty.put("studentId", studentId.trim());
            empty.put("viewType", vt);
            empty.put("totalSessions", 0);
            empty.put("attendancePercentage", 0.0);
            return enrichEligibility(empty, hasMedical);
        }
        return enrichEligibility(summary, hasMedical);
    }

    public List<Map<String, Object>> getBatchAttendanceEligibilityReport(String batch, String viewType) {
        if (batch == null || batch.isBlank()) {
            return List.of();
        }
        String vt = normalizeViewType(viewType);
        List<Map<String, Object>> raw = attendanceDAO.getBatchAttendanceEligibilityReport(batch.trim(), vt);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> row : raw) {
            int medicalCount = ((Number) row.getOrDefault("medicalCount", 0)).intValue();
            boolean hasMedical = medicalCount > 0;
            row.remove("medicalCount");
            result.add(enrichEligibility(row, hasMedical));
        }
        return result;
    }

    private Map<String, Object> enrichEligibility(Map<String, Object> row, boolean hasMedical) {
        double rawPct = ((Number) row.getOrDefault("attendancePercentage", 0.0)).doubleValue();
        // Apply medical grace only when student is below threshold and has attendance medical.
        double bonusPct = (hasMedical && rawPct + 1e-9 < ELIGIBILITY_THRESHOLD) ? MEDICAL_BONUS_PERCENT : 0.0;
        double effectivePct = Math.min(100.0, rawPct + bonusPct);

        String category = categorizePercentage(effectivePct);
        boolean eligible = effectivePct + 1e-9 >= ELIGIBILITY_THRESHOLD;

        String baseLabel = switch (category) {
            case "Above80" -> "Above 80%";
            case "Exactly80" -> "Exactly 80%";
            default -> "Below 80%";
        };
        String scenarioLabel = hasMedical
                ? baseLabel + " (Medical +" + (int) MEDICAL_BONUS_PERCENT + "%)"
                : baseLabel;

        row.put("eligible", eligible);
        row.put("thresholdPercent", ELIGIBILITY_THRESHOLD);
        row.put("eligibilityCategory", category);
        row.put("hasMedical", hasMedical);
        row.put("rawAttendancePercentage", round2(rawPct));
        row.put("medicalBonusPercent", bonusPct);
        row.put("effectiveAttendancePercentage", round2(effectivePct));
        row.put("attendancePercentage", round2(effectivePct));
        row.put("scenarioLabel", scenarioLabel);
        return row;
    }

    private String categorizePercentage(double pct) {
        if (pct > 80.0 + 1e-6) {
            return "Above80";
        }
        if (pct + 1e-6 >= 80.0) {
            return "Exactly80";
        }
        return "Below80";
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

    private double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
