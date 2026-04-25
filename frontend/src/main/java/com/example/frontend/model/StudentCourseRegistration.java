package com.example.frontend.model;

public class StudentCourseRegistration {
    private String courseId;
    private String courseCode;
    private String courseName;
    private int courseCredit;
    private int academicLevel;
    private String semester;
    private String departmentId;
    private String registrationType;
    private boolean alreadyRegistered;

    public StudentCourseRegistration() {}

    public String getCourseId() { return courseId; }
    public String getCourseCode() { return courseCode; }
    public String getCourseName() { return courseName; }
    public int getCourseCredit() { return courseCredit; }
    public int getAcademicLevel() { return academicLevel; }
    public String getSemester() { return semester; }
    public String getDepartmentId() { return departmentId; }
    public String getRegistrationType() { return registrationType; }
    public boolean isAlreadyRegistered() { return alreadyRegistered; }
}