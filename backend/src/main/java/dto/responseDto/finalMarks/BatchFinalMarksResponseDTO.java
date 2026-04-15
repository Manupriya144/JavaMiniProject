package dto.responseDto.finalMarks;

import model.FinalMarks;
import java.util.List;

public class BatchFinalMarksResponseDTO {
    private boolean success;
    private String message;
    private List<FinalMarks> data;

    public BatchFinalMarksResponseDTO() {}

    public BatchFinalMarksResponseDTO(boolean success, String message, List<FinalMarks> data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public List<FinalMarks> getData() { return data; }
    public void setData(List<FinalMarks> data) { this.data = data; }
}
