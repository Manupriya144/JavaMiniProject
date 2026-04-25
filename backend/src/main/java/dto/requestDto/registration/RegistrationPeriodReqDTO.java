package dto.requestDto.registration;

public class RegistrationPeriodReqDTO {
    private String departmentId;
    private int academicLevel;
    private String semester;
    private int academicYear;
    private String startAt;
    private String endAt;
    private String status;

    public RegistrationPeriodReqDTO() {}

    public String getDepartmentId() { return departmentId; }
    public void setDepartmentId(String departmentId) { this.departmentId = departmentId; }

    public int getAcademicLevel() { return academicLevel; }
    public void setAcademicLevel(int academicLevel) { this.academicLevel = academicLevel; }

    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }

    public int getAcademicYear() { return academicYear; }
    public void setAcademicYear(int academicYear) { this.academicYear = academicYear; }

    public String getStartAt() { return startAt; }
    public void setStartAt(String startAt) { this.startAt = startAt; }

    public String getEndAt() { return endAt; }
    public void setEndAt(String endAt) { this.endAt = endAt; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}