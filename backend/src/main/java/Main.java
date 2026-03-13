import command.repository.CommandRegistry;
import server.MultiServer;

public class Main {
    public static void main(String[] args) {
        CommandRegistry.init();

        MultiServer server = new MultiServer();
        server.start();
    }
}
