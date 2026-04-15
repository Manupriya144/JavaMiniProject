package command.finalMarks;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.FinalMarks;
import service.finalMarks.FinalMarksService;
import command.repository.ClientContext;
import command.repository.Command;
import dto.responseDto.finalMarks.BatchFinalMarksResponseDTO;

import java.util.List;

public class GetBatchFinalMarksCommand implements Command {

    private final FinalMarksService service;
    private final ObjectMapper mapper = new ObjectMapper();

    public GetBatchFinalMarksCommand(FinalMarksService service) {
        this.service = service;
    }

    @Override
    public void execute(Object data, ClientContext context) {
        try {
            @SuppressWarnings("unchecked")
            int academicYear = ((Number)((java.util.Map<String, Object>)data).get("academicYear")).intValue();
            @SuppressWarnings("unchecked")
            int semester = ((Number)((java.util.Map<String, Object>)data).get("semester")).intValue();

            List<FinalMarks> list = service.getBatchFinalMarks(academicYear, semester);
            BatchFinalMarksResponseDTO response = new BatchFinalMarksResponseDTO(true, "Batch final marks retrieved.", list);

            context.getOutput().println(mapper.writeValueAsString(response));
        } catch (Exception e) {
            e.printStackTrace();
            context.getOutput().println("{\"success\":false,\"message\":\"Server error\"}");
        }
    }
}
