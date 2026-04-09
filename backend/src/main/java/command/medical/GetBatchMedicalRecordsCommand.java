package command.medical;

import com.fasterxml.jackson.databind.ObjectMapper;
import command.repository.ClientContext;
import command.repository.Command;
import dto.requestDto.medical.GetBatchMedicalRecordsRequestDTO;
import dto.responseDto.medical.MedicalResponseDTO;
import model.Medical;
import service.medical.MedicalService;

import java.util.List;

public class GetBatchMedicalRecordsCommand implements Command {
    private final MedicalService medicalService;
    private final ObjectMapper mapper = new ObjectMapper();

    public GetBatchMedicalRecordsCommand(MedicalService medicalService) {
        this.medicalService = medicalService;
    }

    @Override
    public void execute(Object data, ClientContext context) {
        try {
            if (!isTechOfficeOrHigher(context.getRole())) {
                context.getOutput().println("{\"success\":false,\"message\":\"Forbidden: insufficient role\"}");
                return;
            }
            GetBatchMedicalRecordsRequestDTO request = mapper.convertValue(data, GetBatchMedicalRecordsRequestDTO.class);
            List<Medical> records = medicalService.getBatchMedicalRecords(request.getBatch());
            MedicalResponseDTO response = new MedicalResponseDTO(true, "Batch medical records fetched", records);
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
