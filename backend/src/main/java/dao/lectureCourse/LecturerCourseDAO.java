package dao.lecturerCourse;

import model.LecturerCourse;
import utility.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLIntegrityConstraintViolationException;

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
}