package service.eligibility;

import dao.eligibility.EligibilityDAO;
import model.Eligibility;

import java.util.List;

public class EligibilityService {

    private final EligibilityDAO dao;

    public EligibilityService(EligibilityDAO dao) {
        this.dao = dao;
    }

    public boolean addEligibility(Eligibility eligibility) {
        return dao.insertEligibility(eligibility);
    }

    public Eligibility getStudentEligibility(String studentId, String courseId) {
        return dao.getStudentEligibility(studentId, courseId);
    }

    public List<Eligibility> getBatchEligibility(int academicYear, int semester) {
        return dao.getBatchEligibility(academicYear, semester);
    }
}