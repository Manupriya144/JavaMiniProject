package command.repository;

public interface Command {
    void execute(Object data, ClientContext clientContext );
}
