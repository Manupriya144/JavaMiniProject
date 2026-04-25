package com.example.frontend.model;

import java.util.List;

public class StudentDashboardData {
    private double overallAttendance;
    private double sgpa;
    private double cgpa;
    private int enrolledCourses;
    private List<StudentCourseDashboard> courses;

    public StudentDashboardData() {}

    public double getOverallAttendance() { return overallAttendance; }
    public double getSgpa() { return sgpa; }
    public double getCgpa() { return cgpa; }
    public int getEnrolledCourses() { return enrolledCourses; }
    public List<StudentCourseDashboard> getCourses() { return courses; }

    public void setOverallAttendance(double overallAttendance) { this.overallAttendance = overallAttendance; }
    public void setSgpa(double sgpa) { this.sgpa = sgpa; }
    public void setCgpa(double cgpa) { this.cgpa = cgpa; }
    public void setEnrolledCourses(int enrolledCourses) { this.enrolledCourses = enrolledCourses; }
    public void setCourses(List<StudentCourseDashboard> courses) { this.courses = courses; }
}