package command.lecturer;

import com.fasterxml.jackson.databind.ObjectMapper;
import command.repository.ClientContext;
import command.repository.Command;
import dto.responseDto.lecture.LecturerProfileDTO;
import service.lecture.LecturerProfileService;
import service.login.AuthService;

import java.util.HashMap;
import java.util.Map;

public class GetLecturerProfileCommand implements Command {

    private final LecturerProfileService service;
    private final AuthService authService;
    private final ObjectMapper mapper = new ObjectMapper();

    public GetLecturerProfileCommand(LecturerProfileService service, AuthService authService) {
        this.service = service;
        this.authService = authService;
    }

    @Override
    public void execute(Object data, ClientContext context) {
        Map<String, Object> response = new HashMap<>();

        try {
            if (context.getToken() == null || !authService.isTokenValid(context.getToken())) {
                response.put("success", false);
                response.put("message", "Unauthorized.");
                context.getOutput().println(mapper.writeValueAsString(response));
                return;
            }

            if (!"Lecturer".equalsIgnoreCase(context.getRole())) {
                response.put("success", false);
                response.put("message", "Only lecturers can access profile.");
                context.getOutput().println(mapper.writeValueAsString(response));
                return;
            }

            LecturerProfileDTO profile = service.getLecturerProfile(context.getUserId());

            response.put("success", profile != null);
            response.put("message", profile != null ? "Profile loaded." : "Profile not found.");
            response.put("profile", profile);

            context.getOutput().println(mapper.writeValueAsString(response));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}