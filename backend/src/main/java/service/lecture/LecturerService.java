package service.lecture;




import dao.lecture.LecturerDAO;
import dto.responseDto.lecture.LecturerResponseDTO;

import java.util.List;

public class LecturerService {

    private final LecturerDAO lecturerDAO;

    public LecturerService(LecturerDAO lecturerDAO) {
        this.lecturerDAO = lecturerDAO;
    }

    public List<LecturerResponseDTO> getAllLecturers() {
        return lecturerDAO.getAllLecturers();
    }
}