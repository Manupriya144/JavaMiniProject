package command.ca;

import com.fasterxml.jackson.databind.ObjectMapper;
import command.repository.ClientContext;
import command.repository.Command;
import dto.requestDto.ca.UpdateCAMarksRequestDTO;
import dto.responseDto.ca.CAResponseDTO;
import service.ca.CAMarkService;

public class UpdateCAMarksCommand implements Command {
    private final CAMarkService caMarkService;
    private final ObjectMapper mapper = new ObjectMapper();

    public UpdateCAMarksCommand(CAMarkService caMarkService) {
        this.caMarkService = caMarkService;
    }

    @Override
    public void execute(Object data, ClientContext context) {
        try {
            if (!isAllowedRole(context.getRole())) {
                context.getOutput().println("{\"success\":false,\"message\":\"Forbidden: insufficient role\"}");
                return;
            }
            UpdateCAMarksRequestDTO request = mapper.convertValue(data, UpdateCAMarksRequestDTO.class);
            boolean ok = caMarkService.updateCAMarks(request.getMarkId(), request.getMarks());
            CAResponseDTO response = ok
                    ? new CAResponseDTO(true, "CA mark updated", null)
                    : new CAResponseDTO(false, "CA mark update failed", null);
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
