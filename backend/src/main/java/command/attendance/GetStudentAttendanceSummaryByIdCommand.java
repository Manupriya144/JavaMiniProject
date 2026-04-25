package command.attendance;

import com.fasterxml.jackson.databind.ObjectMapper;
import command.repository.ClientContext;
import command.repository.Command;
import dto.responseDto.attendance.StudentAttendanceSummaryDTO;
import service.attendance.AttendanceService;
import service.login.AuthService;

import java.util.List;
import java.util.Map;

public class GetStudentAttendanceSummaryByIdCommand implements Command {
    private final AttendanceService attendanceService;
    private final AuthService authService;
    private final ObjectMapper mapper = new ObjectMapper();

    public GetStudentAttendanceSummaryByIdCommand(AttendanceService attendanceService, AuthService authService) {
        this.attendanceService = attendanceService;
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

            String studentId = context.getUserId();

            List<StudentAttendanceSummaryDTO> list =
                    attendanceService.GetStudentAttendanceSummaryById(studentId);

            Map<String, Object> response = Map.of(
                    "success", true,
                    "message", "Student attendance loaded",
                    "attendance", list
            );

            context.getOutput().println(mapper.writeValueAsString(response));

        } catch (Exception e) {
            e.printStackTrace();
            context.getOutput().println("{\"success\":false,\"message\":\"Server error\"}");
        }
    }
}
