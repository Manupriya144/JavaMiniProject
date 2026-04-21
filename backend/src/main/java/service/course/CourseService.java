package service.course;

import dao.course.CourseDAO;
import dto.requestDto.course.AddCourseRequestDTO;
import dto.responseDto.course.CourseAllResponseDTO;
import dto.responseDto.course.CourseResponseDTO;
import model.Course;

import java.util.List;

public class CourseService {

    private final CourseDAO courseDAO;

    public CourseService(CourseDAO courseDAO) {
        this.courseDAO = courseDAO;
    }

    public boolean addCourse(AddCourseRequestDTO dto) {
        Course course = new Course(
                dto.getCourseId(),
                dto.getCourseCode(),
                dto.getName(),
                dto.getCourseCredit(),
                dto.getAcademicLevel(),
                dto.getSemester(),
                dto.getDepartmentId()
        );

        return courseDAO.createCourse(course) != null;
    }

    public List<CourseResponseDTO> getAllCourses() {
        return courseDAO.getAllCourses();
    }

    public List<CourseAllResponseDTO> getAllCoursesFull(){
        return courseDAO.getAllCoursesFull();
    }


}