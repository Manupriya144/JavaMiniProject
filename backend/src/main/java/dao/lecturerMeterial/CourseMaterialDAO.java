package dao.lecturerMeterial;

import model.CourseMaterial;
import utility.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourseMaterialDAO {

    public CourseMaterial addMaterial(CourseMaterial material) {
        String sql = """
                INSERT INTO course_material(course_id, lecturer_id, title, file_path)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection con = DataSource.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, material.getCourseId());
            ps.setString(2, material.getLecturerId());
            ps.setString(3, material.getTitle());
            ps.setString(4, material.getFilePath());

            int rows = ps.executeUpdate();

            if (rows > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    material.setMaterialId(rs.getInt(1));
                }
                return material;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<CourseMaterial> getMaterialsByCourse(String courseId, String lecturerId) {
        List<CourseMaterial> list = new ArrayList<>();

        String sql = """
                SELECT material_id, course_id, lecturer_id, title, file_path, uploaded_at
                FROM course_material
                WHERE course_id = ? AND lecturer_id = ?
                ORDER BY uploaded_at DESC
                """;

        try (Connection con = DataSource.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, courseId);
            ps.setString(2, lecturerId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                CourseMaterial material = new CourseMaterial();

                material.setMaterialId(rs.getInt("material_id"));
                material.setCourseId(rs.getString("course_id"));
                material.setLecturerId(rs.getString("lecturer_id"));
                material.setTitle(rs.getString("title"));
                material.setFilePath(rs.getString("file_path"));

                Timestamp ts = rs.getTimestamp("uploaded_at");
                if (ts != null) {
                    material.setUploadedAt(ts.toString());
                }

                list.add(material);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public boolean deleteMaterial(int materialId, String lecturerId) {
        String sql = """
            DELETE FROM course_material
            WHERE material_id = ? AND lecturer_id = ?
            """;

        try (Connection con = DataSource.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, materialId);
            ps.setString(2, lecturerId);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}