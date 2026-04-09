package command.medical;

import com.fasterxml.jackson.databind.ObjectMapper;
import command.repository.ClientContext;
import command.repository.Command;
import dto.requestDto.medical.UpdateMedicalRequestDTO;
import dto.responseDto.medical.MedicalResponseDTO;
import service.medical.MedicalService;

public class UpdateMedicalCommand implements Command {
    private final MedicalService medicalService;
    private final ObjectMapper mapper = new ObjectMapper();

    public UpdateMedicalCommand(MedicalService medicalService) {
        this.medicalService = medicalService;
    }

    @Override
    public void execute(Object data, ClientContext context) {
        try {
            if (!isTechOfficeOrHigher(context.getRole())) {
                context.getOutput().println("{\"success\":false,\"message\":\"Forbidden: insufficient role\"}");
                return;
            }
            UpdateMedicalRequestDTO request = mapper.convertValue(data, UpdateMedicalRequestDTO.class);
            boolean ok = medicalService.updateMedical(
                    request.getMedicalId(),
                    request.getStudentId(),
                    request.getCourseId(),
                    request.getExamType(),
                    request.getDateSubmitted(),
                    request.getMedicalCopy()
            );
            MedicalResponseDTO response = ok
                    ? new MedicalResponseDTO(true, "Medical updated successfully", null)
                    : new MedicalResponseDTO(false, medicalService.getLastValidationMessage(), null);
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
