package dao.user;

import dto.requestDto.user.UpdateUserReqDTO;
import model.Lecturer;
import model.Student;
import model.TechOfficer;
import model.User;
import utility.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    public User findByUsernameAndPassword(String username, String password) {
        String sql = "SELECT user_id, username, password, role FROM users WHERE username = ? AND password = ?";
        try (Connection connection = DataSource.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getString("user_id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("role")
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<User> findAllUsers() {
        String sql = "SELECT * FROM users";
        List<User> users = new ArrayList<>();

        try (Connection connection = DataSource.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                User user = new User(
                        rs.getString("user_id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("contact_number"),
                        rs.getString("profile_picture"),
                        rs.getString("role")
                );
                users.add(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return users;
    }

    public User createUser(User user) {
        String sqlUser = "INSERT INTO users " +
                "(user_id, username, email, password, contact_number, profile_picture, role) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DataSource.getInstance().getConnection()) {
            connection.setAutoCommit(false);

            try (PreparedStatement ps = connection.prepareStatement(sqlUser)) {
                ps.setString(1, user.getUserId());
                ps.setString(2, user.getUsername().trim());
                ps.setString(3, user.getEmail() != null ? user.getEmail().trim() : null);
                ps.setString(4, user.getPassword().trim());
                ps.setString(5, user.getContactNumber() != null ? user.getContactNumber().trim() : null);

                if (user.getProfilePicture() != null) {
                    ps.setString(6, user.getProfilePicture());
                } else {
                    ps.setNull(6, java.sql.Types.VARCHAR);
                }

                ps.setString(7, user.getRole().trim());

                int rows = ps.executeUpdate();
                if (rows == 0) {
                    connection.rollback();
                    return null;
                }
            }

            insertRoleData(connection, user);

            connection.commit();
            return user;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void insertRoleData(Connection connection, User user) throws Exception {
        String role = user.getRole().trim();

        switch (role) {
            case "Student" -> insertStudent(connection, (Student) user);
            case "Lecturer" -> insertLecturer(connection, (Lecturer) user);
            case "Tech_Officer" -> insertTechOfficer(connection, (TechOfficer) user);
            case "Admin" -> {
                // no extra table
            }
            case "Dean" -> {
                // no extra table
            }
            default -> throw new Exception("Unknown role: " + role);
        }
    }

    private void insertStudent(Connection connection, Student student) throws Exception {
        String sql = "INSERT INTO students (user_id, reg_no, batch, academic_level, department_id) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, student.getUserId());
            ps.setString(2, student.getRegNo());
            ps.setString(3, student.getBatch());
            ps.setInt(4, student.getAcademicLevel());
            ps.setString(5, student.getDepartmentId());
            ps.executeUpdate();
        }
    }

    private void insertLecturer(Connection connection, Lecturer lecturer) throws Exception {
        String sql = "INSERT INTO lecturers (user_id, specialization, designation) VALUES (?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, lecturer.getUserId());
            ps.setString(2, lecturer.getSpecialization());
            ps.setString(3, lecturer.getDesignation());
            ps.executeUpdate();
        }
    }

    private void insertTechOfficer(Connection connection, TechOfficer officer) throws Exception {
        String sql = "INSERT INTO tech_officers (user_id, department_id) VALUES (?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, officer.getUserId());
            ps.setString(2, officer.getDepartmentId());
            ps.executeUpdate();
        }
    }

    public User getUserById(String userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";

        try (Connection connection = DataSource.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getString("user_id"),
                            rs.getString("username"),
                            rs.getString("email"),
                            rs.getString("password"),
                            rs.getString("contact_number"),
                            rs.getString("profile_picture"),
                            rs.getString("role")
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean updateUser(UpdateUserReqDTO dto) {
        String sql = """
            UPDATE users
            SET username = ?,
                email = ?,
                contact_number = ?,
                role = ?,
                profile_picture = ?
            WHERE user_id = ?
            """;

        try (Connection con = DataSource.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, dto.getUsername());
            ps.setString(2, dto.getEmail());
            ps.setString(3, dto.getContactNo());
            ps.setString(4, dto.getRole());
            ps.setString(5, dto.getProfilePicture());
            ps.setString(6, dto.getUserId());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteUser(String userId) {
        String sql = "DELETE FROM users WHERE user_id = ?";

        try (Connection con = DataSource.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, userId);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}