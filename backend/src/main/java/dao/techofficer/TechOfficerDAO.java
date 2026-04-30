package dao.techofficer;

import model.TechOfficer;
import utility.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

public class TechOfficerDAO {

    public TechOfficerDAO() {
    }

    public TechOfficer getTechOfficerProfileByUserId(String userId) {
        String sql = """
            SELECT u.user_id, u.username, u.email, u.password,
                   u.contact_number, u.profile_picture, u.role,
                   t.department_id
            FROM users u
            LEFT JOIN tech_officers t ON u.user_id = t.user_id
            WHERE u.user_id = ? AND u.role = 'Tech_Officer'
        """;

        try (Connection con = DataSource.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
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
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean updateTechOfficerProfile(TechOfficer techOfficer) {
        String updateUserSql = """
            UPDATE users
            SET username = ?, email = ?, password = ?,
                contact_number = ?, profile_picture = ?
            WHERE user_id = ? AND role = 'Tech_Officer'
        """;

        String updateTechSql = """
            UPDATE tech_officers
            SET department_id = ?
            WHERE user_id = ?
        """;

        String insertTechSql = """
            INSERT INTO tech_officers (user_id, department_id)
            VALUES (?, ?)
        """;

        try (Connection con = DataSource.getInstance().getConnection()) {
            con.setAutoCommit(false);

            try {
                int userRows;

                try (PreparedStatement userPs = con.prepareStatement(updateUserSql)) {
                    userPs.setString(1, techOfficer.getUsername());
                    userPs.setString(2, techOfficer.getEmail());
                    userPs.setString(3, techOfficer.getPassword());
                    userPs.setString(4, techOfficer.getContactNumber());
                    userPs.setString(5, techOfficer.getProfilePicture());
                    userPs.setString(6, techOfficer.getUserId());

                    userRows = userPs.executeUpdate();
                }

                int techRows;

                try (PreparedStatement techPs = con.prepareStatement(updateTechSql)) {
                    techPs.setString(1, techOfficer.getDepartmentId());
                    techPs.setString(2, techOfficer.getUserId());

                    techRows = techPs.executeUpdate();
                }

                if (techRows == 0) {
                    try (PreparedStatement insertTechPs = con.prepareStatement(insertTechSql)) {
                        insertTechPs.setString(1, techOfficer.getUserId());
                        insertTechPs.setString(2, techOfficer.getDepartmentId());

                        techRows = insertTechPs.executeUpdate();
                    }
                }

                if (userRows == 1 && techRows == 1) {
                    con.commit();
                    return true;
                }

                con.rollback();
                return false;

            } catch (Exception e) {
                con.rollback();
                e.printStackTrace();
                return false;
            } finally {
                con.setAutoCommit(true);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Map<String, Integer> getDashboardStats() {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("totalStudents", 0);
        stats.put("attendanceSessions", 0);
        stats.put("medicalRecords", 0);
        stats.put("pendingApprovals", 0);

        String totalStudentsSql = "SELECT COUNT(*) AS total FROM students";
        String attendanceSessionsSql = "SELECT COUNT(*) AS total FROM session";
        String medicalRecordsSql = "SELECT COUNT(*) AS total FROM medical";
        String pendingApprovalsSql = "SELECT COUNT(*) AS total FROM medical WHERE status = 'Pending'";

        try (Connection con = DataSource.getInstance().getConnection()) {
            stats.put("totalStudents", querySingleCount(con, totalStudentsSql));
            stats.put("attendanceSessions", querySingleCount(con, attendanceSessionsSql));
            stats.put("medicalRecords", querySingleCount(con, medicalRecordsSql));
            stats.put("pendingApprovals", querySingleCount(con, pendingApprovalsSql));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return stats;
    }

    private int querySingleCount(Connection con, String sql) throws Exception {
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("total");
            }
        }

        return 0;
    }
}