package command.grade;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.Grade;
import service.grade.GradeService;
import command.repository.ClientContext;
import command.repository.Command;
import dto.responseDto.grade.BatchGradeResponseDTO;

import java.util.List;

public class GetBatchGradesCommand implements Command {

    private final GradeService service;
    private final ObjectMapper mapper = new ObjectMapper();

    public GetBatchGradesCommand(GradeService service) {
        this.service = service;
    }

    @Override
    public void execute(Object data, ClientContext context) {
        try {
            @SuppressWarnings("unchecked")
            int academicYear = ((Number)((java.util.Map<String,Object>)data).get("academicYear")).intValue();
            @SuppressWarnings("unchecked")
            int semester = ((Number)((java.util.Map<String,Object>)data).get("semester")).intValue();

            List<Grade> list = service.getBatchGrades(academicYear, semester);
            BatchGradeResponseDTO response = new BatchGradeResponseDTO(true, "Batch grades retrieved.", list);

            context.getOutput().println(mapper.writeValueAsString(response));
        } catch (Exception e) {
            e.printStackTrace();
            context.getOutput().println("{\"success\":false,\"message\":\"Server error\"}");
        }
    }
}
