package dto.requestDto.student;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StudentRequestDTO {
    @JsonProperty("user_id")
    private String userId;

    public StudentRequestDTO() {}

    public StudentRequestDTO(String userId) {
        this.userId = userId;
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
}
