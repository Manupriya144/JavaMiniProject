package service.session;

import dao.session.SessionDAO;
import dto.requestDto.lecturer.AddLectureSessionReqDTO;
import model.Session;

import java.time.LocalDate;

public class SessionService {

    private final SessionDAO sessionDAO;

    public SessionService(SessionDAO sessionDAO) {
        this.sessionDAO = sessionDAO;
    }

    public Session addLectureSession(AddLectureSessionReqDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Request data is required.");
        }

        if (dto.getCourseId() == null || dto.getCourseId().isBlank()) {
            throw new IllegalArgumentException("Course ID is required.");
        }

        if (dto.getSessionDate() == null || dto.getSessionDate().isBlank()) {
            throw new IllegalArgumentException("Session date is required.");
        }

        if (dto.getSessionHours() <= 0) {
            throw new IllegalArgumentException("Session hours must be greater than 0.");
        }

        if (dto.getType() == null || dto.getType().isBlank()) {
            throw new IllegalArgumentException("Session type is required.");
        }

        if (!dto.getType().equals("Theory") && !dto.getType().equals("Practical")) {
            throw new IllegalArgumentException("Session type must be Theory or Practical.");
        }

        if (dto.getLecturerId() == null || dto.getLecturerId().isBlank()) {
            throw new IllegalArgumentException("Lecturer ID is required.");
        }

        boolean assigned = sessionDAO.isLecturerAssignedToCourse(dto.getLecturerId(), dto.getCourseId());
        if (!assigned) {
            throw new IllegalArgumentException("This lecturer is not assigned to the selected course.");
        }

        Session session = new Session(
                dto.getCourseId(),
                LocalDate.parse(dto.getSessionDate()),
                dto.getSessionHours(),
                dto.getType()
        );

        return sessionDAO.createSession(session);
    }
}