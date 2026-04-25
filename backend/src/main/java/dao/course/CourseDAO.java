package dao.course;

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


//    public boolean existsByCourseId(String courseId) {
//        String sql = "SELECT 1 FROM course WHERE course_id = ?";
//        try (Connection connection = DataSource.getInstance().getConnection();
//        PreparedStatement ps = connection.prepareStatement(sql)) {
//            ps.setString(1, courseId);
//            ResultSet rs = ps.executeQuery();
//            return rs.next();
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//    }

//    public boolean existsByCourseCode(String courseCode) {
//        String sql = "SELECT 1 FROM course WHERE course_code = ?";
//        try (Connection connection = DataSource.getInstance().getConnection();
//             PreparedStatement ps = connection.prepareStatement(sql)) {
//            ps.setString(1, courseCode);
//            ResultSet rs = ps.executeQuery();
//            return rs.next();
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//    }

//    public boolean existsDepartmentById(String departmentId) {
//        String sql = "SELECT 1 FROM department WHERE department_id = ?";
//
//        try (Connection connection = DataSource.getInstance().getConnection();
//             PreparedStatement ps = connection.prepareStatement(sql)) {
//            ps.setString(1, departmentId);
//            ResultSet rs = ps.executeQuery();
//            return rs.next();
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//    }

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

    public List<CourseAllResponseDTO> getCoursesByDepartmentAndLevel(String departmentId, int academicLevel) {
        List<CourseAllResponseDTO> courseList = new ArrayList<>();

        String sql = """
                SELECT course_id, course_code, name, course_credit,
                       academic_level, semester, department_id
                FROM course
                WHERE department_id = ? AND academic_level = ?
                ORDER BY semester ASC, course_code ASC
                """;

        try (Connection connection = DataSource.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, departmentId);
            ps.setInt(2, academicLevel);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    courseList.add(new CourseAllResponseDTO(
                            rs.getString("course_id"),
                            rs.getString("course_code"),
                            rs.getString("name"),
                            rs.getInt("course_credit"),
                            rs.getInt("academic_level"),
                            rs.getString("semester"),
                            rs.getString("department_id")
                    ));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return courseList;
    }
}