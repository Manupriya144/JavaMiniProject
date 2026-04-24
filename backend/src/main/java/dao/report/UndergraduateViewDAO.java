package dao.report;

import utility.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UndergraduateViewDAO {

    public List<Map<String, Object>> getMyAttendance(String studentId) {
        String sql = "SELECT a.attendance_id, a.student_id, a.session_id, se.course_id, se.session_date, se.type, " +
                "a.status, a.hours_attended " +
                "FROM attendance a " +
                "INNER JOIN session se ON se.session_id = a.session_id " +
                "WHERE a.student_id = ? " +
                "ORDER BY se.session_date DESC, a.attendance_id DESC";

        List<Map<String, Object>> rows = new ArrayList<>();

        try (Connection connection = DataSource.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("attendanceId", rs.getInt("attendance_id"));
                    row.put("studentId", rs.getString("student_id"));
                    row.put("sessionId", rs.getInt("session_id"));
                    row.put("courseId", rs.getString("course_id"));
                    row.put("sessionDate", rs.getString("session_date"));
                    row.put("type", rs.getString("type"));
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

    public List<Map<String, Object>> getMyMedicalRecords(String studentId) {
        String sql = "SELECT medical_id, student_id, course_id, exam_type, date_submitted, medical_copy, status " +
                "FROM medical WHERE student_id = ? ORDER BY date_submitted DESC, medical_id DESC";

        List<Map<String, Object>> rows = new ArrayList<>();
        try (Connection connection = DataSource.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("medicalId", rs.getInt("medical_id"));
                    row.put("studentId", rs.getString("student_id"));
                    row.put("courseId", rs.getString("course_id"));
                    row.put("examType", rs.getString("exam_type"));
                    row.put("dateSubmitted", rs.getString("date_submitted"));
                    row.put("medicalCopy", rs.getString("medical_copy"));
                    row.put("status", rs.getString("status"));
                    rows.add(row);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return rows;
    }

    public List<Map<String, Object>> getMyCourses(String studentId) {
        String sql = "SELECT cr.student_id, cr.course_id, c.course_code, c.name AS course_name, c.course_credit, " +
                "cr.academic_year, cr.semester, cr.registration_type " +
                "FROM course_registration cr " +
                "INNER JOIN course c ON c.course_id = cr.course_id " +
                "WHERE cr.student_id = ? ORDER BY cr.academic_year DESC, cr.semester DESC, c.course_code";

        List<Map<String, Object>> rows = new ArrayList<>();
        try (Connection connection = DataSource.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("studentId", rs.getString("student_id"));
                    row.put("courseId", rs.getString("course_id"));
                    row.put("courseCode", rs.getString("course_code"));
                    row.put("courseName", rs.getString("course_name"));
                    row.put("courseCredit", rs.getInt("course_credit"));
                    row.put("academicYear", rs.getInt("academic_year"));
                    row.put("semester", rs.getString("semester"));
                    row.put("registrationType", rs.getString("registration_type"));
                    rows.add(row);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return rows;
    }

    public List<Map<String, Object>> getMyMarks(String studentId) {
        String sql = "SELECT sm.mark_id, sm.student_id, at.assessment_type_id, at.course_id, at.name AS assessment_name, " +
                "at.component, at.weight, sm.marks " +
                "FROM student_marks sm " +
                "INNER JOIN assessment_type at ON at.assessment_type_id = sm.assessment_type_id " +
                "WHERE sm.student_id = ? ORDER BY at.course_id, at.assessment_type_id";

        List<Map<String, Object>> rows = new ArrayList<>();
        try (Connection connection = DataSource.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("markId", rs.getInt("mark_id"));
                    row.put("studentId", rs.getString("student_id"));
                    row.put("assessmentTypeId", rs.getInt("assessment_type_id"));
                    row.put("courseId", rs.getString("course_id"));
                    row.put("assessmentName", rs.getString("assessment_name"));
                    row.put("component", rs.getString("component"));
                    row.put("weight", rs.getDouble("weight"));
                    row.put("marks", rs.getDouble("marks"));
                    rows.add(row);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return rows;
    }

    public List<Map<String, Object>> getMyGrades(String studentId) {
        String sql = "SELECT result_id, student_id, course_id, academic_year, academic_level, semester, total_marks, grade " +
                "FROM course_result WHERE student_id = ? ORDER BY academic_year DESC, semester DESC, course_id";

        List<Map<String, Object>> rows = new ArrayList<>();
        try (Connection connection = DataSource.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("resultId", rs.getInt("result_id"));
                    row.put("studentId", rs.getString("student_id"));
                    row.put("courseId", rs.getString("course_id"));
                    row.put("academicYear", rs.getInt("academic_year"));
                    row.put("academicLevel", rs.getInt("academic_level"));
                    row.put("semester", rs.getString("semester"));
                    row.put("totalMarks", rs.getDouble("total_marks"));
                    row.put("grade", rs.getString("grade"));
                    rows.add(row);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return rows;
    }

    public Map<String, Object> getMyTimetable(String studentId) {
        String sql = "SELECT t.timetable_id, t.department_id, t.academic_level, t.semester, t.title, t.pdf_file_path, t.uploaded_at " +
                "FROM students s " +
                "INNER JOIN timetable t ON t.department_id = s.department_id AND t.academic_level = s.academic_level " +
                "WHERE s.user_id = ? " +
                "ORDER BY t.uploaded_at DESC LIMIT 1";

        try (Connection connection = DataSource.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("timetableId", rs.getInt("timetable_id"));
                    row.put("departmentId", rs.getString("department_id"));
                    row.put("academicLevel", rs.getInt("academic_level"));
                    row.put("semester", rs.getString("semester"));
                    row.put("title", rs.getString("title"));
                    row.put("pdfFilePath", rs.getString("pdf_file_path"));
                    row.put("uploadedAt", rs.getString("uploaded_at"));
                    return row;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<Map<String, Object>> getAllNotices() {
        String sql = "SELECT notice_id, title, description, pdf_file_path, created_by, created_at " +
                "FROM notice ORDER BY created_at DESC, notice_id DESC";

        List<Map<String, Object>> rows = new ArrayList<>();
        try (Connection connection = DataSource.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("noticeId", rs.getInt("notice_id"));
                row.put("title", rs.getString("title"));
                row.put("description", rs.getString("description"));
                row.put("pdfFilePath", rs.getString("pdf_file_path"));
                row.put("createdBy", rs.getString("created_by"));
                row.put("createdAt", rs.getString("created_at"));
                rows.add(row);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return rows;
    }

    public List<Map<String, Object>> getCourseMaterials(String courseId) {
        String sql = "SELECT material_id, course_id, lecturer_id, title, file_path, uploaded_at " +
                "FROM course_material WHERE course_id = ? ORDER BY uploaded_at DESC";

        List<Map<String, Object>> rows = new ArrayList<>();
        try (Connection connection = DataSource.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("materialId", rs.getInt("material_id"));
                    row.put("courseId", rs.getString("course_id"));
                    row.put("lecturerId", rs.getString("lecturer_id"));
                    row.put("title", rs.getString("title"));
                    row.put("filePath", rs.getString("file_path"));
                    row.put("uploadedAt", rs.getString("uploaded_at"));
                    rows.add(row);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return rows;
    }
}
