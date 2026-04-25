package dto.requestDto.lecturer;

public class UpdateLecturerProfileReqDTO {
    private String designation;
    private String profilePicture;

    public UpdateLecturerProfileReqDTO() {}

    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }

    public String getProfilePicture() { return profilePicture; }
    public void setProfilePicture(String profilePicture) { this.profilePicture = profilePicture; }
}