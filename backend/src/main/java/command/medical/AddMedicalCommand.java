package command.medical;

import com.fasterxml.jackson.databind.ObjectMapper;
import command.repository.ClientContext;
import command.repository.Command;
import dto.requestDto.medical.AddMedicalRequestDTO;
import dto.responseDto.medical.MedicalResponseDTO;
import model.Medical;
import service.medical.MedicalService;

public class AddMedicalCommand implements Command {
    private final MedicalService medicalService;
    private final ObjectMapper mapper = new ObjectMapper();

    public AddMedicalCommand(MedicalService medicalService) {
        this.medicalService = medicalService;
    }

    @Override
    public void execute(Object data, ClientContext context) {
        try {
            if (!isTechOfficeOrHigher(context.getRole())) {
                context.getOutput().println("{\"success\":false,\"message\":\"Forbidden: insufficient role\"}");
                return;
            }
            AddMedicalRequestDTO request = mapper.convertValue(data, AddMedicalRequestDTO.class);
            Medical medical = medicalService.addMedical(
                    request.getStudentId(),
                    request.getCourseId(),
                    request.getExamType(),
                    request.getDateSubmitted(),
                    request.getMedicalCopy()
            );
            MedicalResponseDTO response = medical != null
                    ? new MedicalResponseDTO(true, "Medical added successfully", medical)
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
