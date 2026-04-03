package command.course;

import com.fasterxml.jackson.databind.ObjectMapper;
import command.repository.ClientContext;
import command.repository.Command;
import dto.requestDto.course.AddCourseRequestDTO;
import service.course.CourseService;
import service.login.AuthService;

public class AddCourseCommand implements Command {

    private final CourseService courseService;
    private final AuthService authService;
    private final ObjectMapper mapper = new ObjectMapper();

    public AddCourseCommand(CourseService courseService, AuthService authService) {
        this.courseService = courseService;
        this.authService = authService;
    }

    @Override
    public void execute(Object data, ClientContext context) {
        try {
            String token = context.getToken();
            System.out.println("token : " + token);

            if (token == null || !authService.isTokenValid(token)) {
                context.getOutput().println("{\"success\":false,\"message\":\"Unauthorized\"}");
                return;
            }

            AddCourseRequestDTO dto = mapper.convertValue(data, AddCourseRequestDTO.class);

            boolean success = courseService.addCourse(dto);

            if (success) {
                context.getOutput().println("{\"success\":true,\"message\":\"Course added successfully\"}");
            } else {
                context.getOutput().println("{\"success\":false,\"message\":\"Course creation failed\"}");
            }

        } catch (Exception e) {
            e.printStackTrace();
            context.getOutput().println("{\"success\":false,\"message\":\"Server error\"}");
        }
    }
}