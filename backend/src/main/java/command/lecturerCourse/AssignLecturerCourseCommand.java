package command.lecturerCourse;

import com.fasterxml.jackson.databind.ObjectMapper;
import command.repository.ClientContext;
import command.repository.Command;
import dto.requestDto.lecture_course.LecturerCourseRequestDTO;
import dto.responseDto.lecture_course.LecturerCourseResponseDTO;
import service.lecturerCourse.LecturerCourseService;
import service.login.AuthService;

public class AssignLecturerCourseCommand implements Command {

    private final ObjectMapper mapper = new ObjectMapper();
    private final LecturerCourseService lecturerCourseService;
    private final AuthService authService;

    public AssignLecturerCourseCommand(LecturerCourseService lecturerCourseService, AuthService authService) {
        this.lecturerCourseService = lecturerCourseService;
        this.authService = authService;
    }

    @Override
    public void execute(Object data, ClientContext context) {
        try {
            String token = context.getToken();

            if (token == null || !authService.isTokenValid(token)) {
                context.getOutput().println(mapper.writeValueAsString(
                        new LecturerCourseResponseDTO(false, "Unauthorized: invalid or expired token")
                ));
                return;
            }

            LecturerCourseRequestDTO requestDTO =
                    mapper.convertValue(data, LecturerCourseRequestDTO.class);

            String result = lecturerCourseService.assignLecturerToCourse(
                    requestDTO.getLecturerId(),
                    requestDTO.getCourseId()
            );

            LecturerCourseResponseDTO response;

            switch (result) {
                case "SUCCESS" ->
                        response = new LecturerCourseResponseDTO(true, "Lecturer assigned to course successfully");
                case "INVALID_LECTURER_ID" ->
                        response = new LecturerCourseResponseDTO(false, "Lecturer ID is required");
                case "INVALID_COURSE_ID" ->
                        response = new LecturerCourseResponseDTO(false, "Course ID is required");
                case "DUPLICATE_OR_INVALID_REFERENCE" ->
                        response = new LecturerCourseResponseDTO(false, "Already assigned or invalid lecturer/course");
                default ->
                        response = new LecturerCourseResponseDTO(false, "Failed to assign lecturer to course");
            }

            context.getOutput().println(mapper.writeValueAsString(response));

        } catch (Exception e) {
            e.printStackTrace();
            try {
                context.getOutput().println(mapper.writeValueAsString(
                        new LecturerCourseResponseDTO(false, "Server error while assigning lecturer to course")
                ));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}