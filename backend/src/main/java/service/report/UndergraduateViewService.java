package service.report;

import dao.report.UndergraduateViewDAO;

import java.util.List;
import java.util.Map;

public class UndergraduateViewService {

    private final UndergraduateViewDAO dao;

    public UndergraduateViewService(UndergraduateViewDAO dao) {
        this.dao = dao;
    }

    public List<Map<String, Object>> getMyAttendance(String studentId) {
        return dao.getMyAttendance(studentId);
    }

    public List<Map<String, Object>> getMyMedicalRecords(String studentId) {
        return dao.getMyMedicalRecords(studentId);
    }

    public List<Map<String, Object>> getMyCourses(String studentId) {
        return dao.getMyCourses(studentId);
    }

    public List<Map<String, Object>> getMyMarks(String studentId) {
        return dao.getMyMarks(studentId);
    }

    public List<Map<String, Object>> getMyGrades(String studentId) {
        return dao.getMyGrades(studentId);
    }

    public Map<String, Object> getMyTimetable(String studentId) {
        return dao.getMyTimetable(studentId);
    }

    public List<Map<String, Object>> getAllNotices() {
        return dao.getAllNotices();
    }

    public List<Map<String, Object>> getCourseMaterials(String courseId) {
        return dao.getCourseMaterials(courseId);
    }
}
