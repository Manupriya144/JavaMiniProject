package command;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.requestDto.UserRequestDTO;
import service.UserService;
import service.AuthService;

public class CreateUserCommand implements Command {

    private final UserService userService;
    private final AuthService authService; // added
    private final ObjectMapper mapper = new ObjectMapper();

    public CreateUserCommand(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    @Override
    public void execute(Object data, ClientContext context) {
        try {
            // ===== Token Validation =====
            String token = context.getToken();
            if (token == null || !authService.isTokenValid(token)) {
                context.getOutput().println("{\"success\":false,\"message\":\"Unauthorized: invalid or expired token\"}");
                return;
            }

            // Convert JSON to DTO
            UserRequestDTO request = mapper.convertValue(data, UserRequestDTO.class);
            System.out.println("DTO role: " + request.getRole());

            boolean success = userService.createUser(request);

            if (success) {
                context.getOutput().println(
                        "{\"success\":true,\"message\":\"User created successfully\"}"
                );
            } else {
                context.getOutput().println(
                        "{\"success\":false,\"message\":\"User creation failed\"}"
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
            context.getOutput().println(
                    "{\"success\":false,\"message\":\"Server error\"}"
            );
        }
    }
}