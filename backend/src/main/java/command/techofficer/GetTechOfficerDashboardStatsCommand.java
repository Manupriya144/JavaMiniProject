package command.techofficer;

import com.fasterxml.jackson.databind.ObjectMapper;
import command.repository.ClientContext;
import command.repository.Command;
import dto.responseDto.login.ResponseDTO;
import service.techofficer.TechOfficerService;

public class GetTechOfficerDashboardStatsCommand implements Command {
    private final TechOfficerService techOfficerService;
    private final ObjectMapper mapper = new ObjectMapper();

    public GetTechOfficerDashboardStatsCommand(TechOfficerService techOfficerService) {
        this.techOfficerService = techOfficerService;
    }

    @Override
    public void execute(Object data, ClientContext context) {
        try {
            if (!isAllowedRole(context.getRole())) {
                context.getOutput().println("{\"success\":false,\"message\":\"Forbidden: insufficient role\"}");
                return;
            }

            ResponseDTO response = new ResponseDTO(
                    true,
                    "Technical officer dashboard stats loaded",
                    techOfficerService.getDashboardStats()
            );
            context.getOutput().println(mapper.writeValueAsString(response));
        } catch (Exception e) {
            e.printStackTrace();
            context.getOutput().println("{\"success\":false,\"message\":\"Server error\"}");
        }
    }

    private boolean isAllowedRole(String role) {
        return "Tech_Officer".equalsIgnoreCase(role) || "Admin".equalsIgnoreCase(role) || "Dean".equalsIgnoreCase(role);
    }
}
