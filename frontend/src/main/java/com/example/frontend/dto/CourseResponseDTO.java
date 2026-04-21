package com.example.frontend.dto;

public class CourseResponseDTO {
    private String courseId;
    private String courseCode;
    private String name;

    public CourseResponseDTO() {
    }

    public CourseResponseDTO(String courseId, String courseCode, String name) {
        this.courseId = courseId;
        this.courseCode = courseCode;
        this.name = name;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return courseCode + " - " + name;
    }
}