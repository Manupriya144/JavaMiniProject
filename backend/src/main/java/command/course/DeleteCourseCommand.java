package command.course;

import com.fasterxml.jackson.databind.ObjectMapper;
import command.repository.ClientContext;
import command.repository.Command;
import service.course.CourseService;
import service.login.AuthService;

import java.util.HashMap;
import java.util.Map;

public class DeleteCourseCommand implements Command {

    private final CourseService courseService;
    private final AuthService authService;
    private final ObjectMapper mapper = new ObjectMapper();

    public DeleteCourseCommand(CourseService courseService, AuthService authService) {
        this.courseService = courseService;
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

            if (!"Admin".equalsIgnoreCase(context.getRole())) {
                response.put("success", false);
                response.put("message", "Only admin can delete courses.");
                context.getOutput().println(mapper.writeValueAsString(response));
                return;
            }

            Map<?, ?> map = mapper.convertValue(data, Map.class);
            Object courseIdObj = map.get("courseId");

            if (courseIdObj == null || courseIdObj.toString().isBlank()) {
                response.put("success", false);
                response.put("message", "Course ID is required.");
                context.getOutput().println(mapper.writeValueAsString(response));
                return;
            }

            String courseId = courseIdObj.toString();

            boolean deleted = courseService.deleteCourse(courseId);

            response.put("success", deleted);
            response.put("message", deleted ? "Course deleted successfully." : "Failed to delete course.");

            context.getOutput().println(mapper.writeValueAsString(response));

        } catch (Exception e) {
            e.printStackTrace();

            try {
                response.put("success", false);
                response.put("message", "Server error while deleting course.");
                context.getOutput().println(mapper.writeValueAsString(response));
            } catch (Exception ignored) {}
        }
    }
}