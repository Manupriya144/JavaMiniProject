package command.attendance;

import com.fasterxml.jackson.databind.ObjectMapper;
import command.repository.ClientContext;
import command.repository.Command;
import dto.requestDto.medical.GetMedicalEligibleCoursesRequestDTO;
import dto.responseDto.attendance.AttendanceResponseDTO;
import service.attendance.AttendanceService;

import java.util.List;

public class GetMedicalEligibleCoursesCommand implements Command {
    private final AttendanceService attendanceService;
    private final ObjectMapper mapper = new ObjectMapper();

    public GetMedicalEligibleCoursesCommand(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @Override
    public void execute(Object data, ClientContext context) {
        try {
            if (!isAllowedRole(context.getRole())) {
                context.getOutput().println("{\"success\":false,\"message\":\"Forbidden: insufficient role\"}");
                return;
            }
            GetMedicalEligibleCoursesRequestDTO request = mapper.convertValue(data, GetMedicalEligibleCoursesRequestDTO.class);
            if (request.getStudentId() == null || request.getStudentId().isBlank()) {
                context.getOutput().println("{\"success\":false,\"message\":\"student_id is required\"}");
                return;
            }
            List<?> courses = attendanceService.getMedicalEligibleCourseIds(request.getStudentId());
            AttendanceResponseDTO response = new AttendanceResponseDTO(true, "Courses loaded", courses);
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
