package dao.eligibility;
import model.Eligibility;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class EligibilityDAO {

    private final Connection connection;

    public EligibilityDAO(Connection connection) {
        this.connection = connection;
    }

    public boolean insertEligibility(Eligibility eligibility) {
        String sql = "INSERT INTO eligibility(student_id, course_id, academic_year, semester, is_eligible) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, eligibility.getStudentId());
            ps.setString(2, eligibility.getCourseId());
            ps.setInt(3, eligibility.getAcademicYear());
            ps.setInt(4, eligibility.getSemester());
            ps.setBoolean(5, eligibility.isEligible());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Eligibility getStudentEligibility(String studentId, String courseId) {
        String sql = "SELECT * FROM eligibility WHERE student_id = ? AND course_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, studentId);
            ps.setString(2, courseId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Eligibility(
                        rs.getString("student_id"),
                        rs.getString("course_id"),
                        rs.getInt("academic_year"),
                        rs.getInt("semester"),
                        rs.getBoolean("is_eligible")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Eligibility> getBatchEligibility(int academicYear, int semester) {
        List<Eligibility> list = new ArrayList<>();
        String sql = "SELECT * FROM eligibility WHERE academic_year = ? AND semester = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, academicYear);
            ps.setInt(2, semester);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Eligibility(
                        rs.getString("student_id"),
                        rs.getString("course_id"),
                        rs.getInt("academic_year"),
                        rs.getInt("semester"),
                        rs.getBoolean("is_eligible")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
