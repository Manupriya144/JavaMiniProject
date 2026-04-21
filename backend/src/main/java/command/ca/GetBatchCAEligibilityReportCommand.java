package command.ca;

import com.fasterxml.jackson.databind.ObjectMapper;
import command.repository.ClientContext;
import command.repository.Command;
import dto.requestDto.ca.GetBatchCAEligibilityReportRequestDTO;
import dto.responseDto.ca.CAResponseDTO;
import service.ca.CAMarkService;

public class GetBatchCAEligibilityReportCommand implements Command {
    private final CAMarkService caMarkService;
    private final ObjectMapper mapper = new ObjectMapper();

    public GetBatchCAEligibilityReportCommand(CAMarkService caMarkService) {
        this.caMarkService = caMarkService;
    }

    @Override
    public void execute(Object data, ClientContext context) {
        try {
            if (!isAllowedRole(context.getRole())) {
                context.getOutput().println("{\"success\":false,\"message\":\"Forbidden: insufficient role\"}");
                return;
            }
            GetBatchCAEligibilityReportRequestDTO request = mapper.convertValue(data, GetBatchCAEligibilityReportRequestDTO.class);
            CAResponseDTO response = new CAResponseDTO(
                    true,
                    "Batch CA eligibility report loaded",
                    caMarkService.getBatchCAEligibilityReport(request.getBatch(), request.getCourseId())
            );
            context.getOutput().println(mapper.writeValueAsString(response));
        } catch (Exception e) {
            e.printStackTrace();
            context.getOutput().println("{\"success\":false,\"message\":\"Server error\"}");
        }
    }

    private boolean isAllowedRole(String role) {
        return "Tech_Officer".equalsIgnoreCase(role)
                || "Lecturer".equalsIgnoreCase(role)
                || "Admin".equalsIgnoreCase(role)
                || "Dean".equalsIgnoreCase(role);
    }
}
