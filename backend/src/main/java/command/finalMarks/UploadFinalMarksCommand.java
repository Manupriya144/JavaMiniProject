package command.finalMarks;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.FinalMarks;
import service.finalMarks.FinalMarksService;
import command.repository.ClientContext;
import command.repository.Command;
import dto.requestDto.finalMarks.FinalMarksRequestDTO;
import dto.responseDto.finalMarks.FinalMarksResponseDTO;

public class UploadFinalMarksCommand implements Command {

    private final FinalMarksService service;
    private final ObjectMapper mapper = new ObjectMapper();

    public UploadFinalMarksCommand(FinalMarksService service) {
        this.service = service;
    }

    @Override
    public void execute(Object data, ClientContext context) {
        try {
            FinalMarksRequestDTO dto = mapper.convertValue(data, FinalMarksRequestDTO.class);
            FinalMarks marks = new FinalMarks(dto.getStudentId(), dto.getCourseId(), dto.getAcademicYear(), dto.getSemester(), dto.getMarks());

            boolean success = service.uploadFinalMarks(marks);
            String message = success ? "Final marks uploaded successfully." : "Failed to upload final marks.";
            FinalMarksResponseDTO response = new FinalMarksResponseDTO(success, message, marks);

            context.getOutput().println(mapper.writeValueAsString(response));
        } catch (Exception e) {
            e.printStackTrace();
            context.getOutput().println("{\"success\":false,\"message\":\"Server error\"}");
        }
    }
}

