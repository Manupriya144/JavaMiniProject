package command.grade;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.Grade;
import service.grade.GradeService;
import command.repository.ClientContext;
import command.repository.Command;
import dto.responseDto.grade.GradeResponseDTO;

public class GetStudentGradesCommand implements Command {

    private final GradeService service;
    private final ObjectMapper mapper = new ObjectMapper();

    public GetStudentGradesCommand(GradeService service) {
        this.service = service;
    }

    @Override
    public void execute(Object data, ClientContext context) {
        try {
            @SuppressWarnings("unchecked")
            String studentId = (String)((java.util.Map<String,Object>)data).get("studentId");
            @SuppressWarnings("unchecked")
            String courseId = (String)((java.util.Map<String,Object>)data).get("courseId");

            Grade grade = service.getStudentGrade(studentId, courseId);
            boolean success = grade != null;
            String message = success ? "Grade retrieved." : "No grade found.";
            GradeResponseDTO response = new GradeResponseDTO(success, message, grade);

            context.getOutput().println(mapper.writeValueAsString(response));
        } catch (Exception e) {
            e.printStackTrace();
            context.getOutput().println("{\"success\":false,\"message\":\"Server error\"}");
        }
    }
}