package service.timetable;

import dao.timetable.TimeTableDAO;
import dto.requestDto.timetable.AddTimeTableReqDTO;
import model.TimeTable;

public class TimeTableService {

    private final TimeTableDAO timeTableDAO;

    public TimeTableService(TimeTableDAO timeTableDAO){
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

}
