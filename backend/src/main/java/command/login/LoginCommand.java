package command.login;

import com.fasterxml.jackson.databind.ObjectMapper;
import command.repository.ClientContext;
import command.repository.Command;
import dto.requestDto.login.LoginRequestDTO;
import dto.responseDto.login.LoginResponseDTO;
import service.login.AuthService;
import service.login.AuthService.AuthResult;

public class LoginCommand implements Command {

    private final ObjectMapper mapper = new ObjectMapper();
    private final AuthService authService;

    public LoginCommand(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public void execute(Object data, ClientContext context) {
        try {

            // Convert JSON request to DTO
            LoginRequestDTO loginDTO = mapper.convertValue(data, LoginRequestDTO.class);

            // Authenticate user
            AuthResult result = authService.authenticate(
                    loginDTO.getUsername(),
                    loginDTO.getPassword()
            );

            // Store session data
            if (result.isSuccess()) {
                context.setToken(result.getToken());
                context.setUsername(result.getUsername());
                context.setRole(result.getRole());

                context.setUserId(result.getUserId());
            }

            // Build response
            LoginResponseDTO response = new LoginResponseDTO.Builder()
                    .setSuccess(result.isSuccess())
                    .setUsername(result.getUsername())
                    .setRole(result.getRole())
                    .setMessage(result.getMessage())
                    .setToken(result.getToken())
                    .setUserId(result.getUserId())
                    .build();

            // Send response to frontend
            String jsonResponse = mapper.writeValueAsString(response);
            System.out.println(jsonResponse);
            context.getOutput().println(jsonResponse);

        } catch (Exception e) {
            e.printStackTrace();
            context.getOutput().println("{\"success\":false,\"message\":\"Server error\"}");
        }
    }
}
