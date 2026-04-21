package service.lecturerCourse;

import dao.lecturerCourse.LecturerCourseDAO;
import model.LecturerCourse;

public class LecturerCourseService {

    private final LecturerCourseDAO lecturerCourseDAO;

    public LecturerCourseService(LecturerCourseDAO lecturerCourseDAO) {
        this.lecturerCourseDAO = lecturerCourseDAO;
    }

    public String assignLecturerToCourse(String lecturerId, String courseId) {
        if (lecturerId == null || lecturerId.isBlank()) {
            return "INVALID_LECTURER_ID";
        }

        if (courseId == null || courseId.isBlank()) {
            return "INVALID_COURSE_ID";
        }

        LecturerCourse lecturerCourse = new LecturerCourse(lecturerId, courseId);
        return lecturerCourseDAO.assignLecturerToCourse(lecturerCourse);
    }
}