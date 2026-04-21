package command.ca;

import com.fasterxml.jackson.databind.ObjectMapper;
import command.repository.ClientContext;
import command.repository.Command;
import dto.requestDto.ca.GetStudentCAMarksRequestDTO;
import dto.responseDto.ca.CAResponseDTO;
import service.ca.CAMarkService;

public class GetStudentCAMarksCommand implements Command {
    private final CAMarkService caMarkService;
    private final ObjectMapper mapper = new ObjectMapper();

    public GetStudentCAMarksCommand(CAMarkService caMarkService) {
        this.caMarkService = caMarkService;
    }

    @Override
    public void execute(Object data, ClientContext context) {
        try {
            if (!isAllowedRole(context.getRole())) {
                context.getOutput().println("{\"success\":false,\"message\":\"Forbidden: insufficient role\"}");
                return;
            }
            GetStudentCAMarksRequestDTO request = mapper.convertValue(data, GetStudentCAMarksRequestDTO.class);
            CAResponseDTO response = new CAResponseDTO(
                    true,
                    "Student CA marks loaded",
                    caMarkService.getStudentCAMarks(request.getStudentId(), request.getCourseId())
            );
            context.getOutput().println(mapper.writeValueAsString(response));
        } catch (Exception e) {
            e.printStackTrace();
            context.getOutput().println("{\"success\":false,\"message\":\"Server error\"}");
        }
    }

    private boolean isAllowedRole(String role) {
        return "Student".equalsIgnoreCase(role)
                || "Tech_Officer".equalsIgnoreCase(role)
                || "Lecturer".equalsIgnoreCase(role)
                || "Admin".equalsIgnoreCase(role)
                || "Dean".equalsIgnoreCase(role);
    }
}
