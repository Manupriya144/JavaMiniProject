package command.grade;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.Grade;
import service.grade.GradeService;
import command.repository.ClientContext;
import command.repository.Command;
import dto.requestDto.grade.GradeRequestDTO;
import dto.responseDto.grade.GradeResponseDTO;

public class GenerateGradeCommand implements Command {

    private final GradeService service;
    private final ObjectMapper mapper = new ObjectMapper();

    public GenerateGradeCommand(GradeService service) {
        this.service = service;
    }

    @Override
    public void execute(Object data, ClientContext context) {
        try {
            GradeRequestDTO dto = mapper.convertValue(data, GradeRequestDTO.class);
            Grade grade = new Grade(dto.getStudentId(), dto.getCourseId(), dto.getAcademicYear(), dto.getSemester(), dto.getGrade());
            boolean success = service.addGrade(grade);
            String message = success ? "Grade generated successfully." : "Failed to generate grade.";
            GradeResponseDTO response = new GradeResponseDTO(success, message, grade);
            context.getOutput().println(mapper.writeValueAsString(response));
        } catch (Exception e) {
            e.printStackTrace();
            context.getOutput().println("{\"success\":false,\"message\":\"Server error\"}");
        }
    }
}
