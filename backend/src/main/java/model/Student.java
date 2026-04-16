package model;

public class Student extends User{


    private String regNo;
    private String batch;
    private int academicLevel;
    private String departmentId;

    // Constructors
    public Student() {}

    public Student(String userId, String regNo, String batch, int academicLevel, String departmentId) {
        super.setUserId(userId);
        this.regNo = regNo;
        this.batch = batch;
        this.academicLevel = academicLevel;
        this.departmentId = departmentId;
    }

    public Student(String userId,String username,String email,
                   String contactNumber,
                   String profilePicture, String role ,String regNo,
                   String batch,
                   int academicLevel,
                   String departmentId){
        super.setUserId(userId);
        super.setUsername(username);
        super.setEmail(email);
        super.setContactNumber(contactNumber);
        super.setProfilePicture(profilePicture);
        super.setRole(role);
        this.regNo = regNo;
        this.batch = batch;
        this.academicLevel = academicLevel;
        this.departmentId = departmentId;

    }


    public String getRegNo() { return regNo; }
    public void setRegNo(String regNo) { this.regNo = regNo; }

    public String getBatch() { return batch; }
    public void setBatch(String batch) { this.batch = batch; }

    public int getAcademicLevel() { return academicLevel; }
    public void setAcademicLevel(int academicLevel) { this.academicLevel = academicLevel; }

    public String getDepartmentId() { return departmentId; }
    public void setDepartmentId(String departmentId) { this.departmentId = departmentId; }
}
