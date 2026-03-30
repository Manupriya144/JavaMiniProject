package command.attendance;

import com.fasterxml.jackson.databind.ObjectMapper;
import command.repository.ClientContext;
import command.repository.Command;
import dto.requestDto.attendance.DeleteAttendanceRequestDTO;
import dto.responseDto.attendance.AttendanceResponseDTO;
import service.attendance.AttendanceService;

public class DeleteAttendanceCommand implements Command {
    private final AttendanceService attendanceService;
    private final ObjectMapper mapper = new ObjectMapper();

    public DeleteAttendanceCommand(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @Override
    public void execute(Object data, ClientContext context) {
        try {
            if (!isAllowedRole(context.getRole())) {
                context.getOutput().println("{\"success\":false,\"message\":\"Forbidden: insufficient role\"}");
                return;
            }

            DeleteAttendanceRequestDTO request = mapper.convertValue(data, DeleteAttendanceRequestDTO.class);
            boolean deleted = attendanceService.deleteAttendance(request.getAttendanceId());

            AttendanceResponseDTO response = deleted
                    ? new AttendanceResponseDTO(true, "Attendance deleted successfully", null)
                    : new AttendanceResponseDTO(false, "Failed to delete attendance", null);

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
