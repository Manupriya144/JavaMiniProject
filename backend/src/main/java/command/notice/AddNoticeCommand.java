package command.notice;

import com.fasterxml.jackson.databind.ObjectMapper;
import command.repository.ClientContext;
import command.repository.Command;
import dto.requestDto.notice.AddNoticeReqDTO;
import model.Notice;
import service.login.AuthService;
import service.notice.AddNoticeService;

public class AddNoticeCommand implements Command {

    private final AddNoticeService addNoticeService;
    private final AuthService authService;
    private final ObjectMapper mapper = new ObjectMapper();

    public AddNoticeCommand(AddNoticeService addNoticeService, AuthService authService) {
        this.addNoticeService = addNoticeService;
        this.authService = authService;
    }

    @Override
    public void execute(Object data, ClientContext context) {
        try {
            String token = context.getToken();

            if (token == null || !authService.isTokenValid(token)) {
                context.getOutput().println("{\"success\":false,\"message\":\"Unauthorized\"}");
                return;
            }

            AddNoticeReqDTO dto = mapper.convertValue(data, AddNoticeReqDTO.class);


            String userId = authService.getUserIdFromToken(token);
            System.out.println(authService.getUserIdFromToken(token));

            if (userId == null) {
                context.getOutput().println("{\"success\":false,\"message\":\"Invalid token user\"}");
                return;
            }

            dto.setCreated_by(userId);

            Notice createdNotice = addNoticeService.addNotice(dto);

            if (createdNotice != null) {
                context.getOutput().println(
                        "{\"success\":true,\"message\":\"Notice added successfully\",\"notice_id\":"
                                + createdNotice.getNotice_id() + "}"
                );
            } else {
                context.getOutput().println("{\"success\":false,\"message\":\"Notice creation failed\"}");
            }

        } catch (Exception e) {
            e.printStackTrace();
            context.getOutput().println("{\"success\":false,\"message\":\"Server error\"}");
        }
    }
}