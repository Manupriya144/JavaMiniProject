package dao.notice;

import model.Notice;
import utility.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Types;

public class NoticeDAO {

    public Notice createNotice(Notice notice) {

        String sql = "INSERT INTO notice " +
                "(title, description, pdf_file_path, created_by) " +
                "VALUES (?, ?, ?, ?)";

        try (Connection connection = DataSource.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, notice.getTitle());
            ps.setString(2, notice.getDescription());
            ps.setString(3, notice.getPdf_file_path());

            if (notice.getCreated_by() != null) {
                ps.setInt(4, notice.getCreated_by());
            } else {
                ps.setNull(4, Types.INTEGER);
            }

            int rows = ps.executeUpdate();
            return rows > 0 ? notice : null;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

//package dao.course;
//
//import model.Course;
//
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//
//public class CourseDAO {
//
//    private final Connection connection;
//
//    public CourseDAO(Connection connection) {
//        this.connection = connection;
//    }
//
//    public boolean existsByCourseId(String courseId) {
//        String sql = "SELECT 1 FROM course WHERE course_id = ?";
//        try (PreparedStatement ps = connection.prepareStatement(sql)) {
//            ps.setString(1, courseId);
//            ResultSet rs = ps.executeQuery();
//            return rs.next();
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
//
//    public boolean existsByCourseCode(String courseCode) {
//        String sql = "SELECT 1 FROM course WHERE course_code = ?";
//        try (PreparedStatement ps = connection.prepareStatement(sql)) {
//            ps.setString(1, courseCode);
//            ResultSet rs = ps.executeQuery();
//            return rs.next();
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
//
//    public boolean existsDepartmentById(String departmentId) {
//        String sql = "SELECT 1 FROM department WHERE department_id = ?";
//        try (PreparedStatement ps = connection.prepareStatement(sql)) {
//            ps.setString(1, departmentId);
//            ResultSet rs = ps.executeQuery();
//            return rs.next();
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
//
//    public Course createCourse(Course course) {
//        String sql = "INSERT INTO course " +
//                "(course_id, course_code, name, course_credit, academic_level, semester, department_id) " +
//                "VALUES (?, ?, ?, ?, ?, ?, ?)";
//
//        try (PreparedStatement ps = connection.prepareStatement(sql)) {
//            ps.setString(1, course.getCourseId());
//            ps.setString(2, course.getCourseCode());
//            ps.setString(3, course.getName());
//            ps.setInt(4, course.getCourseCredit());
//            ps.setInt(5, course.getAcademicLevel());
//            ps.setString(6, course.getSemester());
//            ps.setString(7, course.getDepartmentId());
//
//            int rows = ps.executeUpdate();
//            return rows > 0 ? course : null;
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//}
