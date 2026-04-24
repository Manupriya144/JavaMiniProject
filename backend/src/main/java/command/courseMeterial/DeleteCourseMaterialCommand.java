package command.courseMeterial;

import com.fasterxml.jackson.databind.ObjectMapper;
import command.repository.ClientContext;
import command.repository.Command;
import service.courseMeterial.CourseMaterialService;
import service.login.AuthService;

import java.util.HashMap;
import java.util.Map;

public class DeleteCourseMaterialCommand implements Command {

    private final ObjectMapper mapper = new ObjectMapper();
    private final CourseMaterialService courseMaterialService;
    private final AuthService authService;

    public DeleteCourseMaterialCommand(CourseMaterialService courseMaterialService, AuthService authService) {
        this.courseMaterialService = courseMaterialService;
        this.authService = authService;
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
                response.put("message", "Only lecturers can delete course materials.");
                context.getOutput().println(mapper.writeValueAsString(response));
                return;
            }

            Map<?, ?> map = mapper.convertValue(data, Map.class);

            Object materialIdObj = map.get("materialId");

            if (materialIdObj == null) {
                response.put("success", false);
                response.put("message", "Material ID is required.");
                context.getOutput().println(mapper.writeValueAsString(response));
                return;
            }

            int materialId = Integer.parseInt(materialIdObj.toString());

            boolean deleted = courseMaterialService.deleteMaterial(
                    materialId,
                    context.getUserId()
            );

            if (deleted) {
                response.put("success", true);
                response.put("message", "Material deleted successfully.");
            } else {
                response.put("success", false);
                response.put("message", "Failed to delete material.");
            }

            context.getOutput().println(mapper.writeValueAsString(response));

        } catch (Exception e) {
            e.printStackTrace();

            try {
                response.put("success", false);
                response.put("message", "Server error while deleting material.");
                context.getOutput().println(mapper.writeValueAsString(response));
            } catch (Exception ignored) {
            }
        }
    }
}