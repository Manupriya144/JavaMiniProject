package command.eligibility;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.Eligibility;
import service.eligibility.EligibilityService;
import command.repository.ClientContext;
import command.repository.Command;
import dto.responseDto.eligibility.BatchFullEligibilityResponseDTO;

import java.util.List;

public class GetBatchFullEligibilityReportCommand implements Command {

    private final EligibilityService service;
    private final ObjectMapper mapper = new ObjectMapper();

    public GetBatchFullEligibilityReportCommand(EligibilityService service) {
        this.service = service;
    }

    @Override
    public void execute(Object data, ClientContext context) {
        try {
            @SuppressWarnings("unchecked")
            int academicYear = ((Number)((java.util.Map<String, Object>)data).get("academicYear")).intValue();
            @SuppressWarnings("unchecked")
            int semester = ((Number)((java.util.Map<String, Object>)data).get("semester")).intValue();

            List<Eligibility> list = service.getBatchEligibility(academicYear, semester);
            BatchFullEligibilityResponseDTO response = new BatchFullEligibilityResponseDTO(true, "Batch eligibility report retrieved.", list);

            context.getOutput().println(mapper.writeValueAsString(response));
        } catch (Exception e) {
            e.printStackTrace();
            context.getOutput().println("{\"success\":false,\"message\":\"Server error\"}");
        }
    }
}
