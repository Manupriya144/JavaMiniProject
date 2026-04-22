package command.notice;

import com.fasterxml.jackson.databind.ObjectMapper;
import command.repository.ClientContext;
import command.repository.Command;
import dto.NoticeResponseDTO;
import service.notice.NoticeService;
import service.login.AuthService;

import java.util.List;

public class GetAllNoticeCommand implements Command {

    private final NoticeService noticeService;
    private final AuthService authService;
    private final ObjectMapper mapper = new ObjectMapper();

    public GetAllNoticeCommand(NoticeService noticeService, AuthService authService) {
        this.noticeService = noticeService;
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

            List<NoticeResponseDTO> notices = noticeService.getAllNotices();

            context.getOutput().println(mapper.writeValueAsString(notices));

        } catch (Exception e) {
            e.printStackTrace();
            context.getOutput().println("{\"success\":false,\"message\":\"Server error\"}");
        }
    }
}