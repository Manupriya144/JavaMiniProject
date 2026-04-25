package com.example.frontend.service;

import com.example.frontend.dto.CourseAllResponseDTO;
import com.example.frontend.network.ServerClient;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Exam eligibility: attendance ≥ 80% (combined sessions) and CA ≥ 40% on each enrolled module.
 */
public class StudentExamEligibilityService {

    public static final double MIN_ATTENDANCE_PERCENT = 80.0;
    public static final double MIN_CA_PERCENT = 40.0;

    private final AttendanceService attendanceService;
    private final CAMarkService caMarkService;
    private final CourseService courseService;

    public StudentExamEligibilityService(ServerClient client) {
        this.attendanceService = new AttendanceService(client);
        this.caMarkService = new CAMarkService(client);
        this.courseService = new CourseService(client);
    }

    public ExamEligibilityResult evaluate(String userId) {
        if (userId == null || userId.isBlank()) {
            return ExamEligibilityResult.empty("Not signed in.");
        }

        List<CourseAllResponseDTO> courses = courseService.getStudentCourses(userId);
        if (courses == null) {
            courses = List.of();
        }

        Double attendancePct = readAttendancePercentage(userId);
        boolean attendanceLoaded = attendancePct != null;
        boolean attendanceOk = attendanceLoaded && attendancePct + 1e-6 >= MIN_ATTENDANCE_PERCENT;

        List<CourseEligibilityRow> rows = new ArrayList<>();
        boolean allCaOk = true;

        for (CourseAllResponseDTO c : courses) {
            String cid = c.getCourseId();
            if (cid == null || cid.isBlank()) {
                continue;
            }
            CaOutcome ca = readCaEligibility(userId, cid);
            String code = c.getCourseCode();
            rows.add(new CourseEligibilityRow(
                    code == null || code.isBlank() ? cid : code,
                    cid,
                    ca.caPercentage(),
                    ca.eligible(),
                    ca.responseOk()
            ));
            if (!ca.eligible()) {
                allCaOk = false;
            }
        }

        if (courses.isEmpty()) {
            allCaOk = true;
        }

        boolean eligible = attendanceLoaded && attendanceOk && allCaOk;

        String shortMessage = buildShortMessage(attendanceLoaded, attendancePct, attendanceOk, rows, allCaOk, eligible);
        String detailMessage = buildDetailMessage(attendanceLoaded, attendancePct, attendanceOk, rows);

        return new ExamEligibilityResult(
                attendanceLoaded,
                eligible,
                attendanceOk,
                attendancePct,
                rows,
                shortMessage,
                detailMessage
        );
    }

    private Double readAttendancePercentage(String userId) {
        JsonNode res = attendanceService.getStudentAttendanceSummary(userId, "Combined");
        if (res == null || !res.path("success").asBoolean(false) || !res.has("data")) {
            return null;
        }
        JsonNode data = res.get("data");
        if (data == null || !data.isObject()) {
            return null;
        }
        if (!data.has("attendancePercentage")) {
            return null;
        }
        double v = data.path("attendancePercentage").asDouble(Double.NaN);
        return Double.isNaN(v) ? null : v;
    }

    private CaOutcome readCaEligibility(String userId, String courseId) {
        JsonNode root = caMarkService.checkCAEligibility(userId, courseId);
        if (root != null && root.path("success").asBoolean(false) && root.has("data") && root.get("data").isObject()) {
            JsonNode d = root.get("data");
            double pct = d.path("caPercentage").asDouble(0.0);
            boolean ok = d.path("eligible").asBoolean(false);
            return new CaOutcome(pct, ok, true);
        }
        return new CaOutcome(0.0, false, false);
    }

    private String buildShortMessage(
            boolean attendanceLoaded,
            Double attendancePct,
            boolean attendanceOk,
            List<CourseEligibilityRow> rows,
            boolean allCaOk,
            boolean eligible
    ) {
        if (!attendanceLoaded) {
            return "Could not load attendance. Check your connection or try again later.";
        }
        if (eligible) {
            return String.format(Locale.ROOT,
                    "You meet exam rules: attendance %.0f%% (≥ %.0f%%) and CA ≥ %.0f%% on all listed modules.",
                    attendancePct, MIN_ATTENDANCE_PERCENT, MIN_CA_PERCENT);
        }
        StringBuilder sb = new StringBuilder();
        if (!attendanceOk) {
            sb.append(String.format(Locale.ROOT,
                    "Attendance %.0f%% is below %.0f%%. ",
                    attendancePct, MIN_ATTENDANCE_PERCENT));
        }
        if (!allCaOk) {
            sb.append("One or more modules are below CA ").append((int) MIN_CA_PERCENT).append("%.");
        }
        return sb.toString().trim();
    }

    private String buildDetailMessage(
            boolean attendanceLoaded,
            Double attendancePct,
            boolean attendanceOk,
            List<CourseEligibilityRow> rows
    ) {
        if (!attendanceLoaded) {
            return "Attendance summary could not be loaded from the server.";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(String.format(Locale.ROOT,
                "Attendance (combined): %.1f%% — %s (need ≥ %.0f%%).%n",
                attendancePct,
                attendanceOk ? "OK" : "not met",
                MIN_ATTENDANCE_PERCENT));
        if (rows.isEmpty()) {
            sb.append("No enrolled modules were returned for your level; CA is not checked per course.");
        } else {
            sb.append("CA per module (need ≥ ").append((int) MIN_CA_PERCENT).append("%):%n");
            for (CourseEligibilityRow r : rows) {
                sb.append(String.format(Locale.ROOT, "  • %s: %.1f%% — %s%n",
                        r.courseCode(),
                        r.caPercentage(),
                        r.caEligible() ? "OK" : "not met"));
            }
        }
        return sb.toString().trim();
    }

    private record CaOutcome(double caPercentage, boolean eligible, boolean responseOk) {}

    public record CourseEligibilityRow(
            String courseCode,
            String courseId,
            double caPercentage,
            boolean caEligible,
            boolean responseOk
    ) {}

    public record ExamEligibilityResult(
            boolean attendanceDataLoaded,
            boolean fullyEligible,
            boolean attendanceMeetsThreshold,
            Double attendancePercentage,
            List<CourseEligibilityRow> courseRows,
            String dashboardSummary,
            String detailExplanation
    ) {
        static ExamEligibilityResult empty(String message) {
            return new ExamEligibilityResult(
                    false,
                    false,
                    false,
                    null,
                    List.of(),
                    message,
                    message
            );
        }
    }
}
