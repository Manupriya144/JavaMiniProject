package service.finalMarks;

import dao.finalMarks.FinalMarksDAO;
import dto.requestDto.finalMarks.FinalMarksRequestDTO;

public class FinalMarksService {

    private final FinalMarksDAO finalMarksDAO;
    public FinalMarksService( FinalMarksDAO finalMarksDAO){
        this.finalMarksDAO = finalMarksDAO;
    }

    public String upload(FinalMarksRequestDTO dto) {

        if (dto.getRegNo() == null || dto.getRegNo().isBlank()) {
            return "Reg No required";
        }

        if (dto.getCourseId() == null || dto.getCourseId().isBlank()) {
            return "Course required";
        }

        if (dto.getMarks() < 0 || dto.getMarks() > 100) {
            return "Invalid marks";
        }

        String studentId = finalMarksDAO.getStudentIdByRegNo(dto.getRegNo());

        if (studentId == null) {
            return "Student not found";
        }

        boolean success = finalMarksDAO.saveMarks(studentId, dto.getCourseId(), dto.getMarks());

        return success ? "Marks saved successfully" : "Failed to save marks";
    }
}