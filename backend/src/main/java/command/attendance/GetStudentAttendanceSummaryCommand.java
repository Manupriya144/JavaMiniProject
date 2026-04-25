package command.attendance;

import com.fasterxml.jackson.databind.ObjectMapper;
import command.repository.ClientContext;
import command.repository.Command;
import dto.requestDto.attendance.GetStudentAttendanceSummaryRequestDTO;
import dto.responseDto.attendance.AttendanceResponseDTO;
import service.attendance.AttendanceService;

public class GetStudentAttendanceSummaryCommand implements Command {
    private final AttendanceService attendanceService;
    private final ObjectMapper mapper = new ObjectMapper();

    public GetStudentAttendanceSummaryCommand(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @Override
    public void execute(Object data, ClientContext context) {
        try {
            if (!isAllowedRole(context.getRole())) {
                context.getOutput().println("{\"success\":false,\"message\":\"Forbidden: insufficient role\"}");
                return;
            }

            GetStudentAttendanceSummaryRequestDTO request = mapper.convertValue(data, GetStudentAttendanceSummaryRequestDTO.class);
            String studentId = request.getStudentId();
            if ("Student".equalsIgnoreCase(context.getRole())) {
                // Students can only access their own attendance.
                studentId = context.getUserId();
            }
            AttendanceResponseDTO response = new AttendanceResponseDTO(
                    true,
                    "Student attendance summary loaded",
                    attendanceService.getStudentAttendanceSummary(studentId, request.getViewType())
            );
            context.getOutput().println(mapper.writeValueAsString(response));
        } catch (Exception e) {
            e.printStackTrace();
            context.getOutput().println("{\"success\":false,\"message\":\"Server error\"}");
        }
    }

    private boolean isAllowedRole(String role) {
        return "Tech_Officer".equalsIgnoreCase(role)
                || "Admin".equalsIgnoreCase(role)
                || "Dean".equalsIgnoreCase(role)
                || "Lecturer".equalsIgnoreCase(role)
                || "Student".equalsIgnoreCase(role);
    }
}
