package dao.student;

import dto.requestDto.student.UpdateStudentProfileReqDTO;
import model.Student;
import model.User;
import utility.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


public class StudentDAO {


    public Student findByUserId(String userId) {
        String sql = "SELECT s.user_id, s.reg_no, s.batch, s.academic_level, s.department_id " +
                "FROM students s WHERE s.user_id = ?";
        try (Connection connection = DataSource.getInstance().getConnection();
        PreparedStatement ps = connection.prepareStatement(sql))  {
            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();

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

    public Student findStudentById(String userId) {

        String sql = """
                SELECT 
                    u.user_id,
                    u.username,
                    u.email,
                    u.contact_number,
                    u.profile_picture,
                    u.role,
                    s.reg_no,
                    s.batch,
                    s.academic_level,
                    s.department_id
                FROM users u
                LEFT JOIN students s ON u.user_id = s.user_id
                WHERE u.user_id = ?
    """;

        try (Connection connection = DataSource.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, userId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                String role = rs.getString("role");
                if ("Student".equalsIgnoreCase(role)) {

                    return new Student(
                            rs.getString("user_id"),
                            rs.getString("username"),
                            rs.getString("email"),
                            rs.getString("contact_number"),
                            rs.getString("profile_picture"),
                            rs.getString("role"),
                            rs.getString("reg_no"),
                            rs.getString("batch"),
                            rs.getInt("academic_level"),
                            rs.getString("department_id")
                    );
                }

                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean updateStudentProfile(UpdateStudentProfileReqDTO dto) {
        String sqlWithoutPassword = """
            UPDATE users
            SET email = ?, contact_number = ?, profile_picture = ?
            WHERE user_id = ?
            """;

        String sqlWithPassword = """
            UPDATE users
            SET email = ?, contact_number = ?, profile_picture = ?, password = ?
            WHERE user_id = ?
            """;

        try (Connection connection = DataSource.getInstance().getConnection()) {

            boolean hasPassword = dto.getPassword() != null && !dto.getPassword().trim().isEmpty();

            if (hasPassword) {
                try (PreparedStatement ps = connection.prepareStatement(sqlWithPassword)) {
                    ps.setString(1, dto.getEmail());
                    ps.setString(2, dto.getContactNumber());
                    ps.setString(3, dto.getProfilePicture());
                    ps.setString(4, dto.getPassword());
                    ps.setString(5, dto.getUserId());

                    return ps.executeUpdate() > 0;
                }
            } else {
                try (PreparedStatement ps = connection.prepareStatement(sqlWithoutPassword)) {
                    ps.setString(1, dto.getEmail());
                    ps.setString(2, dto.getContactNumber());
                    ps.setString(3, dto.getProfilePicture());
                    ps.setString(4, dto.getUserId());

                    return ps.executeUpdate() > 0;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}
