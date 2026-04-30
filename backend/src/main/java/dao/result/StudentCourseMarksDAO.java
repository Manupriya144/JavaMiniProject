package dao.result;

import dto.responseDto.result.StudentCourseMarksDTO;
import utility.DataSource;

import java.sql.*;
import java.util.*;

public class StudentCourseMarksDAO {

    public List<StudentCourseMarksDTO> getAllStudentCourseMarks() {
        List<StudentCourseMarksDTO> list = new ArrayList<>();

        String sql = """
                SELECT
                    s.user_id AS student_id,
                    s.reg_no,
                    u.username AS student_name,
                    d.name AS department_name,
                    s.academic_level,
                    cr.semester,
                    cr.academic_year,
                    cr.registration_type,
                    c.course_id,
                    c.course_code,
                    c.name AS course_name,

                    COALESCE(total_hours.total, 0) AS total_hours,
                    COALESCE(att_hours.attended, 0) AS attended_hours,
                    COALESCE(final_marks.final_marks, 0) AS final_exam_marks

                FROM course_registration cr
                INNER JOIN students s ON cr.student_id = s.user_id
                INNER JOIN users u ON s.user_id = u.user_id
                INNER JOIN department d ON s.department_id = d.department_id
                INNER JOIN course c ON cr.course_id = c.course_id

                LEFT JOIN (
                    SELECT course_id, SUM(session_hours) AS total
                    FROM session
                    GROUP BY course_id
                ) total_hours ON total_hours.course_id = c.course_id

                LEFT JOIN (
                    SELECT 
                        a.student_id, 
                        se.course_id, 
                        SUM(a.hours_attended) AS attended
                    FROM attendance a
                    INNER JOIN session se ON a.session_id = se.session_id
                    WHERE a.status = 'Present'
                    GROUP BY a.student_id, se.course_id
                ) att_hours ON att_hours.student_id = s.user_id
                            AND att_hours.course_id = c.course_id

                LEFT JOIN (
                    SELECT 
                        sm.student_id, 
                        at.course_id, 
                        MAX(sm.marks) AS final_marks
                    FROM student_marks sm
                    INNER JOIN assessment_type at 
                        ON sm.assessment_type_id = at.assessment_type_id
                    WHERE at.component = 'Final'
                    GROUP BY sm.student_id, at.course_id
                ) final_marks ON final_marks.student_id = s.user_id
                              AND final_marks.course_id = c.course_id

                WHERE cr.academic_year = YEAR(CURDATE())
                  AND cr.semester = c.semester
                  AND cr.registration_type IN ('Proper', 'Repeat')

                ORDER BY s.reg_no, c.course_code
                """;

        try (Connection con = DataSource.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String studentId = rs.getString("student_id");
                String courseId = rs.getString("course_id");
                String registrationType = rs.getString("registration_type");

                double totalHours = rs.getDouble("total_hours");
                double attendedHours = rs.getDouble("attended_hours");

                double medicalAttendanceHours =
                        calculateApprovedAttendanceMedicalHours(con, studentId, courseId);

                double finalAttendanceHours = attendedHours + medicalAttendanceHours;

                if (finalAttendanceHours > totalHours) {
                    finalAttendanceHours = totalHours;
                }

                double attendancePercentage = totalHours > 0
                        ? (finalAttendanceHours / totalHours) * 100.0
                        : 0.0;

                double caPercentage = calculateCAPercentage(con, studentId, courseId);

                boolean hasPractical = hasPracticalSession(con, courseId);

                double finalExamRawMarks = rs.getDouble("final_exam_marks");

                double finalExamContribution = hasPractical
                        ? (finalExamRawMarks / 100.0) * 60.0
                        : (finalExamRawMarks / 100.0) * 70.0;

                String medicalStatus = getMedicalStatus(con, studentId, courseId);

                String resultStatus = calculateResultStatus(
                        registrationType,
                        attendancePercentage,
                        caPercentage,
                        finalExamRawMarks,
                        medicalStatus
                );

                StudentCourseMarksDTO dto = new StudentCourseMarksDTO();

                dto.setStudentId(studentId);
                dto.setRegNo(rs.getString("reg_no"));
                dto.setStudentName(rs.getString("student_name"));
                dto.setDepartmentName(rs.getString("department_name"));
                dto.setAcademicLevel(rs.getInt("academic_level"));
                dto.setSemester(rs.getString("semester"));

                dto.setCourseId(courseId);
                dto.setCourseCode(rs.getString("course_code"));
                dto.setCourseName(rs.getString("course_name"));

                dto.setAttendancePercentage(round(attendancePercentage));
                dto.setCaPercentage(round(caPercentage));
                dto.setFinalExamMarks(round(finalExamContribution));

                dto.setMedicalStatus(medicalStatus);
                dto.setResultStatus(resultStatus);

                list.add(dto);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    private String calculateResultStatus(String registrationType,
                                         double attendancePercentage,
                                         double caPercentage,
                                         double finalExamRawMarks,
                                         String medicalStatus) {

        if ("WH".equalsIgnoreCase(medicalStatus)) {
            return "WH";
        }

        if (finalExamRawMarks <= 0) {
            return "EC";
        }

        if ("Repeat".equalsIgnoreCase(registrationType)) {
            return caPercentage < 50.0 ? "EE" : "Allowed";
        }

        if (attendancePercentage < 80.0 || caPercentage < 50.0) {
            return "EE";
        }

        return "Allowed";
    }

    private String getMedicalStatus(Connection con, String studentId, String courseId) throws SQLException {
        String whSql = """
                SELECT 1
                FROM medical
                WHERE student_id = ?
                  AND course_id = ?
                  AND status = 'Approved'
                  AND exam_type IN ('Mid', 'Final')
                LIMIT 1
                """;

        try (PreparedStatement ps = con.prepareStatement(whSql)) {
            ps.setString(1, studentId);
            ps.setString(2, courseId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return "WH";
                }
            }
        }

        String approvedAttendanceSql = """
                SELECT 1
                FROM medical
                WHERE student_id = ?
                  AND course_id = ?
                  AND status = 'Approved'
                  AND exam_type = 'Attendance'
                LIMIT 1
                """;

        try (PreparedStatement ps = con.prepareStatement(approvedAttendanceSql)) {
            ps.setString(1, studentId);
            ps.setString(2, courseId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return "Approved Attendance Medical";
                }
            }
        }

        String pendingSql = """
                SELECT 1
                FROM medical
                WHERE student_id = ?
                  AND course_id = ?
                  AND status = 'Pending'
                LIMIT 1
                """;

        try (PreparedStatement ps = con.prepareStatement(pendingSql)) {
            ps.setString(1, studentId);
            ps.setString(2, courseId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return "Pending Medical";
                }
            }
        }

