package dao.result;

import dto.requestDto.result.GradeGPAFilterDTO;
import dto.responseDto.result.GradeGPAReportDTO;
import dto.responseDto.result.GradeGPARowDTO;
import utility.DataSource;

import java.sql.*;
import java.util.*;

public class GradeGPADAO {

    public GradeGPAReportDTO generateReport(GradeGPAFilterDTO filter) {
        GradeGPAReportDTO report = new GradeGPAReportDTO();

        try (Connection con = DataSource.getInstance().getConnection()) {

            List<Course> courses = getCourses(con, filter);

            for (Course course : courses) {
                report.getCourses().add(course.courseCode());
            }

            List<Student> students = getStudents(con, filter);

            for (Student student : students) {
                GradeGPARowDTO row = new GradeGPARowDTO();

                row.setStudentId(student.studentId());
                row.setRegNo(student.regNo());
                row.setStudentName(student.name());

                double totalPoints = 0.0;
                int totalCredits = 0;

                for (Course course : courses) {
                    String grade = getGrade(con, student.studentId(), course.courseId(), filter);

                    row.getCourseGrades().put(course.courseCode(), grade);

                    if (shouldCountCredits(grade)) {
                        totalPoints += gradePoint(grade) * course.credit();
                        totalCredits += course.credit();
                    }
                }

                double sgpa = totalCredits == 0 ? 0.0 : totalPoints / totalCredits;
                double cgpa = calculateCGPA(con, student.studentId(), sgpa, totalCredits, filter);

                row.setTotalCredits(totalCredits);
                row.setSgpa(round(sgpa));
                row.setCgpa(round(cgpa));

                report.getRows().add(row);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return report;
    }

    private List<Course> getCourses(Connection con, GradeGPAFilterDTO filter) throws SQLException {
        List<Course> courses = new ArrayList<>();

        String sql = """
            SELECT DISTINCT 
                c.course_id, 
                c.course_code, 
                c.course_credit
            FROM course_result cr
            INNER JOIN course c ON cr.course_id = c.course_id
            WHERE cr.academic_year = ?
              AND cr.academic_level = ?
              AND cr.semester = ?
              AND c.department_id = ?
            ORDER BY c.course_code
            """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, filter.getAcademicYear());
            ps.setInt(2, filter.getAcademicLevel());
            ps.setString(3, filter.getSemester());
            ps.setString(4, filter.getDepartmentId());

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                courses.add(new Course(
                        rs.getString("course_id"),
                        rs.getString("course_code"),
                        rs.getInt("course_credit")
                ));
            }
        }

        return courses;
    }

    private List<Student> getStudents(Connection con, GradeGPAFilterDTO filter) throws SQLException {
        List<Student> students = new ArrayList<>();

        String sql = """
                SELECT DISTINCT s.user_id, s.reg_no, u.username
                FROM students s
                INNER JOIN users u ON s.user_id = u.user_id
                INNER JOIN course_result cr ON cr.student_id = s.user_id
                INNER JOIN course c ON cr.course_id = c.course_id
                WHERE cr.academic_year = ?
                  AND cr.academic_level = ?
                  AND cr.semester = ?
                  AND c.department_id = ?
                ORDER BY s.reg_no
                """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, filter.getAcademicYear());
            ps.setInt(2, filter.getAcademicLevel());
            ps.setString(3, filter.getSemester());
            ps.setString(4, filter.getDepartmentId());

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                students.add(new Student(
                        rs.getString("user_id"),
                        rs.getString("reg_no"),
                        rs.getString("username")
                ));
            }
        }

        return students;
    }

    private String getGrade(Connection con, String studentId, String courseId, GradeGPAFilterDTO filter) throws SQLException {
        String sql = """
                SELECT grade
                FROM course_result
                WHERE student_id = ?
                  AND course_id = ?
                  AND academic_year = ?
                  AND academic_level = ?
                  AND semester = ?
                """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, studentId);
            ps.setString(2, courseId);
            ps.setInt(3, filter.getAcademicYear());
            ps.setInt(4, filter.getAcademicLevel());
            ps.setString(5, filter.getSemester());

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString("grade");
            }
        }

        return "-";
    }

    private double calculateCGPA(Connection con,
                                 String studentId,
                                 double currentSgpa,
                                 int currentCredits,
                                 GradeGPAFilterDTO filter) throws SQLException {

        String sql = """
                SELECT total_credits, sgpa
                FROM semester_result
                WHERE student_id = ?
                  AND NOT (
                        academic_year = ?
                    AND academic_level = ?
                    AND semester = ?
                  )
                """;

        double totalPoints = currentSgpa * currentCredits;
        int totalCredits = currentCredits;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, studentId);
            ps.setInt(2, filter.getAcademicYear());
            ps.setInt(3, filter.getAcademicLevel());
            ps.setString(4, filter.getSemester());

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int previousCredits = rs.getInt("total_credits");
                double previousSgpa = rs.getDouble("sgpa");

                totalPoints += previousSgpa * previousCredits;
                totalCredits += previousCredits;
            }
        }

        return totalCredits == 0 ? 0.0 : totalPoints / totalCredits;
    }

    private boolean shouldCountCredits(String grade) {
        if (grade == null) return false;

        return !grade.equalsIgnoreCase("WH")
                && !grade.equals("-");
    }

    private double gradePoint(String grade) {
        if (grade == null) return 0.0;

        return switch (grade.toUpperCase()) {
            case "A+", "A" -> 4.0;
            case "A-" -> 3.7;
            case "B+" -> 3.3;
            case "B" -> 3.0;
            case "B-" -> 2.7;
            case "C+" -> 2.3;
            case "C" -> 2.0;
            case "C-" -> 1.7;
            case "D+" -> 1.3;
            case "D" -> 1.0;
            case "F", "EE" -> 0.0;
            default -> 0.0;
        };
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    public boolean saveSemesterResults(GradeGPAFilterDTO filter, List<GradeGPARowDTO> rows) {
        String deleteSql = """
                DELETE FROM semester_result
                WHERE student_id = ?
                  AND academic_year = ?
                  AND academic_level = ?
                  AND semester = ?
                """;

        String insertSql = """
                INSERT INTO semester_result(
                    student_id,
                    academic_year,
                    academic_level,
                    semester,
                    total_credits,
                    sgpa,
                    cgpa
                )
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection con = DataSource.getInstance().getConnection()) {
            con.setAutoCommit(false);

            try {
                for (GradeGPARowDTO row : rows) {
                    try (PreparedStatement ps = con.prepareStatement(deleteSql)) {
                        ps.setString(1, row.getStudentId());
                        ps.setInt(2, filter.getAcademicYear());
                        ps.setInt(3, filter.getAcademicLevel());
                        ps.setString(4, filter.getSemester());
                        ps.executeUpdate();
                    }

                    try (PreparedStatement ps = con.prepareStatement(insertSql)) {
                        ps.setString(1, row.getStudentId());
                        ps.setInt(2, filter.getAcademicYear());
                        ps.setInt(3, filter.getAcademicLevel());
                        ps.setString(4, filter.getSemester());
                        ps.setInt(5, row.getTotalCredits());
                        ps.setDouble(6, row.getSgpa());
                        ps.setDouble(7, row.getCgpa());
                        ps.executeUpdate();
                    }
                }

                con.commit();
                return true;

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

    private record Course(String courseId, String courseCode, int credit) {}
    private record Student(String studentId, String regNo, String name) {}
}