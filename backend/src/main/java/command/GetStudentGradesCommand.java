package command;

import service.GradeService;
import server.ClientContext;

public class GetStudentGradesCommand implements Command {

    private GradeService service;

    public GetStudentGradesCommand(GradeService service) {
        this.service = service;
    }

    @Override
    public void execute(Object args, ClientContext context) {
        context.getOutput().println("GetStudentGrades executed");
    }
}