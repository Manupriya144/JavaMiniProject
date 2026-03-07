package command;

import service.EligibilityService;
import server.ClientContext;

public class CheckFullEligibilityCommand implements Command {

    private EligibilityService service;

    public CheckFullEligibilityCommand(EligibilityService service) {
        this.service = service;
    }

    @Override
    public void execute(Object args, ClientContext context) {
        context.getOutput().println("CheckFullEligibility executed");
    }
}