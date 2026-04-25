package service.lecture;


import dao.lecture.LecturerDashboardDAO;
import dto.responseDto.lecture.LecturerDashboardStatsDTO;

public class LecturerDashboardService {

    private final LecturerDashboardDAO lecturerDashboardDAO;

    public LecturerDashboardService(LecturerDashboardDAO lecturerDashboardDAO) {
        this.lecturerDashboardDAO = lecturerDashboardDAO;
    }

    public LecturerDashboardStatsDTO getDashboardStats(String lecturerId) {
        return lecturerDashboardDAO.getDashboardStats(lecturerId);
    }
}