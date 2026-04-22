package command.course;

import com.fasterxml.jackson.databind.ObjectMapper;
import command.repository.ClientContext;
import command.repository.Command;
import dto.responseDto.course.CourseAllResponseDTO;
import service.course.CourseService;
import service.login.AuthService;
import java.util.List;


public class GetAllCoursesCommandFull implements Command {
    private final CourseService courseService;
    private final AuthService  authService;
    private final ObjectMapper mapper = new ObjectMapper();

    public GetAllCoursesCommandFull(CourseService courseService, AuthService authService){
        this.authService = authService;
        this.courseService = courseService;
    }

    @Override
    public void execute(Object data, ClientContext context) {
        try {
            String token = context.getToken();

            if (token == null || !authService.isTokenValid(token)) {
                context.getOutput().println("{\"success\":false,\"message\":\"Unauthorized\"}");
                return;
            }

            List<CourseAllResponseDTO> courses = courseService.getAllCoursesFull();
            context.getOutput().println(mapper.writeValueAsString(courses));

        } catch (Exception e) {
            e.printStackTrace();
            context.getOutput().println("{\"success\":false,\"message\":\"Server error\"}");

        }
    }
}
