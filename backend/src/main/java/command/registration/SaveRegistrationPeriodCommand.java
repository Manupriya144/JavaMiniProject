package command.registration;

import com.fasterxml.jackson.databind.ObjectMapper;
import command.repository.ClientContext;
import command.repository.Command;
import dto.requestDto.registration.RegistrationPeriodReqDTO;
import service.login.AuthService;
import service.resgistration.RegistrationPeriodService;

import java.util.HashMap;
import java.util.Map;

public class SaveRegistrationPeriodCommand implements Command {

    private final RegistrationPeriodService registrationPeriodService;
    private final AuthService authService;
    private final ObjectMapper mapper = new ObjectMapper();

    public SaveRegistrationPeriodCommand(RegistrationPeriodService registrationPeriodService,
                                         AuthService authService) {
        this.registrationPeriodService = registrationPeriodService;
        this.authService = authService;
    }

    @Override
    public void execute(Object data, ClientContext context) {
        Map<String, Object> response = new HashMap<>();

        try {
            String token = context.getToken();

            if (token == null || !authService.isTokenValid(token)) {
                response.put("success", false);
                response.put("message", "Unauthorized.");
                context.getOutput().println(mapper.writeValueAsString(response));
                return;
            }

            if (!"Admin".equalsIgnoreCase(context.getRole())) {
                response.put("success", false);
                response.put("message", "Only admin can manage registration period.");
                context.getOutput().println(mapper.writeValueAsString(response));
                return;
            }

            RegistrationPeriodReqDTO dto =
                    mapper.convertValue(data, RegistrationPeriodReqDTO.class);

            if (dto.getDepartmentId() == null || dto.getDepartmentId().isBlank()) {
                response.put("success", false);
                response.put("message", "Department ID is required.");
                context.getOutput().println(mapper.writeValueAsString(response));
                return;
            }

            boolean saved = registrationPeriodService.saveOrUpdatePeriod(dto);

            response.put("success", saved);
            response.put("message", saved
                    ? "Registration period saved successfully."
                    : "Failed to save registration period.");

            context.getOutput().println(mapper.writeValueAsString(response));

        } catch (Exception e) {
            e.printStackTrace();

            try {
                response.put("success", false);
                response.put("message", "Server error while saving registration period.");
                context.getOutput().println(mapper.writeValueAsString(response));
            } catch (Exception ignored) {}
        }
    }
}