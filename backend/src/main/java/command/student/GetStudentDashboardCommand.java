package command.student;

import com.fasterxml.jackson.databind.ObjectMapper;
import command.repository.ClientContext;
import command.repository.Command;
import dto.responseDto.student.StudentDashboardDTO;
import service.login.AuthService;
import service.student.StudentService;


import java.util.Map;

public class GetStudentDashboardCommand implements Command {

    private final StudentService studentDashboardService;
    private final AuthService authService;
    private final ObjectMapper mapper = new ObjectMapper();

    public GetStudentDashboardCommand(StudentService studentDashboardService,
                                      AuthService authService) {
        this.studentDashboardService = studentDashboardService;
        this.authService = authService;
    }

    @Override
    public void execute(Object data, ClientContext context) {
        try {
            String token = context.getToken();

            if (token == null || !authService.isTokenValid(token)) {
                context.getOutput().println("{\"success\":false,\"message\":\"Unauthorized\"}");
                return;
            }

            String studentId = context.getUserId();

            if (studentId == null || studentId.isBlank()) {
                context.getOutput().println("{\"success\":false,\"message\":\"Student context missing\"}");
                return;
            }

            StudentDashboardDTO dashboard =
                    studentDashboardService.getStudentDashboard(studentId);

            Map<String, Object> response = Map.of(
                    "success", true,
                    "message", "Student dashboard loaded",
                    "dashboard", dashboard
            );

            context.getOutput().println(mapper.writeValueAsString(response));

        } catch (Exception e) {
            e.printStackTrace();
            context.getOutput().println("{\"success\":false,\"message\":\"Server error\"}");
        }
    }
}