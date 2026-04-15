package dto.responseDto.eligibility;
import model.Eligibility;

public class FullEligibilityResponseDTO {

    private boolean success;
    private String message;
    private Eligibility data;

    public FullEligibilityResponseDTO() {}

    public FullEligibilityResponseDTO(boolean success, String message, Eligibility data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Eligibility getData() { return data; }
    public void setData(Eligibility data) { this.data = data; }
}
