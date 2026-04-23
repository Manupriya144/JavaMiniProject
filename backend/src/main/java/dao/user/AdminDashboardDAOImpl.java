package dao.user;



import utility.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminDashboardDAOImpl implements AdminDashboardDAO {

    @Override
    public int getTotalUsers() {
        String sql = "SELECT COUNT(*) FROM users";
        return getCount(sql);
    }

    @Override
    public int getTotalStudents() {
        String sql = "SELECT COUNT(*) FROM users WHERE role = 'Student'";
        return getCount(sql);
    }

    @Override
    public int getTotalLecturers() {
        String sql = "SELECT COUNT(*) FROM users WHERE role = 'Lecturer'";
        return getCount(sql);
    }

    @Override
    public int getTotalTechOfficers() {
        String sql = "SELECT COUNT(*) FROM users WHERE role = 'Tech_Officer'";
        return getCount(sql);
    }

    @Override
    public int getTotalCourses() {
        String sql = "SELECT COUNT(*) FROM course";
        return getCount(sql);
    }

    private int getCount(String sql) {
        try (Connection connection = DataSource.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}