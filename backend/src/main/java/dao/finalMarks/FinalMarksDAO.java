package dao.finalMarks;

import utility.DataSource;

import java.sql.*;

public class FinalMarksDAO {

    public String getStudentIdByRegNo(String regNo) {
        String sql = "SELECT user_id FROM students WHERE reg_no = ?";

        try (Connection con = DataSource.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, regNo);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString("user_id");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private int getFinalAssessmentId(Connection con, String courseId) throws SQLException {

        String find = """
                SELECT assessment_type_id 
                FROM assessment_type
                WHERE course_id = ? AND name = 'Final'
                """;

        try (PreparedStatement ps = con.prepareStatement(find)) {
            ps.setString(1, courseId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("assessment_type_id");
            }
        }

        String insert = """
                INSERT INTO assessment_type(course_id, name, weight, component)
                VALUES (?, 'Final', 100, 'Final')
                """;

        try (PreparedStatement ps = con.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, courseId);
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();

            if (rs.next()) {
                return rs.getInt(1);
            }
        }

        throw new SQLException("Cannot create assessment type");
    }

    public boolean saveMarks(String studentId, String courseId, double marks) {

        String sql = """
                INSERT INTO student_marks(student_id, assessment_type_id, marks)
                VALUES (?, ?, ?)
                ON DUPLICATE KEY UPDATE marks = VALUES(marks)
                """;

        try (Connection con = DataSource.getInstance().getConnection()) {

            int assessmentId = getFinalAssessmentId(con, courseId);

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, studentId);
                ps.setInt(2, assessmentId);
                ps.setDouble(3, marks);

                return ps.executeUpdate() > 0;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}