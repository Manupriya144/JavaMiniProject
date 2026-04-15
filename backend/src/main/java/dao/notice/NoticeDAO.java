package dao.notice;

import model.Notice;
import utility.DataSource;

import java.sql.*;

public class NoticeDAO {

    public Notice createNotice(Notice notice) {

        String sql = "INSERT INTO notice " +
                "(title, description,created_by, pdf_file_path) " +
                "VALUES (?, ?,?, ?)";

        try (Connection connection = DataSource.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, notice.getTitle());
            ps.setString(2, notice.getDescription());
            ps.setString(3,notice.getCreated_by());
            ps.setString(4, notice.getPdf_file_path());

            int rows = ps.executeUpdate();

            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        notice.setNotice_id(rs.getInt(1));
                    }
                }
                return notice;
            }

            return null;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}