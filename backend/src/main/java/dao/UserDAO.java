package dao;

import model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    private final Connection connection;

    public UserDAO(Connection connection) {
        this.connection = connection;
    }

    public User findByUsernameAndPassword(String username, String password) {
        String sql = "SELECT user_id, username, password, role FROM users WHERE username = ? AND password = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new User(
                        rs.getString("user_id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("role")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public List<User> findAllUsers() {
        String sql = "SELECT * FROM users";
        List<User> users = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) { // loop through all rows
                User user = new User(
                        rs.getString("user_id"),            // userId
                        rs.getString("username"),           // username
                        rs.getString("email"),              // email
                        rs.getString("password"),           // password
                        rs.getString("contact_number"),     // contactNumber
                        rs.getString("profile_picture"),    // profilePicture
                        rs.getString("role")                // role
                );
                users.add(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return users;
    }

    public User createUser(User user) {

        String sql = "INSERT INTO users " +
                "(user_id, username, email, password, contact_number, profile_picture, role) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            // Trim values to prevent hidden spaces
            String username = user.getUsername() != null ? user.getUsername().trim() : null;
            String email = user.getEmail() != null ? user.getEmail().trim() : null;
            String password = user.getPassword() != null ? user.getPassword().trim() : null;
            String contactNumber = user.getContactNumber() != null ? user.getContactNumber().trim() : null;

            if (contactNumber != null && contactNumber.length() > 20) {
                contactNumber = contactNumber.substring(0, 20); // ensure fits VARCHAR(20)
            }
            String profilePicture = user.getProfilePicture(); // keep as is, can be null
            String role = user.getRole() != null ? user.getRole().trim() : null;

            // Set values safely
            ps.setString(1, user.getUserId());
            ps.setString(2, username);
            ps.setString(3, email);
            ps.setString(4, password);
            ps.setString(5, contactNumber);

            if (profilePicture != null) {
                ps.setString(6, profilePicture);
            } else {
                ps.setNull(6, java.sql.Types.VARCHAR);
            }

            ps.setString(7, role);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                insertIntoRoleTable(user); // insert into role-specific table
                return user;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private void insertIntoRoleTable(User user) throws Exception {

        String role = user.getRole().trim(); // trim spaces
        String table;

        switch (role) {
            case "Student":
                table = "students";
                break;
            case "Lecturer":
                table = "lecturers";
                break;
            case "Tech_Officer":
                table = "technical_officers";
                break;
            case "Admin":
                table = "admins";
                break;
            default:
                throw new Exception("Invalid role: " + role);
        }

        String sql = "INSERT INTO " + table + " (user_id) VALUES (?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, user.getUserId());
            ps.executeUpdate();
        }
    }

    public User getUserById(String userId) {
        try {
            String sql = "SELECT * FROM users WHERE user_id = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}