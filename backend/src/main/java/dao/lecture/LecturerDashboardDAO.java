package dao.lecture;


import dto.responseDto.lecture.LecturerDashboardStatsDTO;
import utility.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LecturerDashboardDAO {

    public LecturerDashboardStatsDTO getDashboardStats(String lecturerId) {
        int courses = countCourses(lecturerId);
        int students = countStudents(lecturerId);
        int eligible = countEligibleStudents(lecturerId);
        int pendingMarks = countPendingMarks(lecturerId);

        return new LecturerDashboardStatsDTO(courses, students, eligible, pendingMarks);
    }

    private int countCourses(String lecturerId) {
        String sql = """
                SELECT COUNT(*) AS total
                FROM lecturer_course
                WHERE lecturer_id = ?
                """;

        return getCount(sql, lecturerId);
    }

    private int countStudents(String lecturerId) {
        String sql = """
                SELECT COUNT(DISTINCT cr.student_id) AS total
                FROM lecturer_course lc
                JOIN course_registration cr ON lc.course_id = cr.course_id
                WHERE lc.lecturer_id = ?
                """;

        return getCount(sql, lecturerId);
    }

    private int countPendingMarks(String lecturerId) {
        String sql = """
                SELECT COUNT(*) AS total
                FROM lecturer_course lc
                JOIN course_registration cr ON lc.course_id = cr.course_id
                LEFT JOIN course_result r 
                    ON r.student_id = cr.student_id 
                   AND r.course_id = cr.course_id
                   AND r.academic_year = cr.academic_year
                   AND r.semester = cr.semester
                WHERE lc.lecturer_id = ?
                  AND r.result_id IS NULL
                """;

        return getCount(sql, lecturerId);
    }

    private int countEligibleStudents(String lecturerId) {
        String sql = """
            SELECT COUNT(*) AS total
            FROM (
                SELECT 
                    cr.student_id,
                    cr.course_id,

                    COALESCE(SUM(se.session_hours), 0) AS total_hours,
                    COALESCE(SUM(CASE 
                        WHEN a.status = 'Present' THEN a.hours_attended 
                        ELSE 0 
                    END), 0) AS attended_hours,

                    CASE 
                        WHEN EXISTS (
                            SELECT 1
                            FROM session sp
                            WHERE sp.course_id = cr.course_id
                              AND sp.type = 'Practical'
                        )
                        THEN 40
                        ELSE 30
                    END AS ca_max,

                    COALESCE((
                        SELECT SUM((sm.marks / 100) * at.weight)
                        FROM assessment_type at
                        LEFT JOIN student_marks sm
                            ON sm.assessment_type_id = at.assessment_type_id
                           AND sm.student_id = cr.student_id
                        WHERE at.course_id = cr.course_id
                          AND at.component = 'CA'
                    ), 0) AS ca_marks

                FROM lecturer_course lc
                JOIN course_registration cr 
                    ON lc.course_id = cr.course_id
                LEFT JOIN session se 
                    ON se.course_id = cr.course_id
                LEFT JOIN attendance a 
                    ON a.session_id = se.session_id 
                   AND a.student_id = cr.student_id

                WHERE lc.lecturer_id = ?
                  AND cr.academic_year = YEAR(CURDATE())
                  AND cr.registration_type IN ('Proper', 'Repeat')

                GROUP BY cr.student_id, cr.course_id
            ) x
            WHERE x.total_hours > 0
              AND ((x.attended_hours / x.total_hours) * 100) >= 80
              AND x.ca_marks >= (x.ca_max * 0.5)
            """;

        return getCount(sql, lecturerId);
    }


    private int getCount(String sql, String lecturerId) {
        try (Connection con = DataSource.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, lecturerId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }
}