package command.attendance;

import com.fasterxml.jackson.databind.ObjectMapper;
import command.repository.ClientContext;
import command.repository.Command;
import dto.requestDto.attendance.AddAttendanceRequestDTO;
import dto.responseDto.attendance.AttendanceResponseDTO;
import model.Attendance;
import service.attendance.AttendanceService;

public class AddAttendanceCommand implements Command {
    private final AttendanceService attendanceService;
    private final ObjectMapper mapper = new ObjectMapper();

    public AddAttendanceCommand(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @Override
    public void execute(Object data, ClientContext context) {
        try {
            if (!isAllowedRole(context.getRole())) {
                context.getOutput().println("{\"success\":false,\"message\":\"Forbidden: insufficient role\"}");
                return;
            }

            AddAttendanceRequestDTO request = mapper.convertValue(data, AddAttendanceRequestDTO.class);
            Attendance attendance = attendanceService.addAttendance(
                    request.getStudentId(),
                    request.getSessionId(),
                    request.getStatus(),
                    request.getHoursAttended()
            );

            AttendanceResponseDTO response = attendance != null
                    ? new AttendanceResponseDTO(true, "Attendance added successfully", attendance)
                    : new AttendanceResponseDTO(false, "Validation failed: check student_id, session_id and status (Present/Absent)", null);

            context.getOutput().println(mapper.writeValueAsString(response));
        } catch (RuntimeException e) {
            e.printStackTrace();
            AttendanceResponseDTO response = new AttendanceResponseDTO(false, e.getMessage(), null);
            try {
                context.getOutput().println(mapper.writeValueAsString(response));
            } catch (Exception ignored) {
                context.getOutput().println("{\"success\":false,\"message\":\"Server error\"}");
            }
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
