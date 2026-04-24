package com.example.frontend.model;

public class AttendanceCourseOption {
    private final String courseId;
    private final String courseName;

    public AttendanceCourseOption(String courseId, String courseName) {
        this.courseId = courseId;
        this.courseName = courseName;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public String getDisplayText() {
        if (courseName == null || courseName.isBlank()) {
            return courseId;
        }
        return courseId + " - " + courseName;
    }
}
