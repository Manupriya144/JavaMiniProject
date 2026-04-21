package command.ca;

import com.fasterxml.jackson.databind.ObjectMapper;
import command.repository.ClientContext;
import command.repository.Command;
import dto.requestDto.ca.GetBatchCAMarksRequestDTO;
import dto.responseDto.ca.CAResponseDTO;
import service.ca.CAMarkService;

public class GetBatchCAMarksCommand implements Command {
    private final CAMarkService caMarkService;
    private final ObjectMapper mapper = new ObjectMapper();

    public GetBatchCAMarksCommand(CAMarkService caMarkService) {
        this.caMarkService = caMarkService;
    }

    @Override
    public void execute(Object data, ClientContext context) {
        try {
            if (!isAllowedRole(context.getRole())) {
                context.getOutput().println("{\"success\":false,\"message\":\"Forbidden: insufficient role\"}");
                return;
            }
            GetBatchCAMarksRequestDTO request = mapper.convertValue(data, GetBatchCAMarksRequestDTO.class);
            CAResponseDTO response = new CAResponseDTO(
                    true,
                    "Batch CA marks loaded",
                    caMarkService.getBatchCAMarks(request.getBatch(), request.getCourseId())
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
