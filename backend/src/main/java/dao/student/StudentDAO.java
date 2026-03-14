package dao.student;

import model.Student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class StudentDAO {

    private final Connection connection;

    public StudentDAO(Connection connection) {
        this.connection = connection;
    }

    public Student findByUserId(String userId) {
        String sql = "SELECT s.user_id, s.reg_no, s.batch, s.academic_level, s.department_id " +
                "FROM students s WHERE s.user_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Student(
                        rs.getString("user_id"),
                        rs.getString("reg_no"),
                        rs.getString("batch"),
                        rs.getInt("academic_level"),
                        rs.getString("department_id")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
