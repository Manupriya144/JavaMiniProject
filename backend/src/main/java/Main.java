package backend.src.main.java;

import command.CommandRegistry;
import server.MultiServer;

public class Main {

    public static void main(String[] args) {
        // Initialize all commands
        CommandRegistry.init();

        // Start server
        MultiServer server = new MultiServer();
        server.start(); // Make sure your MultiServer class has start() method
    }
}