package command.ca;

import com.fasterxml.jackson.databind.ObjectMapper;
import command.repository.ClientContext;
import command.repository.Command;
import dto.requestDto.ca.UploadCAMarksRequestDTO;
import dto.responseDto.ca.CAResponseDTO;
import model.ca.CAMark;
import service.ca.CAMarkService;

public class UploadCAMarksCommand implements Command {
    private final CAMarkService caMarkService;
    private final ObjectMapper mapper = new ObjectMapper();

    public UploadCAMarksCommand(CAMarkService caMarkService) {
        this.caMarkService = caMarkService;
    }

    @Override
    public void execute(Object data, ClientContext context) {
        try {
            if (!isAllowedRole(context.getRole())) {
                context.getOutput().println("{\"success\":false,\"message\":\"Forbidden: insufficient role\"}");
                return;
            }
            UploadCAMarksRequestDTO request = mapper.convertValue(data, UploadCAMarksRequestDTO.class);
            CAMark saved = caMarkService.uploadCAMarks(request.getStudentId(), request.getAssessmentTypeId(), request.getMarks());
            CAResponseDTO response = saved != null
                    ? new CAResponseDTO(true, "CA mark uploaded", saved)
                    : new CAResponseDTO(false, "Validation failed for CA upload", null);
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
