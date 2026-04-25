package command.lecturer;

import com.fasterxml.jackson.databind.ObjectMapper;
import command.repository.ClientContext;
import command.repository.Command;


import dto.responseDto.lecture.LecturerDashboardStatsDTO;
import service.lecture.LecturerDashboardService;
import service.login.AuthService;

import java.util.HashMap;
import java.util.Map;

public class GetLecturerDashboardStatsCommand implements Command {

    private final LecturerDashboardService dashboardService;
    private final AuthService authService;
    private final ObjectMapper mapper = new ObjectMapper();

    public GetLecturerDashboardStatsCommand(LecturerDashboardService dashboardService,
                                            AuthService authService) {
        this.dashboardService = dashboardService;
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

            if (!"Lecturer".equalsIgnoreCase(context.getRole())) {
                response.put("success", false);
                response.put("message", "Only lecturers can access dashboard stats.");
                context.getOutput().println(mapper.writeValueAsString(response));
                return;
            }

            LecturerDashboardStatsDTO stats =
                    dashboardService.getDashboardStats(context.getUserId());

            response.put("success", true);
            response.put("message", "Lecturer dashboard stats loaded.");
            response.put("stats", stats);

            context.getOutput().println(mapper.writeValueAsString(response));

        } catch (Exception e) {
            e.printStackTrace();

            try {
                response.put("success", false);
                response.put("message", "Server error");
                context.getOutput().println(mapper.writeValueAsString(response));
            } catch (Exception ignored) {}
        }
    }
}