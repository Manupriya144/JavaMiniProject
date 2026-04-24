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
    /**
     * Students who have at least one attendance row (for medical / eligibility pickers).
     */
    public List<Map<String, Object>> getStudentsWithAttendance() {
        String sql = "SELECT DISTINCT s.user_id, s.reg_no, u.username " +
                "FROM attendance a " +
                "INNER JOIN students s ON a.student_id = s.user_id " +
                "INNER JOIN users u ON s.user_id = u.user_id " +
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


    //  * Course IDs the student has attendance for, restricted to their department via timetable


    public List<Map<String, Object>> getCourseIdsForStudentDepartmentAttendance(String studentUserId) {
        String sql = "SELECT DISTINCT se.course_id, COALESCE(c.name, '') AS course_name " +
                "FROM attendance a " +
                "INNER JOIN session se ON a.session_id = se.session_id " +
                "INNER JOIN students st ON a.student_id = st.user_id " +
                "LEFT JOIN course c ON c.course_id = se.course_id " +
                "WHERE a.student_id = ? " +
                "ORDER BY se.course_id";
        List<Map<String, Object>> rows = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, studentUserId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("courseId", rs.getString("course_id"));
                    row.put("courseName", rs.getString("course_name"));
                    rows.add(row);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rows;
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

    public List<Map<String, Object>> getStudentAttendance(String studentId, String viewType) {
        String sql = "SELECT a.attendance_id, a.student_id, s.reg_no, u.username, a.session_id, se.course_id, " +
                "se.session_date, se.type, a.status, a.hours_attended " +
                "FROM attendance a " +
                "INNER JOIN students s ON a.student_id = s.user_id " +
                "INNER JOIN users u ON s.user_id = u.user_id " +
                "INNER JOIN session se ON a.session_id = se.session_id " +
                "WHERE a.student_id = ? AND (? = 'Combined' OR se.type = ?) " +
                "ORDER BY se.session_date DESC, a.attendance_id DESC";
        List<Map<String, Object>> rows = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, studentId);
            stmt.setString(2, viewType);
            stmt.setString(3, viewType);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("attendanceId", rs.getInt("attendance_id"));
                    row.put("studentId", rs.getString("student_id"));
                    row.put("regNo", rs.getString("reg_no"));
                    row.put("studentName", rs.getString("username"));
                    row.put("sessionId", rs.getInt("session_id"));
                    row.put("courseId", rs.getString("course_id"));
                    row.put("sessionDate", rs.getString("session_date"));
                    row.put("sessionType", rs.getString("type"));
                    row.put("status", rs.getString("status"));
                    row.put("hoursAttended", rs.getDouble("hours_attended"));
                    rows.add(row);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rows;
    }

    public List<Map<String, Object>> getBatchAttendance(String batch, String viewType) {
        String sql = "SELECT a.attendance_id, a.student_id, s.reg_no, u.username, s.batch, a.session_id, se.course_id, " +
                "se.session_date, se.type, a.status, a.hours_attended " +
                "FROM attendance a " +
                "INNER JOIN students s ON a.student_id = s.user_id " +
                "INNER JOIN users u ON s.user_id = u.user_id " +
                "INNER JOIN session se ON a.session_id = se.session_id " +
                "WHERE s.batch = ? AND (? = 'Combined' OR se.type = ?) " +
                "ORDER BY s.reg_no ASC, se.session_date DESC, a.attendance_id DESC";
        List<Map<String, Object>> rows = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, batch);
            stmt.setString(2, viewType);
            stmt.setString(3, viewType);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("attendanceId", rs.getInt("attendance_id"));
                    row.put("studentId", rs.getString("student_id"));
                    row.put("regNo", rs.getString("reg_no"));
                    row.put("studentName", rs.getString("username"));
                    row.put("batch", rs.getString("batch"));
                    row.put("sessionId", rs.getInt("session_id"));
                    row.put("courseId", rs.getString("course_id"));
                    row.put("sessionDate", rs.getString("session_date"));
                    row.put("sessionType", rs.getString("type"));
                    row.put("status", rs.getString("status"));
                    row.put("hoursAttended", rs.getDouble("hours_attended"));
                    rows.add(row);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rows;
    }

    public Map<String, Object> getStudentAttendanceSummary(String studentId, String viewType) {
        String sql = "SELECT a.student_id, s.reg_no, u.username, " +
                "COUNT(*) AS totalSessions, " +
                "SUM(CASE WHEN a.status = 'Present' THEN 1 ELSE 0 END) AS presentCount, " +
                "SUM(CASE WHEN a.status = 'Absent' THEN 1 ELSE 0 END) AS absentCount, " +
                "SUM(CASE WHEN a.status = 'Present' THEN a.hours_attended ELSE 0 END) AS totalHoursAttended " +
                "FROM attendance a " +
                "INNER JOIN students s ON a.student_id = s.user_id " +
                "INNER JOIN users u ON s.user_id = u.user_id " +
                "INNER JOIN session se ON a.session_id = se.session_id " +
                "WHERE a.student_id = ? AND (? = 'Combined' OR se.type = ?) " +
                "GROUP BY a.student_id, s.reg_no, u.username";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, studentId);
            stmt.setString(2, viewType);
            stmt.setString(3, viewType);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int total = rs.getInt("totalSessions");
                    int present = rs.getInt("presentCount");
                    double percentage = total == 0 ? 0.0 : (present * 100.0) / total;

                    Map<String, Object> row = new HashMap<>();
                    row.put("studentId", rs.getString("student_id"));
                    row.put("regNo", rs.getString("reg_no"));
                    row.put("studentName", rs.getString("username"));
                    row.put("viewType", viewType);
                    row.put("totalSessions", total);
                    row.put("presentCount", present);
                    row.put("absentCount", rs.getInt("absentCount"));
                    row.put("totalHoursAttended", rs.getDouble("totalHoursAttended"));
                    row.put("attendancePercentage", Math.round(percentage * 100.0) / 100.0);
                    return row;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Map<String, Object>> getBatchAttendanceSummary(String batch, String viewType) {
        String sql = "SELECT a.student_id, s.reg_no, u.username, s.batch, " +
                "COUNT(*) AS totalSessions, " +
                "SUM(CASE WHEN a.status = 'Present' THEN 1 ELSE 0 END) AS presentCount, " +
                "SUM(CASE WHEN a.status = 'Absent' THEN 1 ELSE 0 END) AS absentCount, " +
                "SUM(CASE WHEN a.status = 'Present' THEN a.hours_attended ELSE 0 END) AS totalHoursAttended " +
                "FROM attendance a " +
                "INNER JOIN students s ON a.student_id = s.user_id " +
                "INNER JOIN users u ON s.user_id = u.user_id " +
                "INNER JOIN session se ON a.session_id = se.session_id " +
                "WHERE s.batch = ? AND (? = 'Combined' OR se.type = ?) " +
                "GROUP BY a.student_id, s.reg_no, u.username, s.batch " +
                "ORDER BY s.reg_no ASC";
        List<Map<String, Object>> rows = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, batch);
            stmt.setString(2, viewType);
            stmt.setString(3, viewType);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int total = rs.getInt("totalSessions");
                    int present = rs.getInt("presentCount");
                    double percentage = total == 0 ? 0.0 : (present * 100.0) / total;

                    Map<String, Object> row = new HashMap<>();
                    row.put("studentId", rs.getString("student_id"));
                    row.put("regNo", rs.getString("reg_no"));
                    row.put("studentName", rs.getString("username"));
                    row.put("batch", rs.getString("batch"));
                    row.put("viewType", viewType);
                    row.put("totalSessions", total);
                    row.put("presentCount", present);
                    row.put("absentCount", rs.getInt("absentCount"));
                    row.put("totalHoursAttended", rs.getDouble("totalHoursAttended"));
                    row.put("attendancePercentage", Math.round(percentage * 100.0) / 100.0);
                    rows.add(row);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rows;
    }

    public boolean hasAttendanceMedicalRecord(String studentId) {
        String sql = "SELECT 1 FROM medical WHERE student_id = ? AND exam_type = 'Attendance' AND status = 'Approved' LIMIT 1";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Map<String, Object>> getBatchAttendanceEligibilityReport(String batch, String viewType) {
        String sql = "SELECT a.student_id, s.reg_no, u.username, s.batch, " +
                "COUNT(*) AS totalSessions, " +
                "SUM(CASE WHEN a.status = 'Present' THEN 1 ELSE 0 END) AS presentCount, " +
                "SUM(CASE WHEN a.status = 'Absent' THEN 1 ELSE 0 END) AS absentCount, " +
                "SUM(CASE WHEN a.status = 'Present' THEN a.hours_attended ELSE 0 END) AS totalHoursAttended, " +
                "(SELECT COUNT(*) FROM medical m WHERE m.student_id = s.user_id AND m.exam_type = 'Attendance' AND m.status = 'Approved') AS medicalCount " +
                "FROM attendance a " +
                "INNER JOIN students s ON a.student_id = s.user_id " +
                "INNER JOIN users u ON s.user_id = u.user_id " +
                "INNER JOIN session se ON a.session_id = se.session_id " +
                "WHERE s.batch = ? AND (? = 'Combined' OR se.type = ?) " +
                "GROUP BY a.student_id, s.reg_no, u.username, s.batch " +
                "ORDER BY s.reg_no ASC";
        List<Map<String, Object>> rows = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, batch);
            stmt.setString(2, viewType);
            stmt.setString(3, viewType);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int total = rs.getInt("totalSessions");
                    int present = rs.getInt("presentCount");
                    double percentage = total == 0 ? 0.0 : (present * 100.0) / total;
                    int medicalCount = rs.getInt("medicalCount");

                    Map<String, Object> row = new HashMap<>();
                    row.put("studentId", rs.getString("student_id"));
                    row.put("regNo", rs.getString("reg_no"));
                    row.put("studentName", rs.getString("username"));
                    row.put("batch", rs.getString("batch"));
                    row.put("viewType", viewType);
                    row.put("totalSessions", total);
                    row.put("presentCount", present);
                    row.put("absentCount", rs.getInt("absentCount"));
                    row.put("totalHoursAttended", rs.getDouble("totalHoursAttended"));
                    row.put("attendancePercentage", Math.round(percentage * 100.0) / 100.0);
                    row.put("medicalCount", medicalCount);
                    rows.add(row);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rows;
    }
}
