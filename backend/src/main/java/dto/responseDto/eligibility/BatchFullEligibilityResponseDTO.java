package dto.responseDto.eligibility;

import model.Eligibility;
import java.util.List;

public class BatchFullEligibilityResponseDTO {

    private boolean success;
    private String message;
    private List<Eligibility> data;

    public BatchFullEligibilityResponseDTO() {}

    public BatchFullEligibilityResponseDTO(boolean success, String message, List<Eligibility> data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public List<Eligibility> getData() { return data; }
    public void setData(List<Eligibility> data) { this.data = data; }
}
