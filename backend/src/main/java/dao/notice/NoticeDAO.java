package dao.notice;

import model.Notice;
import utility.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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

    public List<Notice> getAllNotices() {
        List<Notice> noticeList = new ArrayList<>();

        String sql = """
                SELECT n.notice_id,
                       n.title,
                       n.description,
                       n.pdf_file_path,
                       u.username AS created_by,
                       n.created_at
                FROM notice n
                LEFT JOIN users u ON n.created_by = u.user_id
                ORDER BY n.created_at DESC
                """;

        try (Connection connection = DataSource.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Notice notice = new Notice();
                notice.setNotice_id(rs.getInt("notice_id"));
                notice.setTitle(rs.getString("title"));
                notice.setDescription(rs.getString("description"));
                notice.setPdf_file_path(rs.getString("pdf_file_path"));
                notice.setCreated_by(rs.getString("created_by"));

                if (rs.getTimestamp("created_at") != null) {
                    notice.setCreated_at(rs.getTimestamp("created_at").toLocalDateTime());
                }

                noticeList.add(notice);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return noticeList;
    }
}