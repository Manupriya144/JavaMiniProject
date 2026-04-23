package command.courseMaterial;

import com.fasterxml.jackson.databind.ObjectMapper;
import command.repository.ClientContext;
import command.repository.Command;
import dto.responseDto.courseMaterial.CourseMaterialListResponseDTO;
import service.courseMaterial.CourseMaterialService;

public class GetCourseMaterialsCommand implements Command {

    private final CourseMaterialService courseMaterialService;
    private final ObjectMapper mapper = new ObjectMapper();

    public GetCourseMaterialsCommand(CourseMaterialService courseMaterialService) {
        this.courseMaterialService = courseMaterialService;
    }

    @Override
    public void execute(Object data, ClientContext context) {
        try {
            if (!isLecturerOrHigher(context.getRole())) {
                context.getOutput().println("{\"success\":false,\"message\":\"Forbidden: insufficient role\"}");
                return;
            }

            String courseId = ((java.util.Map<String, Object>) data).get("courseId").toString();
            String lecturerId = context.getUserId();

            CourseMaterialListResponseDTO response = new CourseMaterialListResponseDTO(
                    true,
                    "Course materials loaded",
                    courseMaterialService.getCourseMaterials(courseId, lecturerId)
            );
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