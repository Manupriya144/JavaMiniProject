package command.ca;

import com.fasterxml.jackson.databind.ObjectMapper;
import command.repository.ClientContext;
import command.repository.Command;
import dto.requestDto.ca.GetCourseCAReferenceRequestDTO;
import dto.responseDto.ca.CAResponseDTO;
import service.ca.CAMarkService;

import java.util.Map;

public class GetCourseCAReferenceCommand implements Command {
    private final CAMarkService caMarkService;
    private final ObjectMapper mapper = new ObjectMapper();

    public GetCourseCAReferenceCommand(CAMarkService caMarkService) {
        this.caMarkService = caMarkService;
    }

    @Override
    public void execute(Object data, ClientContext context) {
        try {
            if (!isAllowedRole(context.getRole())) {
                context.getOutput().println("{\"success\":false,\"message\":\"Forbidden: insufficient role\"}");
                return;
            }

            GetCourseCAReferenceRequestDTO request =
                    mapper.convertValue(data, GetCourseCAReferenceRequestDTO.class);

            String courseId = request.getCourseId() == null ? null : request.getCourseId().trim();
            if (courseId == null || courseId.isBlank()) {
                context.getOutput().println(mapper.writeValueAsString(
                        new CAResponseDTO(false, "Course ID is required", null)
                ));
                return;
            }

            if ("Lecturer".equalsIgnoreCase(context.getRole())
                    && !caMarkService.canLecturerManageCourse(context.getUserId(), courseId)) {
                context.getOutput().println(mapper.writeValueAsString(
                        new CAResponseDTO(false, "You can only load CA details for your assigned courses", null)
                ));
                return;
            }

            Map<String, Object> referenceData = caMarkService.getCourseCAReference(courseId);
            CAResponseDTO response = new CAResponseDTO(
                    true,
                    "Course CA reference data loaded",
                    referenceData
            );
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
