package dao.student;

import dto.requestDto.student.UpdateStudentProfileReqDTO;
import dto.responseDto.student.StudentCourseDashboardDTO;
import dto.responseDto.student.StudentDashboardDTO;
import dto.responseDto.student.StudentRegisteredCourseDTO;
import model.Student;
import model.User;
import utility.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;


public class StudentDAO {


    public Student findByUserId(String userId) {
        String sql = "SELECT s.user_id, s.reg_no, s.batch, s.academic_level, s.department_id " +
                "FROM students s WHERE s.user_id = ?";
        try (Connection connection = DataSource.getInstance().getConnection();
        PreparedStatement ps = connection.prepareStatement(sql))  {
            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Student(
                        rs.getString("user_id"),
                        rs.getString("reg_no"),
                        rs.getString("batch"),
                        rs.getInt("academic_level"),
                        rs.getString("department_id")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Student findStudentById(String userId) {

        String sql = """
                SELECT 
                    u.user_id,
                    u.username,
                    u.email,
                    u.contact_number,
                    u.profile_picture,
                    u.role,
                    s.reg_no,
                    s.batch,
                    s.academic_level,
                    s.department_id
                FROM users u
                LEFT JOIN students s ON u.user_id = s.user_id
                WHERE u.user_id = ?
    """;

        try (Connection connection = DataSource.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, userId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                String role = rs.getString("role");
                if ("Student".equalsIgnoreCase(role)) {

                    return new Student(
                            rs.getString("user_id"),
                            rs.getString("username"),
                            rs.getString("email"),
                            rs.getString("contact_number"),
                            rs.getString("profile_picture"),
                            rs.getString("role"),
                            rs.getString("reg_no"),
                            rs.getString("batch"),
                            rs.getInt("academic_level"),
                            rs.getString("department_id")
                    );
                }

                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean updateStudentProfile(UpdateStudentProfileReqDTO dto) {
        String sqlWithoutPassword = """
            UPDATE users
            SET email = ?, contact_number = ?, profile_picture = ?
            WHERE user_id = ?
            """;

        String sqlWithPassword = """
            UPDATE users
            SET email = ?, contact_number = ?, profile_picture = ?, password = ?
            WHERE user_id = ?
            """;

        try (Connection connection = DataSource.getInstance().getConnection()) {

            boolean hasPassword = dto.getPassword() != null && !dto.getPassword().trim().isEmpty();

            if (hasPassword) {
                try (PreparedStatement ps = connection.prepareStatement(sqlWithPassword)) {
                    ps.setString(1, dto.getEmail());
                    ps.setString(2, dto.getContactNumber());
                    ps.setString(3, dto.getProfilePicture());
                    ps.setString(4, dto.getPassword());
                    ps.setString(5, dto.getUserId());

                    return ps.executeUpdate() > 0;
                }
            } else {
                try (PreparedStatement ps = connection.prepareStatement(sqlWithoutPassword)) {
                    ps.setString(1, dto.getEmail());
                    ps.setString(2, dto.getContactNumber());
                    ps.setString(3, dto.getProfilePicture());
                    ps.setString(4, dto.getUserId());

                    return ps.executeUpdate() > 0;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public StudentDashboardDTO getStudentDashboard(String studentId) {
        List<StudentCourseDashboardDTO> courses = new ArrayList<>();

        String courseSql = """
            SELECT 
                c.course_id,
                c.course_code,
                c.name AS course_name,
                c.course_credit,
                COALESCE(crs.grade, '-') AS grade
            FROM course_registration reg
            JOIN course c ON reg.course_id = c.course_id
            LEFT JOIN course_result crs 
                ON crs.student_id = reg.student_id
                AND crs.course_id = reg.course_id
                AND crs.academic_year = reg.academic_year
                AND crs.semester = reg.semester
            WHERE reg.student_id = ?
            ORDER BY c.course_code
            """;

        String gpaSql = """
            SELECT 
                COALESCE(sr.sgpa, 0) AS sgpa,
                COALESCE(sr.cgpa, 0) AS cgpa
            FROM semester_result sr
            WHERE sr.student_id = ?
            ORDER BY sr.academic_year DESC, sr.academic_level DESC, sr.semester DESC
            LIMIT 1
            """;

        String attendanceSql = """
            SELECT 
                COALESCE(SUM(s.session_hours), 0) AS total_hours,
                COALESCE(SUM(CASE 
                    WHEN a.status = 'Present' THEN a.hours_attended 
                    ELSE 0 
                END), 0) AS attended_hours
            FROM course_registration reg
            JOIN session s ON reg.course_id = s.course_id
            LEFT JOIN attendance a 
                ON a.session_id = s.session_id 
                AND a.student_id = reg.student_id
            WHERE reg.student_id = ?
            """;

        double sgpa = 0;
        double cgpa = 0;
        double overallAttendance = 0;

        try (Connection con = DataSource.getInstance().getConnection()) {

            try (PreparedStatement ps = con.prepareStatement(courseSql)) {
                ps.setString(1, studentId);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        courses.add(new StudentCourseDashboardDTO(
                                rs.getString("course_id"),
                                rs.getString("course_code"),
                                rs.getString("course_name"),
                                rs.getInt("course_credit"),
                                rs.getString("grade")
                        ));
                    }
                }
            }

            try (PreparedStatement ps = con.prepareStatement(gpaSql)) {
                ps.setString(1, studentId);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        sgpa = rs.getDouble("sgpa");
                        cgpa = rs.getDouble("cgpa");
                    }
                }
            }

            try (PreparedStatement ps = con.prepareStatement(attendanceSql)) {
                ps.setString(1, studentId);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        double totalHours = rs.getDouble("total_hours");
                        double attendedHours = rs.getDouble("attended_hours");

                        overallAttendance = totalHours == 0
                                ? 0
                                : (attendedHours / totalHours) * 100.0;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new StudentDashboardDTO(
                overallAttendance,
                sgpa,
                cgpa,
                courses.size(),
                courses
        );
    }

    public List<StudentRegisteredCourseDTO> getRegisteredCourses(String studentId) {
        List<StudentRegisteredCourseDTO> list = new ArrayList<>();

        String sql = """
            SELECT c.course_id, c.course_code, c.name, c.course_credit
            FROM course_registration cr
            JOIN course c ON cr.course_id = c.course_id
            WHERE cr.student_id = ?
            ORDER BY c.course_code
            """;

        try (Connection con = DataSource.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, studentId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new StudentRegisteredCourseDTO(
                            rs.getString("course_id"),
                            rs.getString("course_code"),
                            rs.getString("name"),
                            rs.getInt("course_credit")
                    ));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}
