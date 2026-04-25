package service.resgistration;

import dao.registration.StudentCourseRegistrationDAO;
import dto.requestDto.registration.RegisterCourseReqDTO;
import dto.responseDto.registration.StudentCourseRegistrationDTO;

import java.util.List;

public class StudentCourseRegistrationService {

    private final StudentCourseRegistrationDAO dao;

    public StudentCourseRegistrationService(StudentCourseRegistrationDAO dao) {
        this.dao = dao;
    }

    public boolean isRegistrationOpen(String studentId) {
        return dao.isRegistrationOpen(studentId);
    }

    public List<StudentCourseRegistrationDTO> getProperCourses(String studentId) {
        return dao.getProperCourses(studentId);
    }

    public List<StudentCourseRegistrationDTO> getRepeatCourses(String studentId) {
        return dao.getRepeatCourses(studentId);
    }

    public boolean registerCourse(String studentId, RegisterCourseReqDTO dto) {
        return dao.registerCourse(studentId, dto);
    }
}