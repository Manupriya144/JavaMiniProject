package service;

import dao.UserDAO;
import dto.responseDto.LogoutResponseDTO;
import model.User;
import io.jsonwebtoken.Jwts;
import utility.JwtUtil;
import utility.TokenBlacklist;

import java.util.Date;

public class AuthService {

    private final UserDAO userDAO;

    public AuthService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    // Authenticate user and return JWT token
    public AuthResult authenticate(String username, String password) {
        User user = userDAO.findByUsernameAndPassword(username, password);
        if (user != null) {
            String token = generateToken(user);  // token generated here
            return new AuthResult(true, user.getUsername(), user.getRole(), "Login successful", token);
        }
        return new AuthResult(false, null, null, "Invalid username or password", null);
    }

    // Generate JWT token (secure HS256)
    private String generateToken(User user) {
        long jwtExpirationMs = 24 * 60 * 60 * 1000; // 24 hours

        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("role", user.getRole())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(utility.JwtUtil.getSecretKey())
                .compact();
    }

    public LogoutResponseDTO logout(String token) {
        JwtUtil jwtUtil = new JwtUtil();
        if (!jwtUtil.validateToken(token)) {
            return new LogoutResponseDTO(false, "Invalid or expired token");
        }
        TokenBlacklist.blacklist(token);

        return new LogoutResponseDTO(true, "Logout successful");
    }

    public boolean isTokenValid(String token) {
        JwtUtil jwtUtil = new JwtUtil();
        return jwtUtil.validateToken(token) && !TokenBlacklist.isBlacklisted(token);
    }

    // AuthResult wrapper
    public static class AuthResult {
        private final boolean success;
        private final String username;
        private final String role;
        private final String message;
        private final String token;

        public AuthResult(boolean success, String username, String role, String message, String token) {
            this.success = success;
            this.username = username;
            this.role = role;
            this.message = message;
            this.token = token;
        }

        public boolean isSuccess() { return success; }
        public String getUsername() { return username; }
        public String getRole() { return role; }
        public String getMessage() { return message; }
        public String getToken() { return token; }
    }
}