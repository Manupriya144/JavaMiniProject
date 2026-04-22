package dto.responseDto.techofficer;

public class TechOfficerProfileResponseDTO {
    private String userId;
    private String username;
    private String email;
    private String contactNumber;
    private String profilePicture;
    private String role;
    private String departmentId;

    public TechOfficerProfileResponseDTO() {
    }

    public TechOfficerProfileResponseDTO(String userId, String username, String email, String contactNumber,
                                         String profilePicture, String role, String departmentId) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.contactNumber = contactNumber;
        this.profilePicture = profilePicture;
        this.role = role;
        this.departmentId = departmentId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }
}
