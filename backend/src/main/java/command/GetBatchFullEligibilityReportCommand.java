package command;

import service.EligibilityService;
import server.ClientContext;

public class GetBatchFullEligibilityReportCommand implements Command {

    private EligibilityService service;

    public GetBatchFullEligibilityReportCommand(EligibilityService service) {
        this.service = service;
    }

    @Override
    public void execute(Object args, ClientContext context) {
        context.getOutput().println("GetBatchFullEligibilityReport executed");
    }
}