package dao.registration;

import dto.requestDto.registration.RegistrationPeriodReqDTO;
import utility.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class RegistrationPeriodDAO {

    public boolean saveOrUpdatePeriod(RegistrationPeriodReqDTO dto) {
        String sql = """
                INSERT INTO registration_period
                (department_id, academic_level, semester, academic_year, start_at, end_at, status)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                ON DUPLICATE KEY UPDATE
                    start_at = VALUES(start_at),
                    end_at = VALUES(end_at),
                    status = VALUES(status)
                """;

        try (Connection con = DataSource.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, dto.getDepartmentId());
            ps.setInt(2, dto.getAcademicLevel());
            ps.setString(3, dto.getSemester());
            ps.setInt(4, dto.getAcademicYear());
            ps.setString(5, dto.getStartAt());
            ps.setString(6, dto.getEndAt());
            ps.setString(7, dto.getStatus());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}