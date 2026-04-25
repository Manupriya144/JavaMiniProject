package command.student;

import com.fasterxml.jackson.databind.ObjectMapper;
import command.repository.ClientContext;
import command.repository.Command;
import dto.responseDto.student.StudentRegisteredCourseDTO;
import service.login.AuthService;
import service.student.StudentService;

import java.util.List;
import java.util.Map;

public class GetStudentRegisteredCoursesCommand implements Command {

    private final StudentService studentService;
    private final AuthService authService;
    private final ObjectMapper mapper = new ObjectMapper();

    public GetStudentRegisteredCoursesCommand(StudentService studentService, AuthService authService) {
        this.studentService = studentService;
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

            String studentId = context.getUserId();

            List<StudentRegisteredCourseDTO> courses =
                    studentService.getRegisteredCourses(studentId);

            Map<String, Object> response = Map.of(
                    "success", true,
                    "courses", courses
            );

            context.getOutput().println(mapper.writeValueAsString(response));

        } catch (Exception e) {
            e.printStackTrace();
            context.getOutput().println("{\"success\":false,\"message\":\"Server error\"}");
        }
    }
}