        return "No Medical";
    }

    private double calculateApprovedAttendanceMedicalHours(Connection con,
                                                           String studentId,
                                                           String courseId) throws SQLException {
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

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    medicalHours += rs.getDouble("session_hours");
                }
            }
        }

        return medicalHours;
    }

    private int getApprovedAttendanceMedicalCount(Connection con,
                                                  String studentId,
                                                  String courseId) throws SQLException {
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

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("medical_count");
                }
            }
        }

        return 0;
    }

    private boolean hasPracticalSession(Connection con, String courseId) throws SQLException {
        String sql = """
                SELECT 1
                FROM session
                WHERE course_id = ?
                  AND type = 'Practical'
                LIMIT 1
                """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, courseId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private double calculateCAPercentage(Connection con,
                                         String studentId,
                                         String courseId) throws SQLException {
        String sql = """
                SELECT at.name, at.weight, sm.marks
                FROM assessment_type at
                LEFT JOIN student_marks sm
                    ON sm.assessment_type_id = at.assessment_type_id
                   AND sm.student_id = ?
                WHERE at.course_id = ?
                  AND at.component = 'CA'
                """;

        List<CAItem> quizList = new ArrayList<>();

        double contribution = 0.0;
        double selectedWeight = 0.0;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, studentId);
            ps.setString(2, courseId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String name = rs.getString("name");
                    double weight = rs.getDouble("weight");

                    double marks = rs.getDouble("marks");
                    if (rs.wasNull()) {
                        marks = 0.0;
                    }

                    double value = (marks / 100.0) * weight;

                    if (name != null && name.toLowerCase().contains("quiz")) {
                        quizList.add(new CAItem(weight, value));
                    } else {
                        contribution += value;
                        selectedWeight += weight;
                    }
                }
            }
        }

        quizList.sort(Comparator.comparingDouble(CAItem::getContribution).reversed());

        int count = Math.min(2, quizList.size());

        for (int i = 0; i < count; i++) {
            contribution += quizList.get(i).getContribution();
            selectedWeight += quizList.get(i).getWeight();
        }

        if (selectedWeight <= 0) {
            return 0.0;
        }

        return round((contribution / selectedWeight) * 100.0);
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private static class CAItem {
        private final double weight;
        private final double contribution;

        public CAItem(double weight, double contribution) {
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