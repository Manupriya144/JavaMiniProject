package service.course;

import dao.course.CourseDAO;
import dao.student.StudentDAO;
import dto.responseDto.course.CourseAllResponseDTO;
import model.Student;

import java.util.List;

public class StudentCourseService {

    private final StudentDAO studentDAO;
    private final CourseDAO courseDAO;

    public StudentCourseService(StudentDAO studentDAO, CourseDAO courseDAO) {
        this.studentDAO = studentDAO;
        this.courseDAO = courseDAO;
    }

    public List<CourseAllResponseDTO> getCoursesForStudent(String userId) {
        Student student = studentDAO.findByUserId(userId);
        if (student == null) {
            return List.of();
        }

        String deptId = student.getDepartmentId();
        int level = student.getAcademicLevel();

        if (deptId == null || deptId.isBlank() || level <= 0) {
            return List.of();
        }

        return courseDAO.getCoursesByDepartmentAndLevel(deptId, level);
    }
}

