package service.student;

import dao.student.StudentDAO;
import model.Student;

public class StudentService {

    private final StudentDAO studentDAO;

    public StudentService(StudentDAO studentDAO) {
        this.studentDAO = studentDAO;
    }

    public Student getStudentByUserId(String userId) {
        return studentDAO.findByUserId(userId);
    }
}
