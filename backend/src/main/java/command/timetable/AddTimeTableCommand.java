package command.timetable;

import com.fasterxml.jackson.databind.ObjectMapper;
import command.repository.ClientContext;
import command.repository.Command;
import dto.requestDto.timetable.AddTimeTableReqDTO;
import model.TimeTable;
import service.login.AuthService;
import service.timetable.TimeTableService;

public class AddTimeTableCommand implements Command {
    private final TimeTableService timeTableService;
    private final AuthService authService;
    private final ObjectMapper mapper = new ObjectMapper();

   public AddTimeTableCommand(TimeTableService timeTableService,
                              AuthService authService){
       this.timeTableService = timeTableService;
       this.authService = authService;

   }


    @Override
    public void execute(Object data, ClientContext context) {
        try{
            String token = context.getToken();

            if(token == null || authService.isTokenValid(token)){
                context.getOutput().println("{\"success\":false,\"message\":\"Unauthorized\"}");
                return;
            }

            AddTimeTableReqDTO dto = mapper.convertValue(data,AddTimeTableReqDTO.class);

            TimeTable createdTimeTable = timeTableService.addTimeTable(dto);

            if (createdTimeTable != null) {
                context.getOutput().println(
                        "{\"success\":true,\"message\":\"Notice added successfully\",\"notice_id\":"
                                + createdTimeTable.getTimetableId() + "}"
                );
            } else {
                context.getOutput().println("{\"success\":false,\"message\":\"Notice creation failed\"}");
            }

        }catch (Exception e){
            e.printStackTrace();
            context.getOutput().println("{\"success\":false,\"message\":\"Server error\"}");
        }
    }
}
