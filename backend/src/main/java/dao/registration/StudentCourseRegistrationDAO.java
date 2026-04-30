package dao.registration;

import dto.requestDto.registration.RegisterCourseReqDTO;
import dto.responseDto.registration.StudentCourseRegistrationDTO;
import utility.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentCourseRegistrationDAO {

    public boolean isRegistrationOpen(String studentId) {
        String sql = """
                SELECT rp.period_id
                FROM students s
                JOIN registration_period rp
                    ON rp.department_id = s.department_id
                   AND rp.academic_level = s.academic_level
                WHERE s.user_id = ?
                  AND rp.status = 'Open'
                  AND NOW() BETWEEN rp.start_at AND rp.end_at
                LIMIT 1
                """;

        try (Connection con = DataSource.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, studentId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<StudentCourseRegistrationDTO> getProperCourses(String studentId) {
        List<StudentCourseRegistrationDTO> list = new ArrayList<>();

        String sql = """
                SELECT 
                    c.course_id,
                    c.course_code,
                    c.name AS course_name,
                    c.course_credit,
                    c.academic_level,
                    c.semester,
                    c.department_id,
                    'Proper' AS registration_type,
                    CASE WHEN cr.course_id IS NULL THEN false ELSE true END AS already_registered
                FROM students s
                JOIN registration_period rp
                    ON rp.department_id = s.department_id
                   AND rp.academic_level = s.academic_level
                JOIN course c
                    ON c.department_id = s.department_id
                   AND c.academic_level = s.academic_level
                   AND c.semester = rp.semester
                LEFT JOIN course_registration cr
                    ON cr.student_id = s.user_id
                   AND cr.course_id = c.course_id
                   AND cr.academic_year = rp.academic_year
                   AND cr.semester = rp.semester
                WHERE s.user_id = ?
                  AND rp.status = 'Open'
                  AND NOW() BETWEEN rp.start_at AND rp.end_at
                ORDER BY c.course_code
                """;

        try (Connection con = DataSource.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, studentId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapCourse(rs));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<StudentCourseRegistrationDTO> getRepeatCourses(String studentId) {
        List<StudentCourseRegistrationDTO> list = new ArrayList<>();

        String sql = """
                SELECT DISTINCT
                    c.course_id,
                    c.course_code,
                    c.name AS course_name,
                    c.course_credit,
                    c.academic_level,
                    rp.semester,
                    c.department_id,
                    'Repeat' AS registration_type,
                    CASE WHEN cr.course_id IS NULL THEN false ELSE true END AS already_registered
                FROM students s
                JOIN registration_period rp
                    ON rp.department_id = s.department_id
                   AND rp.academic_level = s.academic_level
                JOIN course_result result
                    ON result.student_id = s.user_id
                JOIN course c
                    ON c.course_id = result.course_id
                LEFT JOIN course_registration cr
                    ON cr.student_id = s.user_id
                   AND cr.course_id = c.course_id
                   AND cr.academic_year = rp.academic_year
                   AND cr.semester = rp.semester
                WHERE s.user_id = ?
                  AND rp.status = 'Open'
                  AND NOW() BETWEEN rp.start_at AND rp.end_at
                  AND result.grade IN ('E', 'F', 'D', 'EE', 'EC', 'WH', 'MC')
                ORDER BY c.course_code
                """;

        try (Connection con = DataSource.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, studentId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapCourse(rs));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public boolean registerCourse(String studentId, RegisterCourseReqDTO dto) {
        if (studentId == null || studentId.isBlank() || dto == null) {
            return false;
        }

        String sql = """
                INSERT INTO course_registration
                (student_id, course_id, academic_year, semester, registration_type)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection con = DataSource.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, studentId);
            ps.setString(2, dto.getCourseId());
            ps.setInt(3, dto.getAcademicYear());
            ps.setString(4, dto.getSemester());
            ps.setString(5, dto.getRegistrationType());

            return ps.executeUpdate() > 0;

        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Already registered or invalid FK.");
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private StudentCourseRegistrationDTO mapCourse(ResultSet rs) throws SQLException {
        StudentCourseRegistrationDTO dto = new StudentCourseRegistrationDTO();

        dto.setCourseId(rs.getString("course_id"));
        dto.setCourseCode(rs.getString("course_code"));
        dto.setCourseName(rs.getString("course_name"));
        dto.setCourseCredit(rs.getInt("course_credit"));
        dto.setAcademicLevel(rs.getInt("academic_level"));
        dto.setSemester(rs.getString("semester"));
        dto.setDepartmentId(rs.getString("department_id"));
        dto.setRegistrationType(rs.getString("registration_type"));
        dto.setAlreadyRegistered(rs.getBoolean("already_registered"));

        return dto;
    }
}