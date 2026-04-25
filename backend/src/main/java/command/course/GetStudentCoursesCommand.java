package command.course;

import com.fasterxml.jackson.databind.ObjectMapper;
import command.repository.ClientContext;
import command.repository.Command;
import dto.requestDto.course.GetStudentCoursesReqDTO;
import dto.responseDto.course.CourseAllResponseDTO;
import service.course.StudentCourseService;
import service.login.AuthService;

import java.util.List;

public class GetStudentCoursesCommand implements Command {
    private final StudentCourseService studentCourseService;
    private final AuthService authService;
    private final ObjectMapper mapper = new ObjectMapper();

    public GetStudentCoursesCommand(StudentCourseService studentCourseService, AuthService authService) {
        this.studentCourseService = studentCourseService;
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

            GetStudentCoursesReqDTO req = mapper.convertValue(data, GetStudentCoursesReqDTO.class);
            if (req == null || req.getUserId() == null || req.getUserId().isBlank()) {
                context.getOutput().println("{\"success\":false,\"message\":\"userId is required\"}");
                return;
            }

            List<CourseAllResponseDTO> courses = studentCourseService.getCoursesForStudent(req.getUserId());
            context.getOutput().println(mapper.writeValueAsString(courses));

        } catch (Exception e) {
            e.printStackTrace();
            context.getOutput().println("{\"success\":false,\"message\":\"Server error\"}");
        }
    }
}

