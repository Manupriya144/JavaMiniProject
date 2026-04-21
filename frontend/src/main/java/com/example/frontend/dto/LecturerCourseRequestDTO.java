package com.example.frontend.dto;

public class LecturerCourseRequestDTO {
    private String lecturerId;
    private String courseId;

    public LecturerCourseRequestDTO() {
    }

    public LecturerCourseRequestDTO(String lecturerId, String courseId) {
        this.lecturerId = lecturerId;
        this.courseId = courseId;
    }

    public String getLecturerId() {
        return lecturerId;
    }

    public String getCourseId() {
        return courseId;
    }
}