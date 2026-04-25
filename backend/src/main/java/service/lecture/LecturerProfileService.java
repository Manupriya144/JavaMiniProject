package service.lecture;


import dao.lecture.LecturerProfileDAO;
import dto.requestDto.lecturer.UpdateLecturerProfileReqDTO;
import dto.responseDto.lecture.LecturerProfileDTO;

public class LecturerProfileService {

    private final LecturerProfileDAO dao;

    public LecturerProfileService(LecturerProfileDAO dao) {
        this.dao = dao;
    }

    public LecturerProfileDTO getLecturerProfile(String lecturerId) {
        return dao.getLecturerProfile(lecturerId);
    }

    public boolean updateLecturerProfile(String lecturerId, UpdateLecturerProfileReqDTO dto) {
        return dao.updateLecturerProfile(lecturerId, dto);
    }
}