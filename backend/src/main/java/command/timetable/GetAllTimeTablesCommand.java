package command.timetable;

import com.fasterxml.jackson.databind.ObjectMapper;
import command.repository.ClientContext;
import command.repository.Command;

import dto.responseDto.timetable.TimeTableResponseDTO;
import service.login.AuthService;
import service.timetable.TimeTableService;

import java.util.List;

public class GetAllTimeTablesCommand implements Command {

    private final TimeTableService timeTableService;
    private final AuthService authService;
    private final ObjectMapper mapper = new ObjectMapper();

    public GetAllTimeTablesCommand(TimeTableService timeTableService, AuthService authService) {
        this.timeTableService = timeTableService;
        this.authService = authService;
    }

    @Override
    public void execute(Object data, ClientContext context) {
        try {
            String token = context.getToken();

            if (token == null || !authService.isTokenValid(token)) {
                context.getOutput().println("{\"success\":false,\"message\":\"Unauthorized\"}");
                return;
            }
            List<TimeTableResponseDTO> timeTables = timeTableService.getAllTimeTableResponses();

            context.getOutput().println(mapper.writeValueAsString(timeTables));

        } catch (Exception e) {
            e.printStackTrace();
            context.getOutput().println("{\"success\":false,\"message\":\"Server error\"}");
        }
    }
}