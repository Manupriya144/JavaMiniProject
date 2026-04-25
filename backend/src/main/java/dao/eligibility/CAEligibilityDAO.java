package dao.eligibility;

import dto.responseDto.eligibility.CAEligibilityDTO;
import utility.DataSource;

import java.sql.*;
import java.util.*;

public class CAEligibilityDAO {

    public boolean isLecturerAssigned(String lecturerId, String courseId) {
        String sql = """
                SELECT 1 
                FROM lecturer_course
                WHERE lecturer_id = ? 
                  AND course_id = ?
                """;

        try (Connection con = DataSource.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, lecturerId);
            ps.setString(2, courseId);

            return ps.executeQuery().next();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public double getCourseCAMaxMarks(String courseId) {
        String sql = """
                SELECT COUNT(*) AS practical_count
                FROM session
                WHERE course_id = ? 
                  AND type = 'Practical'
                """;

        try (Connection con = DataSource.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, courseId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int practicalCount = rs.getInt("practical_count");
                    return practicalCount > 0 ? 40.0 : 30.0;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 30.0;
    }

    public List<CAEligibilityDTO> getCAEligibility(String lecturerId, String courseId) {
        List<CAEligibilityDTO> result = new ArrayList<>();

        if (!isLecturerAssigned(lecturerId, courseId)) {
            return result;
        }

        String studentSql = """
                SELECT 
                    s.user_id AS student_id,
                    s.reg_no,
                    u.username AS student_name,
                    c.course_id,
                    c.course_code,
                    c.name AS course_name
                FROM course_registration cr
                INNER JOIN students s ON cr.student_id = s.user_id
                INNER JOIN users u ON s.user_id = u.user_id
                INNER JOIN course c ON cr.course_id = c.course_id
                WHERE cr.course_id = ?
                  AND cr.academic_year = YEAR(CURDATE())
                  AND cr.semester = c.semester
                  AND cr.registration_type IN ('Proper', 'Repeat')
                ORDER BY s.reg_no
                """;

        try (Connection con = DataSource.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(studentSql)) {

            ps.setString(1, courseId);

            double caMax = getCourseCAMaxMarks(courseId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String studentId = rs.getString("student_id");

                    double caMarks = calculateStudentCAMarks(con, studentId, courseId, caMax);
                    double caPercentage = caMax == 0 ? 0 : (caMarks / caMax) * 100.0;

                    caMarks = round(caMarks);
                    caPercentage = round(caPercentage);

                    String status = caPercentage >= 50.0
                            ? "Eligible"
                            : "Not Eligible";

                    result.add(new CAEligibilityDTO(
                            studentId,
                            rs.getString("reg_no"),
                            rs.getString("student_name"),
                            rs.getString("course_id"),
                            rs.getString("course_code"),
                            rs.getString("course_name"),
                            caMax,
                            caMarks,
                            caPercentage,
                            status
                    ));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    private double calculateStudentCAMarks(Connection con,
                                           String studentId,
                                           String courseId,
                                           double caMax) throws SQLException {

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

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String name = rs.getString("name");
                    double weight = rs.getDouble("weight");

                    double marks = rs.getDouble("marks");
                    if (rs.wasNull()) {
                        marks = 0.0;
                    }

                    double contribution = (marks / 100.0) * weight;

                    if (name != null && name.toLowerCase().contains("quiz")) {
                        quizMarks.add(new QuizMark(weight, contribution));
                    } else {
                        totalContribution += contribution;
                        selectedWeight += weight;
                    }
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