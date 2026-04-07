package command.attendance;

import com.fasterxml.jackson.databind.ObjectMapper;
import command.repository.ClientContext;
import command.repository.Command;
import dto.requestDto.attendance.GetBatchAttendanceEligibilityReportRequestDTO;
import dto.responseDto.attendance.AttendanceResponseDTO;
import service.attendance.AttendanceService;

public class GetBatchAttendanceEligibilityReportCommand implements Command {
    private final AttendanceService attendanceService;
    private final ObjectMapper mapper = new ObjectMapper();

    public GetBatchAttendanceEligibilityReportCommand(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @Override
    public void execute(Object data, ClientContext context) {
        try {
            if (!isAllowedRole(context.getRole())) {
                context.getOutput().println("{\"success\":false,\"message\":\"Forbidden: insufficient role\"}");
                return;
            }

            GetBatchAttendanceEligibilityReportRequestDTO request = mapper.convertValue(data, GetBatchAttendanceEligibilityReportRequestDTO.class);
            AttendanceResponseDTO response = new AttendanceResponseDTO(
                    true,
                    "Batch attendance eligibility report loaded",
                    attendanceService.getBatchAttendanceEligibilityReport(request.getBatch(), request.getViewType())
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
                || "Lecturer".equalsIgnoreCase(role);
    }
}
