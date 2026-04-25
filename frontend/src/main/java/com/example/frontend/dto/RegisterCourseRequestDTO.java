package com.example.frontend.dto;

public class RegisterCourseRequestDTO {
    private String courseId;
    private int academicYear;
    private String semester;
    private String registrationType;

    public RegisterCourseRequestDTO() {}

    public RegisterCourseRequestDTO(String courseId, int academicYear, String semester, String registrationType) {
        this.courseId = courseId;
        this.academicYear = academicYear;
        this.semester = semester;
        this.registrationType = registrationType;
    }

    public String getCourseId() { return courseId; }
    public int getAcademicYear() { return academicYear; }
    public String getSemester() { return semester; }
    public String getRegistrationType() { return registrationType; }
}