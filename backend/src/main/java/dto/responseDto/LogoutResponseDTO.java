package dto.responseDto;

public class LogoutResponseDTO {

    private boolean success;
    private String message;

    public LogoutResponseDTO(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
}