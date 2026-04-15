package command.finalMarks;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.FinalMarks;
import service.finalMarks.FinalMarksService;
import command.repository.ClientContext;
import command.repository.Command;
import dto.responseDto.finalMarks.FinalMarksResponseDTO;

public class GetStudentFinalMarksCommand implements Command {

    private final FinalMarksService service;
    private final ObjectMapper mapper = new ObjectMapper();

    public GetStudentFinalMarksCommand(FinalMarksService service) {
        this.service = service;
    }

    @Override
    public void execute(Object data, ClientContext context) {
        try {
            @SuppressWarnings("unchecked")
            String studentId = ((String) ((java.util.Map<String, Object>) data).get("studentId"));
            @SuppressWarnings("unchecked")
            String courseId = ((String) ((java.util.Map<String, Object>) data).get("courseId"));

            FinalMarks marks = service.getStudentFinalMarks(studentId, courseId);
            boolean success = marks != null;
            String message = success ? "Final marks retrieved successfully." : "No marks found.";
            FinalMarksResponseDTO response = new FinalMarksResponseDTO(success, message, marks);

            context.getOutput().println(mapper.writeValueAsString(response));
        } catch (Exception e) {
            e.printStackTrace();
            context.getOutput().println("{\"success\":false,\"message\":\"Server error\"}");
        }
    }
}
