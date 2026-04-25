package command.registration;

import com.fasterxml.jackson.databind.ObjectMapper;
import command.repository.ClientContext;
import command.repository.Command;
import dto.responseDto.registration.StudentCourseRegistrationDTO;
import service.login.AuthService;
import service.resgistration.StudentCourseRegistrationService;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetStudentRegistrationCoursesCommand implements Command {

    private final StudentCourseRegistrationService service;
    private final AuthService authService;
    private final ObjectMapper mapper = new ObjectMapper();

    public GetStudentRegistrationCoursesCommand(StudentCourseRegistrationService service,
                                                AuthService authService) {
        this.service = service;
        this.authService = authService;
    }

    @Override
    public void execute(Object data, ClientContext context) {
        Map<String, Object> response = new HashMap<>();

        try {
            String token = context.getToken();

            if (token == null || !authService.isTokenValid(token)) {
                response.put("success", false);
                response.put("message", "Unauthorized.");
                context.getOutput().println(mapper.writeValueAsString(response));
                return;
            }

            if (!"Student".equalsIgnoreCase(context.getRole())) {
                response.put("success", false);
                response.put("message", "Only students can access course registration.");
                context.getOutput().println(mapper.writeValueAsString(response));
                return;
            }

            String studentId = context.getUserId();

            boolean open = service.isRegistrationOpen(studentId);

            if (!open) {
                response.put("success", true);
                response.put("registrationOpen", false);
                response.put("message", "Course registration is currently closed.");
                response.put("properCourses", List.of());
                response.put("repeatCourses", List.of());
                context.getOutput().println(mapper.writeValueAsString(response));
                return;
            }

            List<StudentCourseRegistrationDTO> properCourses =
                    service.getProperCourses(studentId);

            List<StudentCourseRegistrationDTO> repeatCourses =
                    service.getRepeatCourses(studentId);

            response.put("success", true);
            response.put("registrationOpen", true);
            response.put("message", "Course registration is open.");
            response.put("properCourses", properCourses);
            response.put("repeatCourses", repeatCourses);

            context.getOutput().println(mapper.writeValueAsString(response));

        } catch (Exception e) {
            e.printStackTrace();

            try {
                response.put("success", false);
                response.put("message", "Server error while loading registration courses.");
                context.getOutput().println(mapper.writeValueAsString(response));
            } catch (Exception ignored) {}
        }
    }
}