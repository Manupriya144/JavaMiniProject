package command;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.responseDto.LogoutResponseDTO;
import service.AuthService;

public class LogoutCommand implements Command {

    private final AuthService authService;
    private final ObjectMapper mapper = new ObjectMapper();

    public LogoutCommand(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public void execute(Object data, ClientContext context) {
        try {
            // Get the token from ClientContext
            String token = context.getToken();

            if (token == null || token.isEmpty()) {
                context.getOutput().println("{\"success\":false,\"message\":\"No token provided\"}");
                return;
            }

            // Perform logout in AuthService (add to blacklist)
            LogoutResponseDTO response = authService.logout(token);

            // Clear token from ClientContext so user is effectively logged out
            context.setToken(null);
            context.setUsername(null);
            context.setRole(null);

            // Send JSON response to client
            String json = mapper.writeValueAsString(response);
            context.getOutput().println(json);

        } catch (Exception e) {
            e.printStackTrace();
            context.getOutput().println("{\"success\":false,\"message\":\"Logout failed\"}");
        }
    }
}