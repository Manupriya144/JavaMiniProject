package command.undergraduate;

import com.fasterxml.jackson.databind.ObjectMapper;
import command.repository.ClientContext;
import command.repository.Command;
import command.repository.CommandJsonUtil;
import dto.responseDto.report.MyDataResponseDTO;
import service.report.UndergraduateViewService;
import java.util.Map;

public class GetCourseMaterialsCommand implements Command {

    private final UndergraduateViewService service;
    private final ObjectMapper mapper = new ObjectMapper();

    public GetCourseMaterialsCommand(UndergraduateViewService service) {
        this.service = service;
    }

    @Override
    public void execute(Object data, ClientContext context) {
        try {
            if (context.getUserId() == null || context.getUserId().isBlank()) {
                CommandJsonUtil.writeError(mapper, context, "Unauthorized: user context missing");
                return;
            }

            Map<String, String> requestData = mapper.convertValue(data, Map.class);
            String courseId = requestData.get("courseId");

            if (courseId == null || courseId.isBlank()) {
                CommandJsonUtil.writeError(mapper, context, "courseId is required");
                return;
            }

            MyDataResponseDTO response = new MyDataResponseDTO(
                    true,
                    "Course materials retrieved.",
                    service.getCourseMaterials(courseId)
            );
            context.getOutput().println(mapper.writeValueAsString(response));
        } catch (Exception e) {
            e.printStackTrace();
            CommandJsonUtil.writeError(mapper, context, "Server error");
        }
    }
}
