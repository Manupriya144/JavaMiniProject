package dto.responseDto.finalMarks;

import model.FinalMarks;

public class FinalMarksResponseDTO {
    private boolean success;
    private String message;
    private FinalMarks data;

    public FinalMarksResponseDTO() {}

    public FinalMarksResponseDTO(boolean success, String message, FinalMarks data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public FinalMarks getData() { return data; }
    public void setData(FinalMarks data) { this.data = data; }
}