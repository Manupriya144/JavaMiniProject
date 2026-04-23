package command.courseMaterial;

import com.fasterxml.jackson.databind.ObjectMapper;
import command.repository.ClientContext;
import command.repository.Command;
import dto.requestDto.courseMaterial.UpdateCourseMaterialDeadlineReqDTO;
import dto.responseDto.courseMaterial.CourseMaterialActionResponseDTO;
import service.courseMaterial.CourseMaterialService;

public class UpdateCourseMaterialDeadlineCommand implements Command {

    private final CourseMaterialService courseMaterialService;
    private final ObjectMapper mapper = new ObjectMapper();

    public UpdateCourseMaterialDeadlineCommand(CourseMaterialService courseMaterialService) {
        this.courseMaterialService = courseMaterialService;
    }

    @Override
    public void execute(Object data, ClientContext context) {
        try {
            if (!isLecturerOrHigher(context.getRole())) {
                context.getOutput().println("{\"success\":false,\"message\":\"Forbidden: insufficient role\"}");
                return;
            }

            UpdateCourseMaterialDeadlineReqDTO request = mapper.convertValue(data, UpdateCourseMaterialDeadlineReqDTO.class);
            boolean success = courseMaterialService.updateDeadline(request.getMaterialId(), context.getUserId(), request.getDeadline());
            CourseMaterialActionResponseDTO response = success
                    ? new CourseMaterialActionResponseDTO(true, "Deadline updated successfully")
                    : new CourseMaterialActionResponseDTO(false, "Failed to update deadline");
            context.getOutput().println(mapper.writeValueAsString(response));
        } catch (Exception e) {
            e.printStackTrace();
            context.getOutput().println("{\"success\":false,\"message\":\"Server error\"}");
        }
    }

    private boolean isLecturerOrHigher(String role) {
        return "Lecturer".equalsIgnoreCase(role)
                || "Admin".equalsIgnoreCase(role)
                || "Dean".equalsIgnoreCase(role);
    }
}