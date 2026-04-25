package command.eligibility;

import com.fasterxml.jackson.databind.ObjectMapper;
import command.repository.ClientContext;
import command.repository.Command;
import dto.responseDto.student.StudentEligibilityResDTO;
import service.eligibility.StudentEligibilityService;
import service.login.AuthService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetStudentOwnEligibilityCommand implements Command {

    private final StudentEligibilityService studentEligibilityService;
    private final AuthService authService;
    private final ObjectMapper mapper = new ObjectMapper();

    public GetStudentOwnEligibilityCommand(StudentEligibilityService studentEligibilityService,
                                           AuthService authService) {
        this.studentEligibilityService = studentEligibilityService;
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

            if (!"Student".equalsIgnoreCase(context.getRole())) {
                response.put("success", false);
                response.put("message", "Only students can check eligibility.");
                context.getOutput().println(mapper.writeValueAsString(response));
                return;
            }

            String studentId = context.getUserId();

            if (studentId == null || studentId.isBlank()) {
                response.put("success", false);
                response.put("message", "Student context missing.");
                context.getOutput().println(mapper.writeValueAsString(response));
                return;
            }

            List<StudentEligibilityResDTO> eligibility =
                    studentEligibilityService.getStudentOwnEligibility(studentId);

            response.put("success", true);
            response.put("message", "Student eligibility loaded.");
            response.put("eligibility", eligibility);

            context.getOutput().println(mapper.writeValueAsString(response));

        } catch (Exception e) {
            e.printStackTrace();

            try {
                response.put("success", false);
                response.put("message", "Server error while loading student eligibility.");
                context.getOutput().println(mapper.writeValueAsString(response));
            } catch (Exception ignored) {
            }
        }
    }
}