package service.user;


import dao.user.AdminDashboardDAO;
import dto.responseDto.user.AdminStatsResponseDTO;

public class AdminDashboardService {

    private final AdminDashboardDAO adminDashboardDAO;

    public AdminDashboardService(AdminDashboardDAO adminDashboardDAO) {
        this.adminDashboardDAO = adminDashboardDAO;
    }

    public AdminStatsResponseDTO getDashboardStats() {
        int totalUsers = adminDashboardDAO.getTotalUsers();
        int totalStudents = adminDashboardDAO.getTotalStudents();
        int totalLecturers = adminDashboardDAO.getTotalLecturers();
        int totalTechOfficers = adminDashboardDAO.getTotalTechOfficers();
        int totalCourses = adminDashboardDAO.getTotalCourses();

        return new AdminStatsResponseDTO(
                true,
                "Dashboard stats fetched successfully.",
                totalUsers,
                totalStudents,
                totalLecturers,
                totalTechOfficers,
                totalCourses
        );
    }
}