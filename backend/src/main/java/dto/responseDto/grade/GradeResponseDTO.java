package dto.responseDto.grade;

import model.Grade;

public class GradeResponseDTO {

    private boolean success;
    private String message;
    private Grade data;

    public GradeResponseDTO() {}

    public GradeResponseDTO(boolean success, String message, Grade data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Grade getData() { return data; }
    public void setData(Grade data) { this.data = data; }
}
