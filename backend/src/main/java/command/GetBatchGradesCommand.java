package command;

import service.GradeService;
import server.ClientContext;

public class GetBatchGradesCommand implements Command {

    private GradeService service;

    public GetBatchGradesCommand(GradeService service) {
        this.service = service;
    }

    @Override
    public void execute(Object args, ClientContext context) {
        context.getOutput().println("GetBatchGrades executed");
    }
}