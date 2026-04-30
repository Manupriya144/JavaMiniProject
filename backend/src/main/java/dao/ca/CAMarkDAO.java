package dao.ca;

import model.ca.CAMark;
import utility.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CAMarkDAO {

    public CAMark uploadCAMark(String studentId, Integer assessmentTypeId, Double marks) {
        String sql = "INSERT INTO student_marks (student_id, assessment_type_id, marks) VALUES (?, ?, ?)";
        try (Connection con = DataSource.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, studentId);
            stmt.setInt(2, assessmentTypeId);
            stmt.setDouble(3, marks);
            if (stmt.executeUpdate() == 0) {
                return null;
            }
            Integer markId = null;
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    markId = rs.getInt(1);
                }
            }
            return getMarkById(markId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String findStudentIdByRegNo(String regNo) {
        String sql = "SELECT user_id FROM students WHERE reg_no = ?";
        try (Connection con = DataSource.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, regNo);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("user_id");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateCAMark(Integer markId, Double marks) {
        String sql = "UPDATE student_marks SET marks = ? WHERE mark_id = ?";
        try (Connection con = DataSource.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setDouble(1, marks);
            stmt.setInt(2, markId);
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<CAMark> getStudentCAMarks(String studentId, String courseId) {
        String sql = "SELECT sm.mark_id, sm.student_id, sm.assessment_type_id, at.course_id, at.name, at.weight, sm.marks " +
                "FROM student_marks sm INNER JOIN assessment_type at ON sm.assessment_type_id = at.assessment_type_id " +
                "WHERE sm.student_id = ? AND at.component = 'CA' AND (? IS NULL OR at.course_id = ?) " +
                "ORDER BY at.course_id, at.assessment_type_id";
        List<CAMark> rows = new ArrayList<>();
        try (Connection con = DataSource.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, studentId);
            stmt.setString(2, courseId);
            stmt.setString(3, courseId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    rows.add(mapRow(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rows;
    }

    public List<CAMark> getBatchCAMarks(String batch, String courseId) {
        String sql = "SELECT sm.mark_id, sm.student_id, sm.assessment_type_id, at.course_id, at.name, at.weight, sm.marks " +
                "FROM student_marks sm " +
                "INNER JOIN assessment_type at ON sm.assessment_type_id = at.assessment_type_id " +
                "INNER JOIN students s ON sm.student_id = s.user_id " +
                "WHERE s.batch = ? AND at.component = 'CA' AND (? IS NULL OR at.course_id = ?) " +
                "ORDER BY s.reg_no, at.course_id, at.assessment_type_id";
        List<CAMark> rows = new ArrayList<>();
        try (Connection con = DataSource.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, batch);
            stmt.setString(2, courseId);
            stmt.setString(3, courseId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    rows.add(mapRow(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rows;
    }

    public Map<String, Object> getStudentCAEligibility(String studentId, String courseId) {
        String sql = "SELECT sm.student_id, at.course_id, " +
                "SUM(sm.marks * at.weight / 100) AS weightedScore, " +
                "SUM(at.weight) AS totalCAWeight " +
                "FROM student_marks sm " +
                "INNER JOIN assessment_type at ON sm.assessment_type_id = at.assessment_type_id " +
                "WHERE sm.student_id = ? AND at.component = 'CA' AND at.course_id = ? " +
                "GROUP BY sm.student_id, at.course_id";

        try (Connection con = DataSource.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, studentId);
            stmt.setString(2, courseId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("studentId", rs.getString("student_id"));
                    row.put("courseId", rs.getString("course_id"));
                    row.put("weightedScore", rs.getDouble("weightedScore"));
                    row.put("totalCAWeight", rs.getDouble("totalCAWeight"));
                    return row;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Map<String, Object>> getBatchCAEligibilityReport(String batch, String courseId) {
        String sql = "SELECT sm.student_id, s.reg_no, u.username, s.batch, at.course_id, " +
                "SUM(sm.marks * at.weight / 100) AS weightedScore, " +
                "SUM(at.weight) AS totalCAWeight " +
                "FROM student_marks sm " +
                "INNER JOIN assessment_type at ON sm.assessment_type_id = at.assessment_type_id " +
                "INNER JOIN students s ON sm.student_id = s.user_id " +
                "INNER JOIN users u ON s.user_id = u.user_id " +
                "WHERE s.batch = ? AND at.component = 'CA' AND at.course_id = ? " +
                "GROUP BY sm.student_id, s.reg_no, u.username, s.batch, at.course_id " +
                "ORDER BY s.reg_no ASC";

        List<Map<String, Object>> rows = new ArrayList<>();
        try (Connection con = DataSource.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {


            stmt.setString(1, batch);
            stmt.setString(2, courseId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("studentId", rs.getString("student_id"));
                    row.put("regNo", rs.getString("reg_no"));
                    row.put("studentName", rs.getString("username"));
                    row.put("batch", rs.getString("batch"));
                    row.put("courseId", rs.getString("course_id"));
                    row.put("weightedScore", rs.getDouble("weightedScore"));
                    row.put("totalCAWeight", rs.getDouble("totalCAWeight"));
                    rows.add(row);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rows;
    }

    public boolean isCourseAssignedToLecturer(String lecturerId, String courseId) {
        String sql = "SELECT 1 FROM lecturer_course WHERE lecturer_id = ? AND course_id = ?";
        try (Connection con = DataSource.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, lecturerId);
            stmt.setString(2, courseId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isAssessmentTypeAssignedToLecturer(String lecturerId, Integer assessmentTypeId) {
        String sql = "SELECT 1 " +
                "FROM assessment_type at " +
                "INNER JOIN lecturer_course lc ON lc.course_id = at.course_id " +
                "WHERE lc.lecturer_id = ? AND at.assessment_type_id = ?";

        try (Connection con = DataSource.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, lecturerId);
            stmt.setInt(2, assessmentTypeId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isMarkAssignedToLecturer(String lecturerId, Integer markId) {
        String sql = "SELECT 1 " +
                "FROM student_marks sm " +
                "INNER JOIN assessment_type at ON at.assessment_type_id = sm.assessment_type_id " +
                "INNER JOIN lecturer_course lc ON lc.course_id = at.course_id " +
                "WHERE lc.lecturer_id = ? AND sm.mark_id = ?";
        try (Connection con = DataSource.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, lecturerId);
            stmt.setInt(2, markId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Map<String, Object>> getCourseCAAssessmentTypes(String courseId) {
        List<Map<String, Object>> rows = loadAssessmentTypes(
                "SELECT assessment_type_id, name, weight " +
                        "FROM assessment_type " +
                        "WHERE course_id = ? AND component = 'CA' " +
                        "ORDER BY assessment_type_id",
                courseId
        );

        if (!rows.isEmpty()) {
            return rows;
        }

        // Fallback for older data where CA assignments were saved without the component flag.
        return loadAssessmentTypes(
                "SELECT assessment_type_id, name, weight " +
                        "FROM assessment_type " +
                        "WHERE course_id = ? AND UPPER(name) <> 'FINAL' " +
                        "ORDER BY assessment_type_id",
                courseId
        );
    }

    public List<Map<String, Object>> getCourseCAMarkEntries(String courseId) {
        String sql = "SELECT sm.mark_id, sm.student_id, s.reg_no, sm.assessment_type_id, at.name, sm.marks " +
                "FROM student_marks sm " +
                "INNER JOIN assessment_type at ON at.assessment_type_id = sm.assessment_type_id " +
                "LEFT JOIN students s ON s.user_id = sm.student_id " +
                "WHERE at.course_id = ? AND at.component = 'CA' " +
                "ORDER BY sm.mark_id";
        List<Map<String, Object>> rows = new ArrayList<>();
        try (Connection con = DataSource.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, courseId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("markId", rs.getInt("mark_id"));
                    row.put("studentId", rs.getString("student_id"));
                    row.put("studentRegNo", rs.getString("reg_no"));
                    row.put("assessmentTypeId", rs.getInt("assessment_type_id"));
                    row.put("assessmentName", rs.getString("name"));
                    row.put("marks", rs.getDouble("marks"));
                    rows.add(row);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rows;
    }

    private CAMark getMarkById(Integer markId) {
        if (markId == null) {
            return null;
        }
        String sql = "SELECT sm.mark_id, sm.student_id, sm.assessment_type_id, at.course_id, at.name, at.weight, sm.marks " +
                "FROM student_marks sm INNER JOIN assessment_type at ON sm.assessment_type_id = at.assessment_type_id " +
                "WHERE sm.mark_id = ?";
        try (Connection con = DataSource.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, markId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<Map<String, Object>> loadAssessmentTypes(String sql, String courseId) {
        List<Map<String, Object>> rows = new ArrayList<>();
        try (Connection con = DataSource.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, courseId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("assessmentTypeId", rs.getInt("assessment_type_id"));
                    row.put("assessmentName", rs.getString("name"));
                    row.put("weight", rs.getDouble("weight"));
                    rows.add(row);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rows;
    }

    private CAMark mapRow(ResultSet rs) throws Exception {
        return new CAMark(
                rs.getInt("mark_id"),
                rs.getString("student_id"),
                rs.getInt("assessment_type_id"),
                rs.getString("course_id"),
                rs.getString("name"),
                rs.getDouble("weight"),
                rs.getDouble("marks")
        );
    }
}
