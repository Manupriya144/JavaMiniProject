package dao.lectureCourse;

import dto.responseDto.lecture_course.LecturerCourseItemDTO;
import model.LecturerCourse;
import utility.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

public class LecturerCourseDAO {

    public String assignLecturerToCourse(LecturerCourse lecturerCourse) {
        String sql = "INSERT INTO lecturer_course (lecturer_id, course_id) VALUES (?, ?)";

        try (Connection connection = DataSource.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, lecturerCourse.getLecturerId());
            ps.setString(2, lecturerCourse.getCourseId());

            int rows = ps.executeUpdate();
            return rows > 0 ? "SUCCESS" : "FAILED";

        } catch (SQLIntegrityConstraintViolationException e) {
            return "DUPLICATE_OR_INVALID_REFERENCE";
        } catch (Exception e) {
            e.printStackTrace();
            return "ERROR";
        }
    }

    public List<LecturerCourseItemDTO> getCoursesByLecturerId(String lecturerId) {
        List<LecturerCourseItemDTO> list = new ArrayList<>();

        String sql = """
            SELECT c.course_id, c.course_code, c.name, c.course_credit
            FROM lecturer_course lc
            JOIN course c ON lc.course_id = c.course_id
            WHERE lc.lecturer_id = ?
            ORDER BY c.course_code
            """;

        try (Connection con = DataSource.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, lecturerId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new LecturerCourseItemDTO(
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