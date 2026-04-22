package command.techofficer;

import com.fasterxml.jackson.databind.ObjectMapper;
import command.repository.ClientContext;
import command.repository.Command;
import dto.responseDto.login.ResponseDTO;
import dto.responseDto.techofficer.TechOfficerProfileResponseDTO;
import model.TechOfficer;
import service.techofficer.TechOfficerService;

public class GetMyTechOfficerProfileCommand implements Command {
    private final TechOfficerService techOfficerService;
    private final ObjectMapper mapper = new ObjectMapper();

    public GetMyTechOfficerProfileCommand(TechOfficerService techOfficerService) {
        this.techOfficerService = techOfficerService;
    }

    @Override
    public void execute(Object data, ClientContext context) {
        try {
            if (!isAllowedRole(context.getRole())) {
                context.getOutput().println("{\"success\":false,\"message\":\"Forbidden: insufficient role\"}");
                return;
            }

            String userId = context.getUserId();
            if (userId == null || userId.isBlank()) {
                context.getOutput().println(mapper.writeValueAsString(new ResponseDTO(false, "Missing user id", null)));
                return;
            }

            TechOfficer techOfficer = techOfficerService.getTechOfficerProfileByUserId(userId);
            if (techOfficer == null) {
                context.getOutput().println(mapper.writeValueAsString(new ResponseDTO(false, "Technical officer not found", null)));
                return;
            }

            TechOfficerProfileResponseDTO responseDTO = new TechOfficerProfileResponseDTO(
                    techOfficer.getUserId(),
                    techOfficer.getUsername(),
                    techOfficer.getEmail(),
                    techOfficer.getContactNumber(),
                    techOfficer.getProfilePicture(),
                    techOfficer.getRole(),
                    techOfficer.getDepartmentId()
            );
            context.getOutput().println(mapper.writeValueAsString(new ResponseDTO(true, "Technical officer profile loaded", responseDTO)));
        } catch (Exception e) {
            e.printStackTrace();
            context.getOutput().println("{\"success\":false,\"message\":\"Server error\"}");
        }
    }

    private boolean isAllowedRole(String role) {
        return "Tech_Officer".equalsIgnoreCase(role) || "Admin".equalsIgnoreCase(role) || "Dean".equalsIgnoreCase(role);
    }
}

