package command.session;

import com.fasterxml.jackson.databind.ObjectMapper;

import command.repository.ClientContext;
import command.repository.Command;
import dao.session.SessionDAO;

import dao.user.UserDAO;
import dto.requestDto.AddLectureSessionReqDTO;
import model.Session;
import service.login.AuthService;
import service.session.SessionService;

import java.util.HashMap;
import java.util.Map;

public class AddLectureSessionCommand implements Command {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final SessionService sessionService ;
    private final AuthService authService ;

    public AddLectureSessionCommand(SessionService sessionService,AuthService authService){
        this.sessionService = sessionService;
        this.authService = authService;
    }

    @Override
    public void execute(Object data, ClientContext context) {
        Map<String, Object> response = new HashMap<>();

        try {
            String token = context.getToken();

            if (token == null || !authService.isTokenValid(token)) {
                response.put("success", false);
                response.put("message", "Invalid or missing token.");
                context.getOutput().println(objectMapper.writeValueAsString(response));
                return;
            }

            String role = context.getRole();
            if (!"Lecturer".equals(role)) {
                response.put("success", false);
                response.put("message", "Only lecturers can add lecture sessions.");
                context.getOutput().println(objectMapper.writeValueAsString(response));
                return;
            }

            AddLectureSessionReqDTO dto = objectMapper.convertValue(data, AddLectureSessionReqDTO.class);

            if (dto.getLecturerId() == null || dto.getLecturerId().isBlank()) {
                dto.setLecturerId(context.getUserId());
            }

            Session created = sessionService.addLectureSession(dto);

            if (created != null) {
                response.put("success", true);
                response.put("message", "Lecture session added successfully.");
                response.put("sessionId", created.getSessionId());
                response.put("courseId", created.getCourseId());
                response.put("sessionDate", created.getSessionDate().toString());
                response.put("sessionHours", created.getSessionHours());
                response.put("type", created.getType());
            } else {
                response.put("success", false);
                response.put("message", "Failed to add lecture session.");
            }

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }

        try {
            context.getOutput().println(objectMapper.writeValueAsString(response));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}