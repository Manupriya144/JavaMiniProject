package command.course;

import com.fasterxml.jackson.databind.ObjectMapper;
import command.repository.ClientContext;
import command.repository.Command;
import service.courseMeterial.CourseMaterialService;
import service.login.AuthService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetStudentCourseMaterialsCommand implements Command {

    private final ObjectMapper mapper = new ObjectMapper();
    private final AuthService authService;
    private final CourseMaterialService courseMaterialService;

    public GetStudentCourseMaterialsCommand(CourseMaterialService courseMaterialService,
                                            AuthService authService) {
        this.courseMaterialService = courseMaterialService;
        this.authService = authService;
    }

    @Override
    public void execute(Object data, ClientContext context) {
        Map<String, Object> response = new HashMap<>();

        try {
            String token = context.getToken();

            if (token == null || !authService.isTokenValid(token)) {
                response.put("success", false);
                response.put("message", "Invalid or expired token.");
                context.getOutput().println(mapper.writeValueAsString(response));
                return;
            }

            if (!"Student".equalsIgnoreCase(context.getRole())) {
                response.put("success", false);
                response.put("message", "Only students can view student course materials.");
                context.getOutput().println(mapper.writeValueAsString(response));
                return;
            }

            Map<?, ?> dataMap = mapper.convertValue(data, Map.class);
            Object courseIdObj = dataMap.get("courseId");

            if (courseIdObj == null || courseIdObj.toString().isBlank()) {
                response.put("success", false);
                response.put("message", "Course ID is required.");
                context.getOutput().println(mapper.writeValueAsString(response));
                return;
            }

            String courseId = courseIdObj.toString();

            List<?> materials =
                    courseMaterialService.getMaterialsByCourseForStudent(courseId);

            response.put("success", true);
            response.put("message", "Course materials loaded successfully.");
            response.put("materials", materials);

            context.getOutput().println(mapper.writeValueAsString(response));

        } catch (Exception e) {
            e.printStackTrace();

            try {
                response.put("success", false);
                response.put("message", "Server error while loading course materials.");
                context.getOutput().println(mapper.writeValueAsString(response));
            } catch (Exception ignored) {
            }
        }
    }
}