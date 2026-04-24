package command.finalMarks;

import com.fasterxml.jackson.databind.ObjectMapper;
import command.repository.ClientContext;
import command.repository.Command;
import dto.requestDto.finalMarks.FinalMarksRequestDTO;
import service.finalMarks.FinalMarksService;
import service.login.AuthService;

import java.util.HashMap;
import java.util.Map;

public class UploadFinalMarksCommand implements Command {

    private final FinalMarksService service;
    private final AuthService authService;
    private final ObjectMapper mapper = new ObjectMapper();

    public UploadFinalMarksCommand(FinalMarksService service, AuthService authService) {
        this.service = service;
        this.authService = authService;
    }

    @Override
    public void execute(Object data, ClientContext context) {

        Map<String, Object> response = new HashMap<>();

        try {
            String token = context.getToken();

            if (token == null || !authService.isTokenValid(token)) {
                response.put("success", false);
                response.put("message", "Invalid token");
                context.getOutput().println(mapper.writeValueAsString(response));
                return;
            }

            FinalMarksRequestDTO dto =
                    mapper.convertValue(data, FinalMarksRequestDTO.class);

            String msg = service.upload(dto);

            response.put("success", msg.contains("successfully"));
            response.put("message", msg);

            context.getOutput().println(mapper.writeValueAsString(response));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}