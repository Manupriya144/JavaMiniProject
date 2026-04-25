package com.example.frontend.dto;

public class RegistrationPeriodRequestDTO {
    private String departmentId;
    private int academicLevel;
    private String semester;
    private int academicYear;
    private String startAt;
    private String endAt;
    private String status;

    public RegistrationPeriodRequestDTO() {}

    public RegistrationPeriodRequestDTO(String departmentId, int academicLevel,
                                        String semester, int academicYear,
                                        String startAt, String endAt, String status) {
        this.departmentId = departmentId;
        this.academicLevel = academicLevel;
        this.semester = semester;
        this.academicYear = academicYear;
        this.startAt = startAt;
        this.endAt = endAt;
        this.status = status;
    }

    public String getDepartmentId() { return departmentId; }
    public int getAcademicLevel() { return academicLevel; }
    public String getSemester() { return semester; }
    public int getAcademicYear() { return academicYear; }
    public String getStartAt() { return startAt; }
    public String getEndAt() { return endAt; }
    public String getStatus() { return status; }
}