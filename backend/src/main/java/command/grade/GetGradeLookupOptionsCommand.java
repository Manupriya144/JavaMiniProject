package command.grade;

import com.fasterxml.jackson.databind.ObjectMapper;
import command.repository.ClientContext;
import command.repository.Command;
import service.grade.GradeService;

import java.util.LinkedHashMap;
import java.util.Map;

public class GetGradeLookupOptionsCommand implements Command {

    private final GradeService service;
    private final ObjectMapper mapper = new ObjectMapper();

    public GetGradeLookupOptionsCommand(GradeService service) {
        this.service = service;
    }

    @Override
    public void execute(Object data, ClientContext context) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> payload = data == null ? new LinkedHashMap<>() : (Map<String, Object>) data;

            String studentId = payload.get("studentId") == null ? null : String.valueOf(payload.get("studentId"));
            String courseId = payload.get("courseId") == null ? null : String.valueOf(payload.get("courseId"));

            Integer academicYear = null;
            if (payload.get("academicYear") instanceof Number) {
                academicYear = ((Number) payload.get("academicYear")).intValue();
            }

            Integer semester = null;
            if (payload.get("semester") instanceof Number) {
                semester = ((Number) payload.get("semester")).intValue();
            }

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("success", true);
            response.put("message", "Grade lookup options retrieved.");

            Map<String, Object> options = new LinkedHashMap<>();
            options.put("studentIds", service.getDistinctStudentIds());
            options.put("courseIds", studentId == null || studentId.isBlank() ? java.util.Collections.emptyList() : service.getDistinctCourseIds(studentId));
                options.put("academicYears", (studentId == null || studentId.isBlank() || courseId == null || courseId.isBlank())
                    ? service.getDistinctAcademicYears()
                    : service.getDistinctAcademicYears(studentId, courseId));
                options.put("semesters", (studentId == null || studentId.isBlank() || courseId == null || courseId.isBlank())
                    ? service.getDistinctSemesters()
                    : service.getDistinctSemesters(studentId, courseId, academicYear));
            options.put("grades", service.getDistinctGrades(studentId, courseId, academicYear, semester));

            response.put("data", options);
            context.getOutput().println(mapper.writeValueAsString(response));
        } catch (Exception e) {
            e.printStackTrace();
            context.getOutput().println("{\"success\":false,\"message\":\"Server error\"}");
        }
    }
}
