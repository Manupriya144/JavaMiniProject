package command.attendance;

import com.fasterxml.jackson.databind.ObjectMapper;
import command.repository.ClientContext;
import command.repository.Command;
import dto.requestDto.course.UpdateCourseReqDTO;
import service.course.CourseService;
import service.login.AuthService;

import java.util.HashMap;
import java.util.Map;

public class UpdateCourseCommand implements Command {

    private final CourseService courseService;
    private final AuthService authService;
    private final ObjectMapper mapper = new ObjectMapper();

    public UpdateCourseCommand(CourseService courseService, AuthService authService) {
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
                response.put("message", "Only admin can update courses.");
                context.getOutput().println(mapper.writeValueAsString(response));
                return;
            }

            UpdateCourseReqDTO dto = mapper.convertValue(data, UpdateCourseReqDTO.class);

            if (dto.getCourseId() == null || dto.getCourseId().isBlank()) {
                response.put("success", false);
                response.put("message", "Course ID is required.");
                context.getOutput().println(mapper.writeValueAsString(response));
                return;
            }

            boolean updated = courseService.updateCourse(dto);

            response.put("success", updated);
            response.put("message", updated ? "Course updated successfully." : "Failed to update course.");

            context.getOutput().println(mapper.writeValueAsString(response));

        } catch (Exception e) {
            e.printStackTrace();

            try {
                response.put("success", false);
                response.put("message", "Server error while updating course.");
                context.getOutput().println(mapper.writeValueAsString(response));
            } catch (Exception ignored) {}
        }
    }
}