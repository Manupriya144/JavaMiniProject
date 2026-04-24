package command.courseMeterial;

import com.fasterxml.jackson.databind.ObjectMapper;
import command.repository.ClientContext;
import command.repository.Command;
import dto.requestDto.courseMeterial.AddCourseMaterialReqDTO;
import model.CourseMaterial;
import service.courseMeterial.CourseMaterialService;
import service.login.AuthService;

import java.util.HashMap;
import java.util.Map;

public class AddCourseMaterialCommand implements Command {

    private final ObjectMapper mapper = new ObjectMapper();
    private final CourseMaterialService courseMaterialService;
    private final AuthService authService;

    public AddCourseMaterialCommand(CourseMaterialService courseMaterialService, AuthService authService) {
        this.authService = authService;
        this.courseMaterialService = courseMaterialService;
    }

    @Override
    public void execute(Object data, ClientContext context) {
        Map<String, Object> response = new HashMap<>();

        try {
            String token = context.getToken();

            if (token == null || !authService.isTokenValid(token)) {
                response.put("success", false);
                response.put("message", "Invalid or expired token.");
                context.getOutput().println(mapper.writeValueAsString(response));
                return;
            }

            if (!"Lecturer".equalsIgnoreCase(context.getRole())) {
                response.put("success", false);
                response.put("message", "Only lecturers can add course materials.");
                context.getOutput().println(mapper.writeValueAsString(response));
                return;
            }

            AddCourseMaterialReqDTO reqDTO =
                    mapper.convertValue(data, AddCourseMaterialReqDTO.class);

            if (reqDTO.getCourseId() == null || reqDTO.getCourseId().isBlank()) {
                response.put("success", false);
                response.put("message", "Course ID is required.");
                context.getOutput().println(mapper.writeValueAsString(response));
                return;
            }

            if (reqDTO.getTitle() == null || reqDTO.getTitle().isBlank()) {
                response.put("success", false);
                response.put("message", "Material title is required.");
                context.getOutput().println(mapper.writeValueAsString(response));
                return;
            }

            if (reqDTO.getFilePath() == null || reqDTO.getFilePath().isBlank()) {
                response.put("success", false);
                response.put("message", "File path is required.");
                context.getOutput().println(mapper.writeValueAsString(response));
                return;
            }

            CourseMaterial material =
                    courseMaterialService.addMaterial(reqDTO, context.getUserId());

            if (material != null) {
                response.put("success", true);
                response.put("message", "Course material added successfully.");
                response.put("material", material);
            } else {
                response.put("success", false);
                response.put("message", "Failed to add course material.");
            }

            context.getOutput().println(mapper.writeValueAsString(response));

        } catch (Exception e) {
            e.printStackTrace();

            try {
                response.put("success", false);
                response.put("message", "Server error while adding course material.");
                context.getOutput().println(mapper.writeValueAsString(response));
            } catch (Exception ignored) {
            }
        }
    }
}