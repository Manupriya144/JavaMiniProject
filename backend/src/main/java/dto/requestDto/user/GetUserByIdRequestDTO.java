package dto.requestDto.user;

public class GetUserByIdRequestDTO {
    private String userId;

    public GetUserByIdRequestDTO() {}

    public GetUserByIdRequestDTO(String userId) {
        this.userId = userId;
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
}