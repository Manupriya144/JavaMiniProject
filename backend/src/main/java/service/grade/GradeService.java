package service.grade;

import dao.grade.GradeDAO;
import model.Grade;
import java.util.List;

public class GradeService {

    private final GradeDAO dao;

    public GradeService(GradeDAO dao) {
        this.dao = dao;
    }

    public boolean addGrade(Grade grade) {
        return dao.insertGrade(grade);
    }

    public Grade getStudentGrade(String studentId, String courseId) {
        return dao.getStudentGrade(studentId, courseId);
    }

    public List<Grade> getBatchGrades(int academicYear, int semester) {
        return dao.getBatchGrades(academicYear, semester);
    }
}
