package dao.finalMarks;

import model.FinalMarks;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class FinalMarksDAO {
    private final Connection connection;

    public FinalMarksDAO(Connection connection) {
        this.connection = connection;
    }

    public boolean insertFinalMarks(FinalMarks marks) {
        String sql = "INSERT INTO student_marks (student_id, assessment_type_id, marks) VALUES (?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, marks.getStudentId());
            ps.setString(2, marks.getCourseId()); // assume courseId is assessment_type_id
            ps.setDouble(3, marks.getMarks());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateFinalMarks(FinalMarks marks) {
        String sql = "UPDATE student_marks SET marks = ? WHERE student_id = ? AND assessment_type_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDouble(1, marks.getMarks());
            ps.setString(2, marks.getStudentId());
            ps.setString(3, marks.getCourseId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public FinalMarks getStudentMarks(String studentId, String courseId) {
        String sql = "SELECT * FROM student_marks WHERE student_id = ? AND assessment_type_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, studentId);
            ps.setString(2, courseId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new FinalMarks(
                        rs.getString("student_id"),
                        rs.getString("assessment_type_id"),
                        0, // academicYear unknown, skip
                        0, // semester unknown
                        rs.getDouble("marks")
                );
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    public List<FinalMarks> getBatchMarks(int academicYear, int semester) {
        String sql = "SELECT * FROM student_marks"; // simplified
        List<FinalMarks> list = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new FinalMarks(
                        rs.getString("student_id"),
                        rs.getString("assessment_type_id"),
                        academicYear,
                        semester,
                        rs.getDouble("marks")
                ));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
}
