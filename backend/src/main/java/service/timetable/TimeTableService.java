package service.timetable;

import dao.timetable.TimeTableDAO;
import dto.requestDto.timetable.AddTimeTableReqDTO;
import dto.responseDto.timetable.TimeTableResponseDTO;
import model.TimeTable;

import java.util.ArrayList;
import java.util.List;

public class TimeTableService {

    private final TimeTableDAO timeTableDAO;

    public TimeTableService(TimeTableDAO timeTableDAO) {
        this.timeTableDAO = timeTableDAO;
    }

    public TimeTable addTimeTable(AddTimeTableReqDTO addTimeTableReqDTO) {
        TimeTable timeTable = new TimeTable(
                addTimeTableReqDTO.getDepartmentId(),
                addTimeTableReqDTO.getAcademicLevel(),
                addTimeTableReqDTO.getSemester(),
                addTimeTableReqDTO.getTitle(),
                addTimeTableReqDTO.getPdfFilePath()
        );

        return timeTableDAO.createTimeTable(timeTable);
    }

    public List<TimeTable> getAllTimeTables() {
        return timeTableDAO.getAllTimeTables();
    }

    public List<TimeTableResponseDTO> getAllTimeTableResponses() {
        List<TimeTable> timeTables = timeTableDAO.getAllTimeTables();
        List<TimeTableResponseDTO> dtoList = new ArrayList<>();

        for (TimeTable t : timeTables) {
            TimeTableResponseDTO dto = new TimeTableResponseDTO();
            dto.setTimetableId(t.getTimetableId());
            dto.setDepartmentId(t.getDepartmentId());
            dto.setAcademicLevel(t.getAcademicLevel());
            dto.setSemester(t.getSemester());
            dto.setTitle(t.getTitle());
            dto.setPdfFilePath(t.getPdfFilePath());
            dto.setUploadedAt(t.getUploadedAt() == null ? null : t.getUploadedAt().toString());

            dtoList.add(dto);
        }

        return dtoList;
    }
}