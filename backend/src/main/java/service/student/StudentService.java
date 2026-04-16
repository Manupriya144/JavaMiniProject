package service.student;

import dao.student.StudentDAO;
import dto.requestDto.student.UpdateStudentProfileReqDTO;
import model.Student;

public class StudentService {

    private final StudentDAO studentDAO;

    public StudentService(StudentDAO studentDAO) {
        this.studentDAO = studentDAO;
    }

    public Student getStudentByUserId(String userId) {
        return studentDAO.findByUserId(userId);
    }

    public Student getStudentAllByUserId(String userId){
        return studentDAO.findStudentById(userId);
    }

    public boolean updateStudentProfile(UpdateStudentProfileReqDTO dto) {
        if (dto == null) return false;
        if (dto.getUserId() == null || dto.getUserId().trim().isEmpty()) return false;
        if (dto.getEmail() == null || dto.getEmail().trim().isEmpty()) return false;
        if (dto.getContactNumber() == null || dto.getContactNumber().trim().isEmpty()) return false;

        return studentDAO.updateStudentProfile(dto);
    }
}
