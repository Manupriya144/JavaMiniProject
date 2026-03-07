package command;

import com.fasterxml.jackson.databind.JsonNode;
import service.FinalMarksService;

public class UploadFinalMarksCommand implements Command {

    private final FinalMarksService service;

    public UploadFinalMarksCommand(FinalMarksService service){
        this.service = service;
    }

    @Override
    public void execute(Object data, ClientContext context) {

        try{

            JsonNode json = (JsonNode) data;

            String studentId = json.get("studentId").asText();
            String courseId = json.get("courseId").asText();
            int year = json.get("academicYear").asInt();
            int level = json.get("academicLevel").asInt();
            String semester = json.get("semester").asText();
            double marks = json.get("marks").asDouble();

            boolean result = service.uploadFinalMarks(studentId,courseId,year,level,semester,marks);

            if(result){

                context.getOutput().println(
                "{\"success\":true,\"message\":\"Final marks uploaded\"}");

            }else{

                context.getOutput().println(
                "{\"success\":false,\"message\":\"Upload failed\"}");
            }

        }catch(Exception e){

            context.getOutput().println(
            "{\"success\":false,\"message\":\"Server error\"}");
        }
    }
}