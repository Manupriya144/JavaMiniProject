package command;

import com.fasterxml.jackson.databind.JsonNode;
import service.FinalMarksService;

public class UpdateFinalMarksCommand implements Command {

    private final FinalMarksService service;

    public UpdateFinalMarksCommand(FinalMarksService service){
        this.service = service;
    }

    @Override
    public void execute(Object data, ClientContext context) {

        try{

            JsonNode json = (JsonNode) data;

            String studentId = json.get("studentId").asText();
            String courseId = json.get("courseId").asText();
            int year = json.get("academicYear").asInt();
            String semester = json.get("semester").asText();
            double marks = json.get("marks").asDouble();

            boolean result = service.updateFinalMarks(studentId,courseId,year,semester,marks);

            context.getOutput().println(
            "{\"success\":"+result+"}");

        }catch(Exception e){

            context.getOutput().println(
            "{\"success\":false}");
        }
    }
}