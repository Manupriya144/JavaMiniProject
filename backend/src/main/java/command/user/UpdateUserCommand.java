package command.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import command.repository.ClientContext;
import command.repository.Command;
import dto.requestDto.user.UpdateUserReqDTO;
import service.login.AuthService;
import service.user.UserService;

import java.util.HashMap;
import java.util.Map;

public class UpdateUserCommand implements Command {

    private final UserService userService;
    private final AuthService authService;
    private final ObjectMapper mapper = new ObjectMapper();

    public UpdateUserCommand(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    @Override
    public void execute(Object data, ClientContext context) {
        Map<String, Object> response = new HashMap<>();

        try {
            String token = context.getToken();

            if (token == null || !authService.isTokenValid(token)) {
                response.put("success", false);
                response.put("message", "Unauthorized");
                context.getOutput().println(mapper.writeValueAsString(response));
                return;
            }

            if (!"Admin".equalsIgnoreCase(context.getRole())) {
                response.put("success", false);
                response.put("message", "Only admin can update users.");
                context.getOutput().println(mapper.writeValueAsString(response));
                return;
            }

            UpdateUserReqDTO dto = mapper.convertValue(data, UpdateUserReqDTO.class);

            if (dto.getUserId() == null || dto.getUserId().isBlank()) {
                response.put("success", false);
                response.put("message", "User ID is required.");
                context.getOutput().println(mapper.writeValueAsString(response));
                return;
            }

            boolean updated = userService.updateUser(dto);

            response.put("success", updated);
            response.put("message", updated ? "User updated successfully." : "Failed to update user.");

            context.getOutput().println(mapper.writeValueAsString(response));

        } catch (Exception e) {
            e.printStackTrace();

            try {
                response.put("success", false);
                response.put("message", "Server error while updating user.");
                context.getOutput().println(mapper.writeValueAsString(response));
            } catch (Exception ignored) {}
        }
    }
}