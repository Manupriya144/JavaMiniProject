package command.lecturer;

import com.fasterxml.jackson.databind.ObjectMapper;
import command.repository.ClientContext;
import command.repository.Command;


import dto.responseDto.lecture.LecturerResponseDTO;
import service.lecture.LecturerService;
import service.login.AuthService;

import java.util.List;

public class GetAllLecturersCommand implements Command {

    private final ObjectMapper mapper = new ObjectMapper();
    private final LecturerService lecturerService;
    private final AuthService authService;

    public GetAllLecturersCommand(LecturerService lecturerService, AuthService authService) {
        this.lecturerService = lecturerService;
        this.authService = authService;
    }

    @Override
    public void execute(Object data, ClientContext context) {
        try {
            String token = context.getToken();

            if (token == null || !authService.isTokenValid(token)) {
                context.getOutput().println("{\"success\":false,\"message\":\"Unauthorized\"}");
                return;
            }

            List<LecturerResponseDTO> lecturers = lecturerService.getAllLecturers();
            context.getOutput().println(mapper.writeValueAsString(lecturers));

        } catch (Exception e) {
            e.printStackTrace();
            context.getOutput().println("{\"success\":false,\"message\":\"Server error\"}");
        }
    }
}