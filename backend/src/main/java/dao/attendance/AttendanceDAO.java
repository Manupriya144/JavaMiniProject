package dao.attendance;

import model.Attendance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AttendanceDAO {
    private final Connection connection;

    public AttendanceDAO(Connection connection) {
        this.connection = connection;
    }

    public Attendance addAttendance(Attendance attendance) {
        String sql = "INSERT INTO attendance (student_id, session_id, status, hours_attended) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, attendance.getStudentId());
            stmt.setInt(2, attendance.getSessionId());
            stmt.setString(3, attendance.getStatus());
            stmt.setDouble(4, attendance.getHoursAttended() == null ? 0.0 : attendance.getHoursAttended());

            int rows = stmt.executeUpdate();
            if (rows == 0) {
                return null;
            }

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    attendance.setAttendanceId(rs.getInt(1));
                }
            }
            return attendance;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database insert failed: " + e.getMessage(), e);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Attendance insert failed", e);
        }
    }

    public boolean updateAttendance(Integer attendanceId, String status, Double hoursAttended) {
        String sql = "UPDATE attendance SET status = ?, hours_attended = ? WHERE attendance_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setDouble(2, hoursAttended == null ? 0.0 : hoursAttended);
            stmt.setInt(3, attendanceId);

            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteAttendance(Integer attendanceId) {
        String sql = "DELETE FROM attendance WHERE attendance_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, attendanceId);
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Attendance getAttendanceById(Integer attendanceId) {
        String sql = "SELECT attendance_id, student_id, session_id, status, hours_attended FROM attendance WHERE attendance_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, attendanceId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Attendance(
                            rs.getInt("attendance_id"),
                            rs.getString("student_id"),
                            rs.getInt("session_id"),
                            rs.getString("status"),
                            rs.getDouble("hours_attended")
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Map<String, Object>> getStudentOptions() {
        String sql = "SELECT s.user_id, s.reg_no, u.username " +
                "FROM students s INNER JOIN users u ON s.user_id = u.user_id " +
                "ORDER BY s.reg_no";
        List<Map<String, Object>> rows = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("userId", rs.getString("user_id"));
                row.put("regNo", rs.getString("reg_no"));
                row.put("username", rs.getString("username"));
                rows.add(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rows;
    }

    public List<Map<String, Object>> getSessionOptions() {
        String sql = "SELECT session_id, course_id, session_date, type FROM session ORDER BY session_date DESC, session_id DESC";
        List<Map<String, Object>> rows = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("sessionId", rs.getInt("session_id"));
                row.put("courseId", rs.getString("course_id"));
                row.put("sessionDate", rs.getString("session_date"));
                row.put("type", rs.getString("type"));
                rows.add(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rows;
    }
}
