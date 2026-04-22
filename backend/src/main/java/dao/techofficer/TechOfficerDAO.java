package dao.techofficer;

import model.TechOfficer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class TechOfficerDAO {
    private final Connection connection;

    public TechOfficerDAO(Connection connection) {
        this.connection = connection;
    }

    public TechOfficer getTechOfficerProfileByUserId(String userId) {
        String sql = "SELECT u.user_id, u.username, u.email, u.password, u.contact_number, u.profile_picture, u.role, t.department_id " +
                "FROM users u INNER JOIN tech_officers t ON u.user_id = t.user_id " +
                "WHERE u.user_id = ? AND u.role = 'Tech_Officer'";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new TechOfficer(
                        rs.getString("user_id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("contact_number"),
                        rs.getString("profile_picture"),
                        rs.getString("role"),
                        rs.getString("department_id")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateTechOfficerProfile(TechOfficer techOfficer) {
        String updateUserSql = "UPDATE users SET username = ?, email = ?, password = ?, contact_number = ?, profile_picture = ? " +
                "WHERE user_id = ? AND role = 'Tech_Officer'";
        String updateTechSql = "UPDATE tech_officers SET department_id = ? WHERE user_id = ?";

        try {
            connection.setAutoCommit(false);

            int userRows;
            try (PreparedStatement userPs = connection.prepareStatement(updateUserSql)) {
                userPs.setString(1, techOfficer.getUsername());
                userPs.setString(2, techOfficer.getEmail());
                userPs.setString(3, techOfficer.getPassword());
                userPs.setString(4, techOfficer.getContactNumber());
                userPs.setString(5, techOfficer.getProfilePicture());
                userPs.setString(6, techOfficer.getUserId());
                userRows = userPs.executeUpdate();
            }

            int techRows;
            try (PreparedStatement techPs = connection.prepareStatement(updateTechSql)) {
                techPs.setString(1, techOfficer.getDepartmentId());
                techPs.setString(2, techOfficer.getUserId());
                techRows = techPs.executeUpdate();
            }

            if (userRows == 1 && techRows == 1) {
                connection.commit();
                return true;
            }

            connection.rollback();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            try {
                connection.rollback();
            } catch (Exception ignored) {
            }
            return false;
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (Exception ignored) {
            }
        }
    }
}
