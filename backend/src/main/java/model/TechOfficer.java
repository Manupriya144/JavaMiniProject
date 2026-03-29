package model;

public class TechOfficer extends User {

    private String departmentId;

    // Default constructor
    public TechOfficer() {}

    // Constructor with User fields + department
    public TechOfficer(String userId, String username, String email, String password,
                       String contactNumber, String profilePicture, String role,
                       String departmentId) {
        super(userId, username, email, password, contactNumber, profilePicture, role);
        this.departmentId = departmentId;
    }

    // Constructor for only userId + department (simpler case)
    public TechOfficer(String userId, String departmentId) {
        super.setUserId(userId);
        this.departmentId = departmentId;
    }

    // Getter & Setter
    public String getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }


}