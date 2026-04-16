package command.student;

import com.fasterxml.jackson.databind.ObjectMapper;
import command.repository.ClientContext;
import command.repository.Command;
import dto.requestDto.student.StudentRequestDTO;
import dto.responseDto.student.StudentResponseDTO;
import model.Student;
import service.login.AuthService;
import service.student.StudentService;


public class GetStudentByIdCommand implements Command {
    private final StudentService studentService;
    private final AuthService authService;
    private final ObjectMapper mapper = new ObjectMapper();

    public GetStudentByIdCommand(StudentService studentService,AuthService authService) {
        this.studentService = studentService;
        this.authService = authService;
    }

    @Override
    public void execute(Object data, ClientContext context) {
        try {
            String token = context.getToken();
            if(token == null || !authService.isTokenValid(token)){
                System.out.println("unauthorized access");
                return;
            }
            StudentRequestDTO request = mapper.convertValue(data, StudentRequestDTO.class);

            Student student = studentService.getStudentAllByUserId(request.getUserId());

            StudentResponseDTO response;
            if (student != null) {
                response = new StudentResponseDTO(true, "Student found", student);
            } else {
                response = new StudentResponseDTO(false, "No student found for user_id: " + request.getUserId(), null);
            }

            String json = mapper.writeValueAsString(response);
            System.out.println("Backend response: " + json);
            context.getOutput().println(json);

        } catch (Exception e) {
            e.printStackTrace();
            context.getOutput().println("{\"success\":false,\"message\":\"Server error\"}");
        }
    }
}
