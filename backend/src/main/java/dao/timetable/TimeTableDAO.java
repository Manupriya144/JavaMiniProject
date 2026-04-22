package dao.timetable;


import model.TimeTable;
import utility.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TimeTableDAO {
    public TimeTable createTimeTable(TimeTable timeTable) {

        String sql = "INSERT INTO timetable " +
                "(department_id, academic_level,semester, title,pdf_file_path) " +
                "VALUES (?, ?,?, ?,?)";

        try (Connection connection = DataSource.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1,timeTable.getDepartmentId());
            ps.setInt(2,timeTable.getAcademicLevel());
            ps.setString(3,timeTable.getSemester());
            ps.setString(4,timeTable.getTitle());
            ps.setString(5,timeTable.getPdfFilePath());


            int rows = ps.executeUpdate();

            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        timeTable.setTimetableId(rs.getInt(1));
                    }
                }
                return timeTable;
            }

            return null;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<TimeTable> getAllTimeTables() {
        List<TimeTable> list = new ArrayList<>();

        String sql = """
            SELECT timetable_id, department_id, academic_level, semester, title, pdf_file_path, uploaded_at
            FROM timetable
            ORDER BY uploaded_at DESC
            """;

        try (Connection connection = DataSource.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            System.out.println("===== TimeTableDAO.getAllTimeTables =====");
            System.out.println("DB connection success");

            while (rs.next()) {
                TimeTable timeTable = new TimeTable();
                timeTable.setTimetableId(rs.getInt("timetable_id"));
                timeTable.setDepartmentId(rs.getString("department_id"));
                timeTable.setAcademicLevel(rs.getInt("academic_level"));
                timeTable.setSemester(rs.getString("semester"));
                timeTable.setTitle(rs.getString("title"));
                timeTable.setPdfFilePath(rs.getString("pdf_file_path"));

                Timestamp ts = rs.getTimestamp("uploaded_at");
                if (ts != null) {
                    timeTable.setUploadedAt(ts.toLocalDateTime());
                }

                System.out.println("Fetched from DB -> " + timeTable.getTitle());

                list.add(timeTable);
            }

            System.out.println("Total fetched from DB: " + list.size());

        } catch (Exception e) {
            System.out.println("===== DAO ERROR =====");
            e.printStackTrace();
        }

        return list;
    }
}
