package service.attendance;

import dao.attendance.AttendanceDAO;
import dto.responseDto.attendance.StudentAttendanceSummaryDTO;
import model.Attendance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AttendanceService {
    private final AttendanceDAO attendanceDAO;
    private static final int THEORY_SESSION_COUNT = 15;
    private static final int PRACTICAL_SESSION_COUNT = 15;
    private static final double HOURS_PER_THEORY_SESSION = 2.0;
    private static final double HOURS_PER_PRACTICAL_SESSION = 2.0;

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
    public List<Map<String, Object>> getStudentsWithAttendance() {
        return attendanceDAO.getStudentsWithAttendance();
    }

    public List<Map<String, Object>> getMedicalEligibleCourseIds(String studentUserId) {
        if (studentUserId == null || studentUserId.isBlank()) {
            return List.of();
        }
        return attendanceDAO.getCourseIdsForStudentDepartmentAttendance(studentUserId.trim());
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
        String normalizedViewType = normalizeViewType(viewType);
        Map<String, Object> summary = attendanceDAO.getStudentAttendanceSummary(studentId.trim(), normalizedViewType);
        if (summary == null) {
            return null;
        }
        applySessionAssumptions(summary, normalizedViewType);
        return summary;
    }

    public List<Map<String, Object>> getBatchAttendanceSummary(String batch, String viewType) {
        if (batch == null || batch.isBlank()) {
            return List.of();
        }
        String normalizedViewType = normalizeViewType(viewType);
        List<Map<String, Object>> rows = attendanceDAO.getBatchAttendanceSummary(batch.trim(), normalizedViewType);
        for (Map<String, Object> row : rows) {
            applySessionAssumptions(row, normalizedViewType);
        }
        return rows;
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
            empty.put("presentCount", 0);
            empty.put("attendancePercentage", 0.0);
            applySessionAssumptions(empty, vt);
            return enrichEligibility(empty, hasMedical);
        }
        applySessionAssumptions(summary, vt);
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
            applySessionAssumptions(row, vt);
            result.add(enrichEligibility(row, hasMedical));
        }
        return result;
    }

    private void applySessionAssumptions(Map<String, Object> row, String viewType) {
        int expectedSessions = getExpectedSessions(viewType);
        int presentCount = ((Number) row.getOrDefault("presentCount", 0)).intValue();
        int absentCount = Math.max(0, expectedSessions - presentCount);
        double attendancePercentage = expectedSessions == 0 ? 0.0 : (presentCount * 100.0) / expectedSessions;
        double hoursPerSession = "Practical".equalsIgnoreCase(viewType) ? HOURS_PER_PRACTICAL_SESSION : HOURS_PER_THEORY_SESSION;
        if ("Combined".equalsIgnoreCase(viewType)) {
            hoursPerSession = (HOURS_PER_THEORY_SESSION + HOURS_PER_PRACTICAL_SESSION) / 2.0;
        }
        double totalHoursAttended = presentCount * hoursPerSession;

        row.put("totalSessions", expectedSessions);
        row.put("absentCount", absentCount);
        row.put("attendancePercentage", round2(attendancePercentage));
        row.put("totalHoursAttended", round2(totalHoursAttended));
        row.put("sessionHours", round2(hoursPerSession));
    }

    private int getExpectedSessions(String viewType) {
        if ("Theory".equalsIgnoreCase(viewType)) {
            return THEORY_SESSION_COUNT;
        }
        if ("Practical".equalsIgnoreCase(viewType)) {
            return PRACTICAL_SESSION_COUNT;
        }
        return THEORY_SESSION_COUNT + PRACTICAL_SESSION_COUNT;
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
                ? baseLabel + " (With Approved Medical)"
                : baseLabel;
        String eligibilityStatus = eligible ? "Eligible" : "Not Eligible";
        String ruleNote = eligible
                ? "Meets minimum attendance eligibility (>= 80%)."
                : "Below minimum attendance eligibility (80%).";

        row.put("eligible", eligible);
        row.put("thresholdPercent", ELIGIBILITY_THRESHOLD);
        row.put("eligibilityCategory", category);
        row.put("hasMedical", hasMedical);
        row.put("rawAttendancePercentage", round2(rawPct));
        row.put("medicalBonusPercent", bonusPct);
        row.put("effectiveAttendancePercentage", round2(effectivePct));
        row.put("attendancePercentage", round2(effectivePct));
        row.put("scenarioLabel", scenarioLabel);
        row.put("eligibilityStatus", eligibilityStatus);
        row.put("ruleNote", ruleNote);
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

    public List<StudentAttendanceSummaryDTO> GetStudentAttendanceSummaryById(String id){
        return attendanceDAO.getStudentAttendanceSummaryById(id);
    }
}
