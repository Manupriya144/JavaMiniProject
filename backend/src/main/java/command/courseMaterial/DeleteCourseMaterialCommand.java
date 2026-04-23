package command.courseMaterial;

import com.fasterxml.jackson.databind.ObjectMapper;
import command.repository.ClientContext;
import command.repository.Command;
import dto.requestDto.courseMaterial.DeleteCourseMaterialReqDTO;
import dto.responseDto.courseMaterial.CourseMaterialActionResponseDTO;
import service.courseMaterial.CourseMaterialService;

public class DeleteCourseMaterialCommand implements Command {

    private final CourseMaterialService courseMaterialService;
    private final ObjectMapper mapper = new ObjectMapper();

    public DeleteCourseMaterialCommand(CourseMaterialService courseMaterialService) {
        this.courseMaterialService = courseMaterialService;
    }

    @Override
    public void execute(Object data, ClientContext context) {
        try {
            if (!isLecturerOrHigher(context.getRole())) {
                context.getOutput().println("{\"success\":false,\"message\":\"Forbidden: insufficient role\"}");
                return;
            }

            DeleteCourseMaterialReqDTO request = mapper.convertValue(data, DeleteCourseMaterialReqDTO.class);
            boolean success = courseMaterialService.deleteCourseMaterial(request.getMaterialId(), context.getUserId());
            CourseMaterialActionResponseDTO response = success
                    ? new CourseMaterialActionResponseDTO(true, "Course material removed successfully")
                    : new CourseMaterialActionResponseDTO(false, "Failed to remove course material");
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