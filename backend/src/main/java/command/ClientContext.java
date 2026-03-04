package command;

import java.io.PrintWriter;

public class ClientContext {

    private final PrintWriter output;

    // Authenticated user info
    private String username;
    private String role;
    private String token;

    public ClientContext(PrintWriter output) {
        this.output = output;
    }

    public PrintWriter getOutput() {
        return output;
    }


    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}