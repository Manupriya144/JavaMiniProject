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
        Integer finalAssessmentTypeId = resolveFinalAssessmentTypeId(marks.getCourseId());
        if (finalAssessmentTypeId == null) {
            return false;
        }
        String sql = "INSERT INTO student_marks (student_id, assessment_type_id, marks) VALUES (?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, marks.getStudentId());
            ps.setInt(2, finalAssessmentTypeId);
            ps.setDouble(3, marks.getMarks());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateFinalMarks(FinalMarks marks) {
        Integer finalAssessmentTypeId = resolveFinalAssessmentTypeId(marks.getCourseId());
        if (finalAssessmentTypeId == null) {
            return false;
        }
        String sql = "UPDATE student_marks SET marks = ? WHERE student_id = ? AND assessment_type_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDouble(1, marks.getMarks());
            ps.setString(2, marks.getStudentId());
            ps.setInt(3, finalAssessmentTypeId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public FinalMarks getStudentMarks(String studentId, String courseId) {
        Integer finalAssessmentTypeId = resolveFinalAssessmentTypeId(courseId);
        if (finalAssessmentTypeId == null) {
            return null;
        }
        String sql = "SELECT sm.student_id, at.course_id, sm.marks FROM student_marks sm " +
                "INNER JOIN assessment_type at ON sm.assessment_type_id = at.assessment_type_id " +
                "WHERE sm.student_id = ? AND sm.assessment_type_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, studentId);
            ps.setInt(2, finalAssessmentTypeId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new FinalMarks(
                        rs.getString("student_id"),
                        rs.getString("course_id"),
                        0, // academicYear unknown, skip
                        0, // semester unknown
                        rs.getDouble("marks")
                );
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    public List<FinalMarks> getBatchMarks(int academicYear, int semester) {
        String sql = "SELECT sm.student_id, at.course_id, sm.marks FROM student_marks sm " +
                "INNER JOIN assessment_type at ON sm.assessment_type_id = at.assessment_type_id " +
                "WHERE at.component = 'Final'";
        List<FinalMarks> list = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new FinalMarks(
                        rs.getString("student_id"),
                        rs.getString("course_id"),
                        academicYear,
                        semester,
                        rs.getDouble("marks")
                ));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    private Integer resolveFinalAssessmentTypeId(String courseId) {
        String sql = "SELECT assessment_type_id FROM assessment_type WHERE course_id = ? AND component = 'Final' ORDER BY assessment_type_id LIMIT 1";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, courseId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("assessment_type_id");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
