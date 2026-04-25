package service.eligibility;

import dao.eligibility.StudentEligibilityDAO;
import dto.responseDto.eligibility.StudentEligibilityDTO;
import dto.responseDto.student.StudentEligibilityResDTO;

import java.util.List;

public class StudentEligibilityService {

    private final StudentEligibilityDAO dao = new StudentEligibilityDAO();

    public List<StudentEligibilityDTO> getEligibilityByCourse(String lecturerId, String courseId) {
        return dao.getEligibilityByCourse(lecturerId, courseId);
    }

    public List<StudentEligibilityResDTO> getStudentOwnEligibility(String studentId) {
        return dao.getStudentOwnEligibility(studentId);
    }
}