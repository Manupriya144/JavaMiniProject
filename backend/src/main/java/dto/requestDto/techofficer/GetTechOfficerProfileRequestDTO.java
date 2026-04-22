package dto.requestDto.techofficer;

public class GetTechOfficerProfileRequestDTO {
    private String userId;

    public GetTechOfficerProfileRequestDTO() {
    }

    public GetTechOfficerProfileRequestDTO(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
