package dao.eligibility;

import dto.responseDto.eligibility.StudentEligibilityDTO;
import dto.responseDto.student.StudentEligibilityResDTO;
import utility.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class StudentEligibilityDAO {

    public List<StudentEligibilityDTO> getEligibilityByCourse(String lecturerId, String courseId) {
        List<StudentEligibilityDTO> list = new ArrayList<>();

        String sql = """
                SELECT 
                    s.user_id AS student_id,
                    s.reg_no,
                    u.username AS student_name,
                    c.course_id,
                    c.course_code,
                    c.name AS course_name,
                    COALESCE(total_sessions.total_hours, 0) AS total_hours,
                    COALESCE(attended.attended_hours, 0) AS attended_hours
                FROM course_registration cr
                INNER JOIN students s ON cr.student_id = s.user_id
                INNER JOIN users u ON s.user_id = u.user_id
                INNER JOIN course c ON cr.course_id = c.course_id
                INNER JOIN lecturer_course lc ON lc.course_id = c.course_id
                LEFT JOIN (
                    SELECT course_id, SUM(session_hours) AS total_hours
                    FROM session
                    GROUP BY course_id
                ) total_sessions ON total_sessions.course_id = c.course_id
                LEFT JOIN (
                    SELECT 
                        a.student_id,
                        se.course_id,
                        SUM(a.hours_attended) AS attended_hours
                    FROM attendance a
                    INNER JOIN session se ON a.session_id = se.session_id
                    WHERE a.status = 'Present'
                    GROUP BY a.student_id, se.course_id
                ) attended ON attended.student_id = s.user_id 
                           AND attended.course_id = c.course_id
                WHERE lc.lecturer_id = ?
                  AND c.course_id = ?
                ORDER BY s.reg_no
                """;

        try (Connection con = DataSource.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, lecturerId);
            ps.setString(2, courseId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                double totalHours = rs.getDouble("total_hours");
                double attendedHours = rs.getDouble("attended_hours");

                double percentage = 0.0;
                if (totalHours > 0) {
                    percentage = (attendedHours / totalHours) * 100.0;
                }

                percentage = Math.round(percentage * 100.0) / 100.0;

                String status = percentage >= 80.0 ? "Eligible" : "Not Eligible";

                StudentEligibilityDTO dto = new StudentEligibilityDTO(
                        rs.getString("student_id"),
                        rs.getString("reg_no"),
                        rs.getString("student_name"),
                        rs.getString("course_id"),
                        rs.getString("course_code"),
                        rs.getString("course_name"),
                        totalHours,
                        attendedHours,
                        percentage,
                        status
                );

                list.add(dto);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<StudentEligibilityResDTO> getStudentOwnEligibility(String studentId) {
        List<StudentEligibilityResDTO> list = new ArrayList<>();

        String sql = """
                SELECT 
                    c.course_id,
                    c.course_code,
                    c.name AS course_name,
                    COALESCE(total_sessions.total_hours, 0) AS total_hours,
                    COALESCE(attended.attended_hours, 0) AS attended_hours
                FROM course_registration cr
                INNER JOIN course c ON cr.course_id = c.course_id

                LEFT JOIN (
                    SELECT course_id, SUM(session_hours) AS total_hours
                    FROM session
                    GROUP BY course_id
                ) total_sessions 
                    ON total_sessions.course_id = c.course_id

                LEFT JOIN (
                    SELECT 
                        a.student_id,
                        se.course_id,
                        SUM(a.hours_attended) AS attended_hours
                    FROM attendance a
                    INNER JOIN session se ON a.session_id = se.session_id
                    WHERE a.status = 'Present'
                    GROUP BY a.student_id, se.course_id
                ) attended 
                    ON attended.student_id = cr.student_id
                   AND attended.course_id = c.course_id

                WHERE cr.student_id = ?
                ORDER BY c.course_code
                """;

        try (Connection con = DataSource.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, studentId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String courseId = rs.getString("course_id");

                double totalHours = rs.getDouble("total_hours");
                double attendedHours = rs.getDouble("attended_hours");

                double medicalHours = calculateApprovedMedicalHours(con, studentId, courseId);

                double finalAttendanceHours = attendedHours + medicalHours;

                if (finalAttendanceHours > totalHours) {
                    finalAttendanceHours = totalHours;
                }

                double attendancePercentage = totalHours > 0
                        ? (finalAttendanceHours / totalHours) * 100.0
                        : 0.0;

                attendancePercentage = round(attendancePercentage);

                String attendanceStatus = attendancePercentage >= 80.0
                        ? "Eligible"
                        : "Not Eligible";

                double caMaxMarks = getCourseCAMaxMarks(con, courseId);

                double caMarks = calculateStudentCAMarks(
                        con,
                        studentId,
                        courseId,
                        caMaxMarks
                );

                double caPercentage = caMaxMarks > 0
                        ? (caMarks / caMaxMarks) * 100.0
                        : 0.0;

                caPercentage = round(caPercentage);

                String caStatus = caPercentage >= 50.0
                        ? "Eligible"
                        : "Not Eligible";

                String finalStatus =
                        attendanceStatus.equals("Eligible") &&
                                caStatus.equals("Eligible")
                                ? "Eligible"
                                : "Not Eligible";

                StudentEligibilityResDTO dto = new StudentEligibilityResDTO();

                dto.setCourseId(courseId);
                dto.setCourseCode(rs.getString("course_code"));
                dto.setCourseName(rs.getString("course_name"));

                dto.setTotalHours(round(totalHours));
                dto.setAttendedHours(round(attendedHours));
                dto.setMedicalHours(round(medicalHours));
                dto.setFinalAttendanceHours(round(finalAttendanceHours));
                dto.setAttendancePercentage(attendancePercentage);
                dto.setAttendanceStatus(attendanceStatus);

                dto.setCaMaxMarks(round(caMaxMarks));
                dto.setCaMarks(round(caMarks));
                dto.setCaPercentage(caPercentage);
                dto.setCaStatus(caStatus);

                dto.setFinalEligibilityStatus(finalStatus);

                list.add(dto);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    private double calculateApprovedMedicalHours(Connection con, String studentId, String courseId) throws SQLException {
        int approvedMedicalCount = getApprovedAttendanceMedicalCount(con, studentId, courseId);

        if (approvedMedicalCount <= 0) {
            return 0.0;
        }

        String sql = """
                SELECT se.session_hours
                FROM attendance a
                INNER JOIN session se ON a.session_id = se.session_id
                WHERE a.student_id = ?
                  AND se.course_id = ?
                  AND a.status = 'Absent'
                ORDER BY se.session_date ASC, se.session_id ASC
                LIMIT ?
                """;

        double medicalHours = 0.0;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, studentId);
            ps.setString(2, courseId);
            ps.setInt(3, approvedMedicalCount);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                medicalHours += rs.getDouble("session_hours");
            }
        }

        return medicalHours;
    }

    private int getApprovedAttendanceMedicalCount(Connection con, String studentId, String courseId) throws SQLException {
        String sql = """
                SELECT COUNT(*) AS medical_count
                FROM medical
                WHERE student_id = ?
                  AND course_id = ?
                  AND exam_type = 'Attendance'
                  AND status = 'Approved'
                """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, studentId);
            ps.setString(2, courseId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("medical_count");
            }
        }

        return 0;
    }

    private double getCourseCAMaxMarks(Connection con, String courseId) throws SQLException {
        String sql = """
                SELECT COUNT(*) AS practical_count
                FROM session
                WHERE course_id = ?
                  AND type = 'Practical'
                """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, courseId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int practicalCount = rs.getInt("practical_count");

                if (practicalCount > 0) {
                    return 40.0;
                }
            }
        }

        return 30.0;
    }

    private double calculateStudentCAMarks(Connection con, String studentId, String courseId, double caMax) throws SQLException {
        String sql = """
                SELECT 
                    at.name,
                    at.weight,
                    sm.marks
                FROM assessment_type at
                LEFT JOIN student_marks sm
                    ON sm.assessment_type_id = at.assessment_type_id
                   AND sm.student_id = ?
                WHERE at.course_id = ?
                  AND at.component = 'CA'
                """;

        List<QuizMark> quizMarks = new ArrayList<>();

        double totalContribution = 0.0;
        double selectedWeight = 0.0;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, studentId);
            ps.setString(2, courseId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String assessmentName = rs.getString("name");
                double weight = rs.getDouble("weight");

                double marks = rs.getDouble("marks");

                if (rs.wasNull()) {
                    marks = 0.0;
                }

                double contribution = (marks / 100.0) * weight;

                if (assessmentName != null &&
                        assessmentName.toLowerCase().contains("quiz")) {

                    quizMarks.add(new QuizMark(weight, contribution));

                } else {
                    totalContribution += contribution;
                    selectedWeight += weight;
                }
            }
        }

        quizMarks.sort(Comparator.comparingDouble(QuizMark::getContribution).reversed());

        int bestQuizCount = Math.min(2, quizMarks.size());

        for (int i = 0; i < bestQuizCount; i++) {
            totalContribution += quizMarks.get(i).getContribution();
            selectedWeight += quizMarks.get(i).getWeight();
        }

        if (selectedWeight <= 0) {
            return 0.0;
        }

        return round((totalContribution / selectedWeight) * caMax);
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private static class QuizMark {
        private final double weight;
        private final double contribution;

        public QuizMark(double weight, double contribution) {
            this.weight = weight;
            this.contribution = contribution;
        }

        public double getWeight() {
            return weight;
        }

        public double getContribution() {
            return contribution;
        }
    }


}