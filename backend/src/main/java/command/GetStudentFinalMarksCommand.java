package command;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import service.FinalMarksService;

public class GetStudentFinalMarksCommand implements Command {

    private final FinalMarksService service;
    private final ObjectMapper mapper = new ObjectMapper();

    public GetStudentFinalMarksCommand(FinalMarksService service){
        this.service = service;
    }

    @Override
    public void execute(Object data, ClientContext context) {

        try{

            JsonNode json = (JsonNode) data;

            String studentId = json.get("studentId").asText();

            var marks = service.getStudentMarks(studentId);

            String jsonResult = mapper.writeValueAsString(marks);

            context.getOutput().println(jsonResult);

        }catch(Exception e){

            context.getOutput().println("{\"success\":false}");
        }
    }
}