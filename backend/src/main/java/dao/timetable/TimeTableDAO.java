package dao.timetable;


import model.TimeTable;
import utility.DataSource;

import java.sql.*;

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
}
