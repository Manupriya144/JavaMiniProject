package command.lecturer;

import com.fasterxml.jackson.databind.ObjectMapper;
import command.repository.ClientContext;
import command.repository.Command;
import dto.requestDto.lecturer.UpdateLecturerProfileReqDTO;

import service.lecture.LecturerProfileService;
import service.login.AuthService;

import java.util.HashMap;
import java.util.Map;

public class UpdateLecturerProfileCommand implements Command {

    private final LecturerProfileService service;
    private final AuthService authService;
    private final ObjectMapper mapper = new ObjectMapper();

    public UpdateLecturerProfileCommand(LecturerProfileService service, AuthService authService) {
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
                response.put("message", "Only lecturers can update profile.");
                context.getOutput().println(mapper.writeValueAsString(response));
                return;
            }

            UpdateLecturerProfileReqDTO dto =
                    mapper.convertValue(data, UpdateLecturerProfileReqDTO.class);

            boolean updated = service.updateLecturerProfile(context.getUserId(), dto);

            response.put("success", updated);
            response.put("message", updated ? "Profile updated." : "Profile update failed.");

            context.getOutput().println(mapper.writeValueAsString(response));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}