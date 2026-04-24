package command.undergraduate;

import com.fasterxml.jackson.databind.ObjectMapper;
import command.repository.ClientContext;
import command.repository.Command;
import command.repository.CommandJsonUtil;
import dto.responseDto.report.MyDataResponseDTO;
import service.report.UndergraduateViewService;

public class GetMyAttendanceSummaryByCourseCommand implements Command {

    private final UndergraduateViewService service;
    private final ObjectMapper mapper = new ObjectMapper();

    public GetMyAttendanceSummaryByCourseCommand(UndergraduateViewService service) {
        this.service = service;
    }

    @Override
    public void execute(Object data, ClientContext context) {
        try {
            if (context.getUserId() == null || context.getUserId().isBlank()) {
                CommandJsonUtil.writeError(mapper, context, "Unauthorized: user context missing");
                return;
            }

            MyDataResponseDTO response = new MyDataResponseDTO(
                    true,
                    "My attendance summary retrieved.",
                    service.getMyAttendanceSummaryByCourse(context.getUserId())
            );
            context.getOutput().println(mapper.writeValueAsString(response));
        } catch (Exception e) {
            e.printStackTrace();
            CommandJsonUtil.writeError(mapper, context, "Server error");
        }
    }
}
