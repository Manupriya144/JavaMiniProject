package service.finalMarks;

import dao.finalMarks.FinalMarksDAO;
import model.FinalMarks;

import java.util.List;

public class FinalMarksService {

    private final FinalMarksDAO finalMarksDAO;

    public FinalMarksService(FinalMarksDAO finalMarksDAO) {
        this.finalMarksDAO = finalMarksDAO;
    }

    public boolean uploadFinalMarks(FinalMarks marks) {
        return finalMarksDAO.insertFinalMarks(marks);
    }

    public boolean updateFinalMarks(FinalMarks marks) {
        return finalMarksDAO.updateFinalMarks(marks);
    }

    public FinalMarks getStudentFinalMarks(String studentId, String courseId) {
        return finalMarksDAO.getStudentMarks(studentId, courseId);
    }

    public List<FinalMarks> getBatchFinalMarks(int academicYear, int semester) {
        return finalMarksDAO.getBatchMarks(academicYear, semester);
    }
}
