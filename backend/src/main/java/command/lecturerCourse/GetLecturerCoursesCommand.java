package command.lecturerCourse;

import com.fasterxml.jackson.databind.ObjectMapper;

import command.repository.ClientContext;
import command.repository.Command;
import dto.responseDto.lecture_course.LecturerCourseItemDTO;
import service.lecturerCourse.LecturerCourseService;
import service.login.AuthService;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetLecturerCoursesCommand implements Command {

    private final LecturerCourseService lecturerCourseService;
    private final AuthService authService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public GetLecturerCoursesCommand(LecturerCourseService lecturerCourseService, AuthService authService) {
        this.lecturerCourseService = lecturerCourseService;
        this.authService = authService;
    }

    @Override
    public void execute(Object data, ClientContext context) {
        Map<String, Object> response = new HashMap<>();

        try {
            String token = context.getToken();

            if (token == null || !authService.isTokenValid(token)) {
                response.put("success", false);
                response.put("message", "Invalid or missing token.");
                context.getOutput().println(objectMapper.writeValueAsString(response));
                return;
            }

            if (!"Lecturer".equals(context.getRole())) {
                response.put("success", false);
                response.put("message", "Only lecturers can load lecturer courses.");
                context.getOutput().println(objectMapper.writeValueAsString(response));
                return;
            }

            String lecturerId = context.getUserId();
            System.out.println("GET_LECTURER_COURSES lecturerId = " + lecturerId);

            List<LecturerCourseItemDTO> courses = lecturerCourseService.getLecturerCourses(lecturerId);

            response.put("success", true);
            response.put("courses", courses);

        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", e.getMessage());
        }

        try {
            context.getOutput().println(objectMapper.writeValueAsString(response));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}