package command.medical;

import com.fasterxml.jackson.databind.ObjectMapper;
import command.repository.ClientContext;
import command.repository.Command;
import dto.requestDto.medical.MedicalIdRequestDTO;
import dto.responseDto.medical.MedicalResponseDTO;
import service.medical.MedicalService;

public class ApproveMedicalCommand implements Command {
    private final MedicalService medicalService;
    private final ObjectMapper mapper = new ObjectMapper();

    public ApproveMedicalCommand(MedicalService medicalService) {
        this.medicalService = medicalService;
    }

    @Override
    public void execute(Object data, ClientContext context) {
        try {
            if (!isTechOfficeOrHigher(context.getRole())) {
                context.getOutput().println("{\"success\":false,\"message\":\"Forbidden: insufficient role\"}");
                return;
            }
            MedicalIdRequestDTO request = mapper.convertValue(data, MedicalIdRequestDTO.class);
            boolean ok = medicalService.approveMedical(request.getMedicalId());
            MedicalResponseDTO response = ok
                    ? new MedicalResponseDTO(true, "Medical approved", null)
                    : new MedicalResponseDTO(false, "Approve medical failed", null);
            context.getOutput().println(mapper.writeValueAsString(response));
        } catch (Exception e) {
            e.printStackTrace();
            context.getOutput().println("{\"success\":false,\"message\":\"Server error\"}");
        }
    }

    private boolean isTechOfficeOrHigher(String role) {
        return "Tech_Officer".equalsIgnoreCase(role)
                || "Admin".equalsIgnoreCase(role)
                || "Dean".equalsIgnoreCase(role);
    }
}
