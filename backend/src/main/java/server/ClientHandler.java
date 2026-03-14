package server;

import com.fasterxml.jackson.databind.ObjectMapper;
import command.repository.ClientContext;
import command.repository.Command;
import command.repository.CommandRegistry;
import dto.requestDto.login.RequestDTO;
import utility.JwtUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private final Socket socket;
    private final ObjectMapper mapper = new ObjectMapper();
    private final JwtUtil jwtUtil = new JwtUtil();

    ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter output = new PrintWriter(socket.getOutputStream(), true)) {

            ClientContext clientContext = new ClientContext(output);
            output.println("{\"success\":true,\"message\":\"Server ready\"}");

            String line;
            while ((line = input.readLine()) != null) {

                try {
                    RequestDTO request = mapper.readValue(line, RequestDTO.class);
                    String commandName = request.getCommand();

                    // LOGIN does NOT require token
                    if (!commandName.equalsIgnoreCase("LOGIN")) {

                        String token = request.getToken();

                        if (token == null || !jwtUtil.validateToken(token)) {
                            clientContext.getOutput().println(
                                    "{\"success\":false,\"message\":\"Unauthorized\"}"
                            );
                            continue;
                        }

                        String username = jwtUtil.getUsernameFromToken(token);
                        String role = jwtUtil.getRoleFromToken(token);
                        clientContext.setUsername(username);
                        clientContext.setRole(role);
                    }

                    Command command = CommandRegistry.getCommand(commandName);

                    if (command != null) {
                        command.execute(request.getData(), clientContext);
                    } else {
                        clientContext.getOutput().println(
                                "{\"success\":false,\"message\":\"Unknown command\"}"
                        );
                    }

                } catch (Exception e) {
                    clientContext.getOutput().println(
                            "{\"success\":false,\"message\":\"Invalid JSON\"}"
                    );
                }
            }

        } catch (IOException e) {
            System.err.println("Client disconnected: " + e.getMessage());
        }
    }
}