package command.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import command.repository.ClientContext;
import command.repository.Command;
import service.login.AuthService;
import service.user.UserService;

import java.util.HashMap;
import java.util.Map;

public class DeleteUserCommand implements Command {

    private final UserService userService;
    private final AuthService authService;
    private final ObjectMapper mapper = new ObjectMapper();

    public DeleteUserCommand(UserService userService, AuthService authService) {
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
                response.put("message", "Only admin can delete users.");
                context.getOutput().println(mapper.writeValueAsString(response));
                return;
            }

            Map<?, ?> map = mapper.convertValue(data, Map.class);
            Object userIdObj = map.get("userId");

            if (userIdObj == null || userIdObj.toString().isBlank()) {
                response.put("success", false);
                response.put("message", "User ID is required.");
                context.getOutput().println(mapper.writeValueAsString(response));
                return;
            }

            String userId = userIdObj.toString();

            if (userId.equals(context.getUserId())) {
                response.put("success", false);
                response.put("message", "You cannot delete your own account.");
                context.getOutput().println(mapper.writeValueAsString(response));
                return;
            }

            boolean deleted = userService.deleteUser(userId);

            response.put("success", deleted);
            response.put("message", deleted ? "User deleted successfully." : "Failed to delete user.");

            context.getOutput().println(mapper.writeValueAsString(response));

        } catch (Exception e) {
            e.printStackTrace();

            try {
                response.put("success", false);
                response.put("message", "Server error while deleting user.");
                context.getOutput().println(mapper.writeValueAsString(response));
            } catch (Exception ignored) {}
        }
    }
}