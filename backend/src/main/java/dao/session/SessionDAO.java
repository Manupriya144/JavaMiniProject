package dao.session;

import model.Session;
import utility.DataSource;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SessionDAO {

    public boolean isLecturerAssignedToCourse(String lecturerId, String courseId) {
        String sql = "SELECT 1 FROM lecturer_course WHERE lecturer_id = ? AND course_id = ?";

        try (Connection connection = DataSource.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, lecturerId);
            ps.setString(2, courseId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Session createSession(Session session) {
        String sql = "INSERT INTO session (course_id, session_date, session_hours, type) VALUES (?, ?, ?, ?)";

        try (Connection connection = DataSource.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, session.getCourseId());
            ps.setDate(2, Date.valueOf(session.getSessionDate()));
            ps.setDouble(3, session.getSessionHours());
            ps.setString(4, session.getType());

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        session.setSessionId(rs.getInt(1));
                        return session;
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}