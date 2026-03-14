package command.student;

import com.fasterxml.jackson.databind.ObjectMapper;
import command.repository.ClientContext;
import command.repository.Command;
import dto.requestDto.student.StudentRequestDTO;
import dto.responseDto.student.StudentResponseDTO;
import model.Student;
import service.student.StudentService;

public class GetStudentByUserIdCommand implements Command {

    private final StudentService studentService;
    private final ObjectMapper mapper = new ObjectMapper();

    public GetStudentByUserIdCommand(StudentService studentService) {
        this.studentService = studentService;
    }

    @Override
    public void execute(Object data, ClientContext context) {
        try {
            StudentRequestDTO request = mapper.convertValue(data, StudentRequestDTO.class);

            Student student = studentService.getStudentByUserId(request.getUserId());

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
