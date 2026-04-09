package dao.medical;

import model.Medical;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MedicalDAO {
    private final Connection connection;

    public MedicalDAO(Connection connection) {
        this.connection = connection;
    }

    public Medical addMedical(Medical medical) {
        String sql = "INSERT INTO medical (student_id, course_id, exam_type, date_submitted, medical_copy, status) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, medical.getStudentId());
            stmt.setString(2, medical.getCourseId());
            stmt.setString(3, medical.getExamType());
            stmt.setString(4, medical.getDateSubmitted());
            stmt.setString(5, medical.getMedicalCopy());
            stmt.setString(6, medical.getStatus());
            int rows = stmt.executeUpdate();
            if (rows == 0) {
                return null;
            }
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    medical.setMedicalId(rs.getInt(1));
                }
            }
            return medical;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean updateMedical(Medical medical) {
        String sql = "UPDATE medical SET student_id = ?, course_id = ?, exam_type = ?, date_submitted = ?, medical_copy = ? " +
                "WHERE medical_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, medical.getStudentId());
            stmt.setString(2, medical.getCourseId());
            stmt.setString(3, medical.getExamType());
            stmt.setString(4, medical.getDateSubmitted());
            stmt.setString(5, medical.getMedicalCopy());
            stmt.setInt(6, medical.getMedicalId());
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateStatus(int medicalId, String status) {
        String sql = "UPDATE medical SET status = ? WHERE medical_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, medicalId);
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Medical> getStudentMedicalRecords(String studentId) {
        String sql = "SELECT medical_id, student_id, course_id, exam_type, date_submitted, medical_copy, status " +
                "FROM medical WHERE student_id = ? ORDER BY date_submitted DESC, medical_id DESC";
        List<Medical> list = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapMedical(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Medical> getBatchMedicalRecords(String batch) {
        String sql = "SELECT m.medical_id, m.student_id, m.course_id, m.exam_type, m.date_submitted, m.medical_copy, m.status " +
                "FROM medical m INNER JOIN students s ON m.student_id = s.user_id " +
                "WHERE s.batch = ? ORDER BY m.date_submitted DESC, m.medical_id DESC";
        List<Medical> list = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, batch);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapMedical(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean hasAttendanceSessionForDate(String studentId, String courseId, String sessionDate) {
        String sql = "SELECT 1 FROM attendance a " +
                "INNER JOIN session s ON a.session_id = s.session_id " +
                "WHERE a.student_id = ? AND s.course_id = ? AND s.session_date = ? LIMIT 1";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, studentId);
            stmt.setString(2, courseId);
            stmt.setString(3, sessionDate);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private Medical mapMedical(ResultSet rs) throws Exception {
        return new Medical(
                rs.getInt("medical_id"),
                rs.getString("student_id"),
                rs.getString("course_id"),
                rs.getString("exam_type"),
                rs.getString("date_submitted"),
                rs.getString("medical_copy"),
                rs.getString("status")
        );
    }
}
