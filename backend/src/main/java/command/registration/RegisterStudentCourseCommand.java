package command.registration;

import com.fasterxml.jackson.databind.ObjectMapper;
import command.repository.ClientContext;
import command.repository.Command;
import dto.requestDto.registration.RegisterCourseReqDTO;
import service.login.AuthService;
import service.resgistration.StudentCourseRegistrationService;


import java.util.HashMap;
import java.util.Map;

public class RegisterStudentCourseCommand implements Command {

    private final StudentCourseRegistrationService service;
    private final AuthService authService;
    private final ObjectMapper mapper = new ObjectMapper();

    public RegisterStudentCourseCommand(StudentCourseRegistrationService service,
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
                response.put("message", "Only students can register courses.");
                context.getOutput().println(mapper.writeValueAsString(response));
                return;
            }

            String studentId = context.getUserId();

            boolean open = service.isRegistrationOpen(studentId);

            if (!open) {
                response.put("success", false);
                response.put("message", "Course registration is closed.");
                context.getOutput().println(mapper.writeValueAsString(response));
                return;
            }

            RegisterCourseReqDTO dto =
                    mapper.convertValue(data, RegisterCourseReqDTO.class);

            if (dto.getCourseId() == null || dto.getCourseId().isBlank()) {
                response.put("success", false);
                response.put("message", "Course ID is required.");
                context.getOutput().println(mapper.writeValueAsString(response));
                return;
            }

            if (dto.getAcademicYear() <= 0) {
                response.put("success", false);
                response.put("message", "Academic year is required.");
                context.getOutput().println(mapper.writeValueAsString(response));
                return;
            }

            if (dto.getSemester() == null || dto.getSemester().isBlank()) {
                response.put("success", false);
                response.put("message", "Semester is required.");
                context.getOutput().println(mapper.writeValueAsString(response));
                return;
            }

            if (dto.getRegistrationType() == null || dto.getRegistrationType().isBlank()) {
                response.put("success", false);
                response.put("message", "Registration type is required.");
                context.getOutput().println(mapper.writeValueAsString(response));
                return;
            }

            boolean registered = service.registerCourse(studentId, dto);

            response.put("success", registered);
            response.put("message", registered
                    ? "Course registered successfully."
                    : "Failed to register course. You may already be registered.");

            context.getOutput().println(mapper.writeValueAsString(response));

        } catch (Exception e) {
            e.printStackTrace();

            try {
                response.put("success", false);
                response.put("message", "Server error while registering course.");
                context.getOutput().println(mapper.writeValueAsString(response));
            } catch (Exception ignored) {}
        }
    }
}