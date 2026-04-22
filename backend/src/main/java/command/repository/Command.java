package command.repository;


/**
 * here used command interface for command
 * design pattern everyone can work independently on there
 * work, and it makes easy to manage large amount of request and response
 */
public interface Command {
    void execute(Object data, ClientContext clientContext );
}
