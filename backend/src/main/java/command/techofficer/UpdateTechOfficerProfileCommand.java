package command.techofficer;

import com.fasterxml.jackson.databind.ObjectMapper;
import command.repository.ClientContext;
import command.repository.Command;
import dto.requestDto.techofficer.UpdateTechOfficerProfileRequestDTO;
import dto.responseDto.login.ResponseDTO;
import service.techofficer.TechOfficerService;

public class UpdateTechOfficerProfileCommand implements Command {
    private final TechOfficerService techOfficerService;
    private final ObjectMapper mapper = new ObjectMapper();

    public UpdateTechOfficerProfileCommand(TechOfficerService techOfficerService) {
        this.techOfficerService = techOfficerService;
    }

    @Override
    public void execute(Object data, ClientContext context) {
        try {
            if (!isAllowedRole(context.getRole())) {
                context.getOutput().println("{\"success\":false,\"message\":\"Forbidden: insufficient role\"}");
                return;
            }

            UpdateTechOfficerProfileRequestDTO request = mapper.convertValue(data, UpdateTechOfficerProfileRequestDTO.class);
            boolean updated = techOfficerService.updateTechOfficerProfile(request);

            if (updated) {
                context.getOutput().println(mapper.writeValueAsString(new ResponseDTO(true, "Technical officer profile updated", null)));
            } else {
                context.getOutput().println(mapper.writeValueAsString(new ResponseDTO(false, "Failed to update technical officer profile", null)));
            }
        } catch (Exception e) {
            e.printStackTrace();
            context.getOutput().println("{\"success\":false,\"message\":\"Server error\"}");
        }
    }

    private boolean isAllowedRole(String role) {
        return "Tech_Officer".equalsIgnoreCase(role) || "Admin".equalsIgnoreCase(role) || "Dean".equalsIgnoreCase(role);
    }
}
