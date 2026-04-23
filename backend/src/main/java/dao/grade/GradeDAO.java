package dao.grade;

import model.Grade;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class GradeDAO {

    private final Connection connection;

    public GradeDAO(Connection connection) {
        this.connection = connection;
    }

    public boolean insertGrade(Grade grade) {
        String sql = "INSERT INTO grades(student_id, course_id, academic_year, semester, grade) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, grade.getStudentId());
            ps.setString(2, grade.getCourseId());
            ps.setInt(3, grade.getAcademicYear());
            ps.setInt(4, grade.getSemester());
            ps.setString(5, grade.getGrade());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Grade getStudentGrade(String studentId, String courseId) {
        String sql = "SELECT * FROM grades WHERE student_id = ? AND course_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, studentId);
            ps.setString(2, courseId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Grade(
                        rs.getString("student_id"),
                        rs.getString("course_id"),
                        rs.getInt("academic_year"),
                        rs.getInt("semester"),
                        rs.getString("grade")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Grade> getBatchGrades(int academicYear, int semester) {
        List<Grade> list = new ArrayList<>();
        String sql = "SELECT * FROM grades WHERE academic_year = ? AND semester = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, academicYear);
            ps.setInt(2, semester);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Grade(
                        rs.getString("student_id"),
                        rs.getString("course_id"),
                        rs.getInt("academic_year"),
                        rs.getInt("semester"),
                        rs.getString("grade")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
