package dao.result;

import dto.requestDto.result.GenerateCourseResultReqDTO;
import utility.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CourseResultGeneratorDAO {

    public int generateCourseResults(GenerateCourseResultReqDTO dto) {
        String sql = """
                SELECT student_id, registration_type
                FROM course_registration
                WHERE course_id = ?
                  AND academic_year = ?
                  AND semester = ?
                """;

        int count = 0;

        try (Connection con = DataSource.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, dto.getCourseId());
            ps.setInt(2, dto.getAcademicYear());
            ps.setString(3, dto.getSemester());

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String studentId = rs.getString("student_id");
                String registrationType = rs.getString("registration_type");

                processStudent(con, studentId, registrationType, dto);
                count++;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return count;
    }

    private void processStudent(Connection con,
                                String studentId,
                                String registrationType,
                                GenerateCourseResultReqDTO dto) throws SQLException {

        String courseId = dto.getCourseId();

        boolean hasPractical = hasPracticalSession(con, courseId);

        double caMax = hasPractical ? 40.0 : 30.0;
        double finalMax = hasPractical ? 60.0 : 70.0;

        double attendancePercentage = calculateAttendancePercentage(con, studentId, courseId);
        double caPercentage = calculateCAPercentage(con, studentId, courseId);
        double finalRawMarks = getFinalRawMarks(con, studentId, courseId);

        double caContribution = (caPercentage / 100.0) * caMax;
        double finalContribution = (finalRawMarks / 100.0) * finalMax;

        double totalMarks = round(caContribution + finalContribution);

        String grade;

        if ("Suspend".equalsIgnoreCase(registrationType)) {
            grade = "WH";
            totalMarks = 0.0;

        } else if (hasApprovedExamMedical(con, studentId, courseId)) {
            grade = "WH";
            totalMarks = 0.0;

        } else if ("Repeat".equalsIgnoreCase(registrationType)) {

            if (caPercentage < 50.0) {
                grade = "EE";
                totalMarks = 0.0;
            } else {
                grade = calculateGrade(totalMarks);
                grade = capRepeatGradeToC(grade);
            }

        } else {

            if (attendancePercentage < 80.0 || caPercentage < 50.0) {
                grade = "EE";
                totalMarks = 0.0;
            } else {
                grade = calculateGrade(totalMarks);
            }
        }

        saveCourseResult(
                con,
                studentId,
                courseId,
                dto.getAcademicYear(),
                dto.getAcademicLevel(),
                dto.getSemester(),
                totalMarks,
                grade
        );
    }

    private void saveCourseResult(Connection con,
                                  String studentId,
                                  String courseId,
                                  int academicYear,
                                  int academicLevel,
                                  String semester,
                                  double totalMarks,
                                  String grade) throws SQLException {

        String sql = """
                INSERT INTO course_result(
                    student_id,
                    course_id,
                    academic_year,
                    academic_level,
                    semester,
                    total_marks,
                    grade
                )
                VALUES (?, ?, ?, ?, ?, ?, ?)
                ON DUPLICATE KEY UPDATE
                    academic_level = VALUES(academic_level),
                    total_marks = VALUES(total_marks),
                    grade = VALUES(grade)
                """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, studentId);
            ps.setString(2, courseId);
            ps.setInt(3, academicYear);
            ps.setInt(4, academicLevel);
            ps.setString(5, semester);
            ps.setDouble(6, totalMarks);
            ps.setString(7, grade);

            ps.executeUpdate();
        }
    }

    private boolean hasApprovedExamMedical(Connection con, String studentId, String courseId) throws SQLException {
        String sql = """
                SELECT 1
                FROM medical
                WHERE student_id = ?
                  AND course_id = ?
                  AND status = 'Approved'
                  AND exam_type IN ('Mid', 'Final')
                LIMIT 1
                """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, studentId);
            ps.setString(2, courseId);

            return ps.executeQuery().next();
        }
    }

    private double calculateAttendancePercentage(Connection con, String studentId, String courseId) throws SQLException {
        double totalHours = getTotalCourseHours(con, courseId);
        double attendedHours = getAttendedHours(con, studentId, courseId);
        double medicalHours = calculateApprovedAttendanceMedicalHours(con, studentId, courseId);

        double finalAttendanceHours = attendedHours + medicalHours;

        if (finalAttendanceHours > totalHours) {
            finalAttendanceHours = totalHours;
        }

        if (totalHours <= 0) {
            return 0.0;
        }

        return round((finalAttendanceHours / totalHours) * 100.0);
    }

    private double getTotalCourseHours(Connection con, String courseId) throws SQLException {
        String sql = """
                SELECT COALESCE(SUM(session_hours), 0) AS total_hours
                FROM session
                WHERE course_id = ?
                """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, courseId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getDouble("total_hours");
            }
        }

        return 0.0;
    }

    private double getAttendedHours(Connection con, String studentId, String courseId) throws SQLException {
        String sql = """
                SELECT COALESCE(SUM(a.hours_attended), 0) AS attended_hours
                FROM attendance a
                INNER JOIN session se ON a.session_id = se.session_id
                WHERE a.student_id = ?
                  AND se.course_id = ?
                  AND a.status = 'Present'
                """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, studentId);
            ps.setString(2, courseId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getDouble("attended_hours");
            }
        }

        return 0.0;
    }

    private double calculateApprovedAttendanceMedicalHours(Connection con, String studentId, String courseId) throws SQLException {
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

    private double calculateCAPercentage(Connection con, String studentId, String courseId) throws SQLException {
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

            ResultSet rs = ps.executeQuery();

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

        quizList.sort(Comparator.comparingDouble(CAItem::getContribution).reversed());

        int bestQuizCount = Math.min(2, quizList.size());

        for (int i = 0; i < bestQuizCount; i++) {
            contribution += quizList.get(i).getContribution();
            selectedWeight += quizList.get(i).getWeight();
        }

        if (selectedWeight <= 0) {
            return 0.0;
        }

        return round((contribution / selectedWeight) * 100.0);
    }

    private double getFinalRawMarks(Connection con, String studentId, String courseId) throws SQLException {
        String sql = """
                SELECT sm.marks
                FROM student_marks sm
                INNER JOIN assessment_type at
                    ON sm.assessment_type_id = at.assessment_type_id
                WHERE sm.student_id = ?
                  AND at.course_id = ?
                  AND at.component = 'Final'
                LIMIT 1
                """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, studentId);
            ps.setString(2, courseId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getDouble("marks");
            }
        }

        return 0.0;
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

            return ps.executeQuery().next();
        }
    }

    private String calculateGrade(double marks) {
        if (marks >= 85) return "A+";
        if (marks >= 80) return "A";
        if (marks >= 75) return "A-";
        if (marks >= 70) return "B+";
        if (marks >= 65) return "B";
        if (marks >= 60) return "B-";
        if (marks >= 55) return "C+";
        if (marks >= 50) return "C";
        if (marks >= 45) return "C-";
        if (marks >= 40) return "D+";
        if (marks >= 35) return "D";
        return "F";
    }

    private String capRepeatGradeToC(String grade) {
        double gp = gradePoint(grade);
        double cGp = gradePoint("C");

        if (gp > cGp) {
            return "C";
        }

        return grade;
    }

    private double gradePoint(String grade) {
        if (grade == null) return 0.0;

        return switch (grade.toUpperCase()) {
            case "A+", "A" -> 4.0;
            case "A-" -> 3.7;
            case "B+" -> 3.3;
            case "B" -> 3.0;
            case "B-" -> 2.7;
            case "C+" -> 2.3;
            case "C" -> 2.0;
            case "C-" -> 1.7;
            case "D+" -> 1.3;
            case "D" -> 1.0;
            default -> 0.0;
        };
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