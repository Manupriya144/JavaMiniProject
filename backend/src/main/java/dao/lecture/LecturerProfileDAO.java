package dao.lecture;

import dto.requestDto.lecturer.UpdateLecturerProfileReqDTO;

import dto.responseDto.lecture.LecturerProfileDTO;
import utility.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LecturerProfileDAO {

    public LecturerProfileDTO getLecturerProfile(String lecturerId) {
        String sql = """
                SELECT 
                    u.user_id,
                    u.username,
                    u.email,
                    u.contact_number,
                    u.profile_picture,
                    l.specialization,
                    l.designation
                FROM users u
                INNER JOIN lecturers l ON u.user_id = l.user_id
                WHERE u.user_id = ?
                  AND u.role = 'Lecturer'
                """;

        try (Connection con = DataSource.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, lecturerId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    LecturerProfileDTO dto = new LecturerProfileDTO();

                    dto.setUserId(rs.getString("user_id"));
                    dto.setUsername(rs.getString("username"));
                    dto.setEmail(rs.getString("email"));
                    dto.setContactNumber(rs.getString("contact_number"));
                    dto.setProfilePicture(rs.getString("profile_picture"));
                    dto.setSpecialization(rs.getString("specialization"));
                    dto.setDesignation(rs.getString("designation"));

                    return dto;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean updateLecturerProfile(String lecturerId, UpdateLecturerProfileReqDTO dto) {
        String updateUserSql = """
                UPDATE users
                SET profile_picture = ?
                WHERE user_id = ?
                  AND role = 'Lecturer'
                """;

        String updateLecturerSql = """
                UPDATE lecturers
                SET designation = ?
                WHERE user_id = ?
                """;

        try (Connection con = DataSource.getInstance().getConnection()) {
            con.setAutoCommit(false);

            try {
                try (PreparedStatement ps = con.prepareStatement(updateUserSql)) {
                    ps.setString(1, dto.getProfilePicture());
                    ps.setString(2, lecturerId);
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = con.prepareStatement(updateLecturerSql)) {
                    ps.setString(1, dto.getDesignation());
                    ps.setString(2, lecturerId);
                    ps.executeUpdate();
                }

                con.commit();
                return true;

            } catch (Exception e) {
                con.rollback();
                e.printStackTrace();
                return false;
            } finally {
                con.setAutoCommit(true);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}