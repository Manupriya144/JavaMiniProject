package command.attendance;

import com.fasterxml.jackson.databind.ObjectMapper;
import command.repository.ClientContext;
import command.repository.Command;
import dto.requestDto.attendance.GetAttendanceByIdRequestDTO;
import dto.responseDto.attendance.AttendanceResponseDTO;
import model.Attendance;
import service.attendance.AttendanceService;

public class GetAttendanceByIdCommand implements Command {
    private final AttendanceService attendanceService;
    private final ObjectMapper mapper = new ObjectMapper();

    public GetAttendanceByIdCommand(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @Override
    public void execute(Object data, ClientContext context) {
        try {
            if (!isAllowedRole(context.getRole())) {
                context.getOutput().println("{\"success\":false,\"message\":\"Forbidden: insufficient role\"}");
                return;
            }

            GetAttendanceByIdRequestDTO request = mapper.convertValue(data, GetAttendanceByIdRequestDTO.class);
            Attendance attendance = attendanceService.getAttendanceById(request.getAttendanceId());

            AttendanceResponseDTO response = attendance != null
                    ? new AttendanceResponseDTO(true, "Attendance found", attendance)
                    : new AttendanceResponseDTO(false, "Attendance not found", null);

            context.getOutput().println(mapper.writeValueAsString(response));
        } catch (Exception e) {
            e.printStackTrace();
            context.getOutput().println("{\"success\":false,\"message\":\"Server error\"}");
        }
    }

    private boolean isAllowedRole(String role) {
        return "Tech_Officer".equalsIgnoreCase(role)
                || "Admin".equalsIgnoreCase(role)
                || "Dean".equalsIgnoreCase(role);
    }
}
