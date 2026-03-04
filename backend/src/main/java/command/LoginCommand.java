package command;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.requestDto.LoginRequestDTO;
import dto.responseDto.LoginResponseDTO;
import service.AuthService;
import service.AuthService.AuthResult;

public class LoginCommand implements Command {

    private final ObjectMapper mapper = new ObjectMapper();
    private final AuthService authService;

    public LoginCommand(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public void execute(Object data, ClientContext context) {
        try {
            //  Convert request JSON to LoginRequestDTO
            LoginRequestDTO loginDTO = mapper.convertValue(data, LoginRequestDTO.class);

            // Authenticate user and get AuthResult (includes JWT token)
            AuthResult result = authService.authenticate(loginDTO.getUsername(), loginDTO.getPassword());

            // Store token and user info in ClientContext for later use
            if (result.isSuccess()) {
                context.setToken(result.getToken());      // store token for session
                context.setUsername(result.getUsername()); // optional, useful for logging
                context.setRole(result.getRole());        // optional, useful for role-based commands
            }

            // Build response including JWT token
            LoginResponseDTO response = new LoginResponseDTO.Builder()
                    .setSuccess(result.isSuccess())
                    .setUsername(result.getUsername())
                    .setRoll(result.getRole())
                    .setMessage(result.getMessage())
                    .setToken(result.getToken())
                    .build();

            // Send JSON response to frontend
            String jsonResponse = mapper.writeValueAsString(response);
            context.getOutput().println(jsonResponse);

        } catch (Exception e) {
            e.printStackTrace();
            context.getOutput().println("{\"success\":false,\"message\":\"Server error\"}");
        }
    }
}