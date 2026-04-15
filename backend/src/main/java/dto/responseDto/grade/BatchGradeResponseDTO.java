package dto.responseDto.grade;

import model.Grade;
import java.util.List;

public class BatchGradeResponseDTO {

    private boolean success;
    private String message;
    private List<Grade> data;

    public BatchGradeResponseDTO() {}

    public BatchGradeResponseDTO(boolean success, String message, List<Grade> data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public List<Grade> getData() { return data; }
    public void setData(List<Grade> data) { this.data = data; }
}