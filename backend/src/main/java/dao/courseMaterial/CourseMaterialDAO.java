package dao.courseMaterial;

import model.CourseMaterial;
import utility.DataSource;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CourseMaterialDAO {

    public CourseMaterial createCourseMaterial(CourseMaterial material) {
        String sql = "INSERT INTO course_material (course_id, lecturer_id, title, file_path, deadline) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DataSource.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, material.getCourseId());
            ps.setString(2, material.getLecturerId());
            ps.setString(3, material.getTitle());
            ps.setString(4, material.getFilePath());
            if (material.getDeadline() != null) {
                ps.setDate(5, Date.valueOf(material.getDeadline()));
            } else {
                ps.setNull(5, java.sql.Types.DATE);
            }

            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        material.setMaterialId(rs.getInt(1));
                    }
                }
                return material;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<CourseMaterial> getMaterialsByCourseAndLecturer(String courseId, String lecturerId) {
        List<CourseMaterial> materials = new ArrayList<>();
        String sql = "SELECT material_id, course_id, lecturer_id, title, file_path, deadline, uploaded_at FROM course_material WHERE course_id = ? AND lecturer_id = ? ORDER BY uploaded_at DESC";

        try (Connection connection = DataSource.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, courseId);
            ps.setString(2, lecturerId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    materials.add(mapRow(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return materials;
    }

    public boolean updateDeadline(int materialId, String lecturerId, LocalDate deadline) {
        String sql = "UPDATE course_material SET deadline = ? WHERE material_id = ? AND lecturer_id = ?";

        try (Connection connection = DataSource.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            if (deadline != null) {
                ps.setDate(1, Date.valueOf(deadline));
            } else {
                ps.setNull(1, java.sql.Types.DATE);
            }
            ps.setInt(2, materialId);
            ps.setString(3, lecturerId);

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteCourseMaterial(int materialId, String lecturerId) {
        String sql = "DELETE FROM course_material WHERE material_id = ? AND lecturer_id = ?";

        try (Connection connection = DataSource.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, materialId);
            ps.setString(2, lecturerId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private CourseMaterial mapRow(ResultSet rs) throws Exception {
        return new CourseMaterial(
                rs.getInt("material_id"),
                rs.getString("course_id"),
                rs.getString("lecturer_id"),
                rs.getString("title"),
                rs.getString("file_path"),
                rs.getDate("deadline") != null ? rs.getDate("deadline").toLocalDate() : null,
                rs.getTimestamp("uploaded_at") != null ? rs.getTimestamp("uploaded_at").toLocalDateTime() : null
        );
    }
}