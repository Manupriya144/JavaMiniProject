package dao.grade;

import model.Grade;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class GradeDAO {

    private final Connection connection;

    public GradeDAO(Connection connection) {
        this.connection = connection;
    }

    public boolean insertGrade(Grade grade) {
        String sql = "INSERT INTO course_result(student_id, course_id, academic_year, academic_level, semester, grade) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, grade.getStudentId());
            ps.setString(2, grade.getCourseId());
            ps.setInt(3, grade.getAcademicYear());
            ps.setInt(4, 1);
            ps.setString(5, String.valueOf(grade.getSemester()));
            ps.setString(6, grade.getGrade());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Grade getStudentGrade(String studentId, String courseId) {
        return getStudentGrade(studentId, courseId, null, null);
    }

    public Grade getStudentGrade(String studentId, String courseId, Integer academicYear, Integer semester) {
        StringBuilder sql = new StringBuilder("SELECT * FROM course_result WHERE student_id = ? AND course_id = ?");
        if (academicYear != null) {
            sql.append(" AND academic_year = ?");
        }
        if (semester != null) {
            sql.append(" AND semester = ?");
        }
        sql.append(" ORDER BY academic_year DESC, semester DESC LIMIT 1");

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            int idx = 1;
            ps.setString(idx++, studentId);
            ps.setString(idx++, courseId);
            if (academicYear != null) {
                ps.setInt(idx++, academicYear);
            }
            if (semester != null) {
                ps.setString(idx, String.valueOf(semester));
            }
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Grade(
                        rs.getString("student_id"),
                        rs.getString("course_id"),
                        rs.getInt("academic_year"),
                        Integer.parseInt(rs.getString("semester")),
                        rs.getString("grade")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Grade> getBatchGrades(int academicYear, int semester) {
        List<Grade> list = new ArrayList<>();
        String sql = "SELECT * FROM course_result WHERE academic_year = ? AND semester = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, academicYear);
            ps.setString(2, String.valueOf(semester));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Grade(
                        rs.getString("student_id"),
                        rs.getString("course_id"),
                        rs.getInt("academic_year"),
                        Integer.parseInt(rs.getString("semester")),
                        rs.getString("grade")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public Set<String> getDistinctStudentIds() {
        Set<String> values = new LinkedHashSet<>();
        String sql = "SELECT DISTINCT student_id FROM course_result ORDER BY student_id";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                values.add(rs.getString("student_id"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return values;
    }

    public Set<String> getDistinctCourseIds(String studentId) {
        Set<String> values = new LinkedHashSet<>();
        String sql = "SELECT DISTINCT course_id FROM course_result WHERE student_id = ? ORDER BY course_id";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                values.add(rs.getString("course_id"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return values;
    }

    public Set<Integer> getDistinctAcademicYears(String studentId, String courseId) {
        Set<Integer> values = new LinkedHashSet<>();
        String sql = "SELECT DISTINCT academic_year FROM course_result WHERE student_id = ? AND course_id = ? ORDER BY academic_year";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, studentId);
            ps.setString(2, courseId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                values.add(rs.getInt("academic_year"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return values;
    }

    public Set<Integer> getDistinctAcademicYears() {
        Set<Integer> values = new LinkedHashSet<>();
        String sql = "SELECT DISTINCT academic_year FROM course_result ORDER BY academic_year";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                values.add(rs.getInt("academic_year"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return values;
    }

    public Set<Integer> getDistinctSemesters(String studentId, String courseId, Integer academicYear) {
        Set<Integer> values = new LinkedHashSet<>();
        String sql = "SELECT DISTINCT semester FROM course_result WHERE student_id = ? AND course_id = ?"
                + (academicYear == null ? "" : " AND academic_year = ?")
                + " ORDER BY semester";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, studentId);
            ps.setString(2, courseId);
            if (academicYear != null) {
                ps.setInt(3, academicYear);
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                values.add(Integer.parseInt(rs.getString("semester")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return values;
    }

    public Set<Integer> getDistinctSemesters() {
        Set<Integer> values = new LinkedHashSet<>();
        String sql = "SELECT DISTINCT semester FROM course_result ORDER BY semester";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                values.add(Integer.parseInt(rs.getString("semester")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return values;
    }

    public Set<String> getDistinctGrades(String studentId, String courseId, Integer academicYear, Integer semester) {
        Set<String> values = new LinkedHashSet<>();
        StringBuilder sql = new StringBuilder("SELECT DISTINCT grade FROM course_result WHERE 1=1");
        List<Object> params = new ArrayList<>();
        if (studentId != null && !studentId.isBlank()) {
            sql.append(" AND student_id = ?");
            params.add(studentId);
        }
        if (courseId != null && !courseId.isBlank()) {
            sql.append(" AND course_id = ?");
            params.add(courseId);
        }
        if (academicYear != null) {
            sql.append(" AND academic_year = ?");
            params.add(academicYear);
        }
        if (semester != null) {
            sql.append(" AND semester = ?");
            params.add(String.valueOf(semester));
        }
        sql.append(" ORDER BY grade");

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                Object value = params.get(i);
                if (value instanceof Integer) {
                    ps.setInt(i + 1, (Integer) value);
                } else {
                    ps.setString(i + 1, String.valueOf(value));
                }
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                values.add(rs.getString("grade"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return values;
    }
}
