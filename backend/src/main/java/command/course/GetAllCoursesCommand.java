package command.course;

import com.fasterxml.jackson.databind.ObjectMapper;
import command.repository.ClientContext;
import command.repository.Command;
import dto.responseDto.course.CourseResponseDTO;
import service.course.CourseService;
import service.login.AuthService;

import java.util.List;

public class GetAllCoursesCommand implements Command {

    private final ObjectMapper mapper = new ObjectMapper();
    private final CourseService courseService;
    private final AuthService authService;

    public GetAllCoursesCommand(CourseService courseService, AuthService authService) {
        this.courseService = courseService;
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

            List<CourseResponseDTO> courses = courseService.getAllCourses();
            context.getOutput().println(mapper.writeValueAsString(courses));

        } catch (Exception e) {
            e.printStackTrace();
            context.getOutput().println("{\"success\":false,\"message\":\"Server error\"}");
        }
    }
}