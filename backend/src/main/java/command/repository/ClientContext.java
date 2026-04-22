package command.repository;

import java.io.PrintWriter;

/**
 * here normally save client details
 * because same time more client connect to the
 * sever so save their details is the only purpose of this
 * and keep it clean architecture
 */
public class ClientContext {

    private final PrintWriter output;

    // Authenticated user info
    private String username;
    private String role;
    private String token;
    private String userId;


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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


}