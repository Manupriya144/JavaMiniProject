package dto.responseDto.login;

public class LoginResponseDTO {

    private final boolean success;
    private final String username;
    private final String message;
    private final String role;
    private final String token;
    private final String userId;

    // Private constructor
    private LoginResponseDTO(Builder builder) {
        this.success = builder.success;
        this.username = builder.username;
        this.message = builder.message;
        this.role = builder.role;
        this.token = builder.token;
        this.userId = builder.userId;
    }

    // Getters
    public boolean isSuccess() {
        return success;
    }

    public String getUsername() {
        return username;
    }

    public String getMessage() {
        return message;
    }

    public String getRole() {
        return role;
    }

    public String getToken() {
        return token;
    }

    public String getUserId() {
        return userId;
    }

    // Builder Class
    public static class Builder {

        private boolean success;
        private String username;
        private String message;
        private String role;
        private String token;
        private String userId;

        public Builder setSuccess(boolean success) {
            this.success = success;
            return this;
        }

        public Builder setUsername(String username) {
            this.username = username;
            return this;
        }

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        public Builder setRole(String role) {
            this.role = role;
            return this;
        }

        public Builder setToken(String token) {
            this.token = token;
            return this;
        }

        public Builder setUserId(String userId) {
            this.userId = userId;
            return this;
        }

        public LoginResponseDTO build() {
            return new LoginResponseDTO(this);
        }
    }
}
