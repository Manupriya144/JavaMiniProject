package command.courseMaterial;

import com.fasterxml.jackson.databind.ObjectMapper;
import command.repository.ClientContext;
import command.repository.Command;
import dto.requestDto.courseMaterial.AddCourseMaterialReqDTO;
import dto.responseDto.courseMaterial.CourseMaterialResponseDTO;
import model.CourseMaterial;
import service.courseMaterial.CourseMaterialService;

public class AddCourseMaterialCommand implements Command {

    private final CourseMaterialService courseMaterialService;
    private final ObjectMapper mapper = new ObjectMapper();

    public AddCourseMaterialCommand(CourseMaterialService courseMaterialService) {
        this.courseMaterialService = courseMaterialService;
    }

    @Override
    public void execute(Object data, ClientContext context) {
        try {
            if (!isLecturerOrHigher(context.getRole())) {
                context.getOutput().println("{\"success\":false,\"message\":\"Forbidden: insufficient role\"}");
                return;
            }

            AddCourseMaterialReqDTO request = mapper.convertValue(data, AddCourseMaterialReqDTO.class);
            String lecturerId = context.getUserId();
            if (lecturerId == null || lecturerId.isBlank()) {
                lecturerId = request.getLecturerId();
            }

            CourseMaterial material = courseMaterialService.addCourseMaterial(
                    request.getCourseId(),
                    lecturerId,
                    request.getTitle(),
                    request.getFilePath()
            );

            CourseMaterialResponseDTO response = material != null
                    ? new CourseMaterialResponseDTO(true, "Course material added successfully", material)
                    : new CourseMaterialResponseDTO(false, "Failed to add course material", null);

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