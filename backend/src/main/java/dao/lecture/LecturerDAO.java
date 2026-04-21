package dao.lecture;


import dto.responseDto.lecture.LecturerResponseDTO;
import utility.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class LecturerDAO {

    public List<LecturerResponseDTO> getAllLecturers() {
        String sql = """
                SELECT u.user_id, u.username, u.email
                FROM users u
                INNER JOIN lecturers l ON u.user_id = l.user_id
                ORDER BY u.username
                """;

        List<LecturerResponseDTO> lecturers = new ArrayList<>();

        try (Connection connection = DataSource.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lecturers.add(new LecturerResponseDTO(
                        rs.getString("user_id"),
                        rs.getString("username"),
                        rs.getString("email")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lecturers;
    }
}