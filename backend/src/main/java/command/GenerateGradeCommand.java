package command;

import service.GradeService;
import server.ClientContext;

public class GenerateGradeCommand implements Command {

    private GradeService service;

    public GenerateGradeCommand(GradeService service) {
        this.service = service;
    }

    @Override
    public void execute(Object args, ClientContext context) {
        context.getOutput().println("GenerateGrade executed");
    }
}