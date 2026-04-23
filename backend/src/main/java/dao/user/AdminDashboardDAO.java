package dao.user;

public interface AdminDashboardDAO {
    int getTotalUsers();
    int getTotalStudents();
    int getTotalLecturers();
    int getTotalTechOfficers();
    int getTotalCourses();
}