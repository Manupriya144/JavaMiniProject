package command;

import service.FinalMarksService;
import server.ClientContext;

public class GetBatchFinalMarksCommand implements Command {

    private FinalMarksService service;

    public GetBatchFinalMarksCommand(FinalMarksService service) {
        this.service = service;
    }

    @Override
    public void execute(Object args, ClientContext context) {
        context.getOutput().println("GetBatchFinalMarks executed");
    }
}