package command.eligibility;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.Eligibility;
import service.eligibility.EligibilityService;
import command.repository.ClientContext;
import command.repository.Command;
import dto.requestDto.eligibility.FullEligibilityRequestDTO;
import dto.responseDto.eligibility.FullEligibilityResponseDTO;

public class CheckFullEligibilityCommand implements Command {

    private final EligibilityService service;
    private final ObjectMapper mapper = new ObjectMapper();

    public CheckFullEligibilityCommand(EligibilityService service) {
        this.service = service;
    }

    @Override
    public void execute(Object data, ClientContext context) {
        try {
            FullEligibilityRequestDTO dto = mapper.convertValue(data, FullEligibilityRequestDTO.class);
            Eligibility eligibility = service.getStudentEligibility(dto.getStudentId(), dto.getCourseId());
            boolean success = eligibility != null;
            String message = success ? "Eligibility retrieved." : "No eligibility found.";
            FullEligibilityResponseDTO response = new FullEligibilityResponseDTO(success, message, eligibility);

            context.getOutput().println(mapper.writeValueAsString(response));
        } catch (Exception e) {
            e.printStackTrace();
            context.getOutput().println("{\"success\":false,\"message\":\"Server error\"}");
        }
    }
}
