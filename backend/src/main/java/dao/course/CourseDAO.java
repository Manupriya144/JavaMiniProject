package dao.course;

import dto.requestDto.course.UpdateCourseReqDTO;
import dto.responseDto.course.CourseAllResponseDTO;
import dto.responseDto.course.CourseResponseDTO;
import model.Course;
import utility.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;


public class CourseDAO {


    public Course createCourse(Course course) {
        String sql = "INSERT INTO course " +
                "(course_id, course_code, name, course_credit, academic_level, semester, department_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DataSource.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, course.getCourseId());
            ps.setString(2, course.getCourseCode());
            ps.setString(3, course.getName());
            ps.setInt(4, course.getCourseCredit());
            ps.setInt(5, course.getAcademicLevel());
            ps.setString(6, course.getSemester());
            ps.setString(7, course.getDepartmentId());

            int rows = ps.executeUpdate();
            return rows > 0 ? course : null;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<CourseResponseDTO> getAllCourses() {
        String sql = """
                SELECT course_id, course_code, name
                FROM course
                ORDER BY course_code
                """;

        List<CourseResponseDTO> courses = new ArrayList<>();

        try (Connection connection = DataSource.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                courses.add(new CourseResponseDTO(
                        rs.getString("course_id"),
                        rs.getString("course_code"),
                        rs.getString("name")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return courses;
    }

    public List<CourseAllResponseDTO> getAllCoursesFull() {
        List<CourseAllResponseDTO> courseList = new ArrayList<>();

        String sql = """
                SELECT course_id, course_code, name, course_credit,
                       academic_level, semester, department_id
                FROM course
                ORDER BY course_code ASC
                """;

        try (Connection connection = DataSource.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                CourseAllResponseDTO course = new CourseAllResponseDTO(
                        rs.getString("course_id"),
                        rs.getString("course_code"),
                        rs.getString("name"),
                        rs.getInt("course_credit"),
                        rs.getInt("academic_level"),
                        rs.getString("semester"),
                        rs.getString("department_id")
                );

                courseList.add(course);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return courseList;
    }

    public boolean updateCourse(UpdateCourseReqDTO dto) {
        String sql = """
            UPDATE course
            SET course_code = ?,
                name = ?,
                course_credit = ?,
                academic_level = ?,
                semester = ?,
                department_id = ?
            WHERE course_id = ?
            """;

        try (Connection con = DataSource.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, dto.getCourseCode());
            ps.setString(2, dto.getName());
            ps.setInt(3, dto.getCourseCredit());
            ps.setInt(4, dto.getAcademicLevel());
            ps.setString(5, dto.getSemester());
            ps.setString(6, dto.getDepartmentId());
            ps.setString(7, dto.getCourseId());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteCourse(String courseId) {
        String sql = "DELETE FROM course WHERE course_id = ?";

        try (Connection con = DataSource.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, courseId);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


}