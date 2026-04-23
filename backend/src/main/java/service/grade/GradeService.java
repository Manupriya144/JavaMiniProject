package service.grade;

import dao.grade.GradeDAO;
import model.Grade;
import java.util.List;
import java.util.Set;

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

    public Grade getStudentGrade(String studentId, String courseId, Integer academicYear, Integer semester) {
        return dao.getStudentGrade(studentId, courseId, academicYear, semester);
    }

    public List<Grade> getBatchGrades(int academicYear, int semester) {
        return dao.getBatchGrades(academicYear, semester);
    }

    public Set<String> getDistinctStudentIds() {
        return dao.getDistinctStudentIds();
    }

    public Set<String> getDistinctCourseIds(String studentId) {
        return dao.getDistinctCourseIds(studentId);
    }

    public Set<Integer> getDistinctAcademicYears(String studentId, String courseId) {
        return dao.getDistinctAcademicYears(studentId, courseId);
    }

    public Set<Integer> getDistinctAcademicYears() {
        return dao.getDistinctAcademicYears();
    }

    public Set<Integer> getDistinctSemesters(String studentId, String courseId, Integer academicYear) {
        return dao.getDistinctSemesters(studentId, courseId, academicYear);
    }

    public Set<Integer> getDistinctSemesters() {
        return dao.getDistinctSemesters();
    }

    public Set<String> getDistinctGrades(String studentId, String courseId, Integer academicYear, Integer semester) {
        return dao.getDistinctGrades(studentId, courseId, academicYear, semester);
    }
}
