package command.user;

import com.fasterxml.jackson.databind.ObjectMapper;

import command.repository.ClientContext;
import command.repository.Command;
import dto.responseDto.user.AdminStatsResponseDTO;
import service.login.AuthService;
import service.user.AdminDashboardService;


public class GetAdminStatsCommand implements Command {

    private final AdminDashboardService adminDashboardService;
    private final AuthService authService ;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public GetAdminStatsCommand(AdminDashboardService adminDashboardService, AuthService authService) {
        this.adminDashboardService = adminDashboardService;
        this.authService = authService;
    }

    @Override
    public void execute(Object data, ClientContext context) {
        try {
            String token = context.getToken();

            // 1. Check token exists
            if (token == null || token.isEmpty()) {
                writeResponse(context, new AdminStatsResponseDTO(false, "Unauthorized. Token missing."));
                return;
            }

            // 2. Validate token
            if (!authService.isTokenValid(token)) {
                writeResponse(context, new AdminStatsResponseDTO(false, "Invalid or expired token."));
                return;
            }


            // 4. Business logic
            AdminStatsResponseDTO response = adminDashboardService.getDashboardStats();
            writeResponse(context, response);

        } catch (Exception e) {
            writeResponse(context, new AdminStatsResponseDTO(false, e.getMessage()));
        }
    }

    private void writeResponse(ClientContext context, AdminStatsResponseDTO response) {
        try {
            context.getOutput().println(objectMapper.writeValueAsString(response));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}