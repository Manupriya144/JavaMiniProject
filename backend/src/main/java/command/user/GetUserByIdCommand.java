package command.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import command.repository.ClientContext;
import command.repository.Command;
import dto.requestDto.user.GetUserByIdRequestDTO;  // we'll create this
import dto.responseDto.user.UserResponseDTO;
import dto.responseDto.login.ResponseDTO;          // generic wrapper
import model.User;
import service.login.AuthService;
import service.user.UserService;

public class GetUserByIdCommand implements Command {

    private final UserService userService;
    private final AuthService authService;
    private final ObjectMapper mapper = new ObjectMapper();

    public GetUserByIdCommand(UserService userService, AuthService authService) {
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

            // Convert incoming request JSON into DTO
            GetUserByIdRequestDTO requestDTO = mapper.convertValue(data, GetUserByIdRequestDTO.class);

            // Fetch user from service
            User user = userService.getUserById(requestDTO.getUserId());

            if (user != null) {
                // Convert to response DTO
                UserResponseDTO userResponse = new UserResponseDTO(
                        user.getUserId(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getRole(),
                        user.getContactNumber(),
                        user.getProfilePicture()
                );

                // Wrap in generic ResponseDTO
                ResponseDTO response = new ResponseDTO(true, "User found", userResponse);
                context.getOutput().println(mapper.writeValueAsString(response));

            } else {
                ResponseDTO response = new ResponseDTO(false, "User not found", null);
                context.getOutput().println(mapper.writeValueAsString(response));
            }

        } catch (Exception e) {
            e.printStackTrace();
            context.getOutput().println("{\"success\":false,\"message\":\"Server error\"}");
        }
    }
}