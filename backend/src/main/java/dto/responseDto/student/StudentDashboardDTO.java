package dto.responseDto.student;

import java.util.List;

public class StudentDashboardDTO {
    private double overallAttendance;
    private double sgpa;
    private double cgpa;
    private int enrolledCourses;
    private List<StudentCourseDashboardDTO> courses;

    public StudentDashboardDTO() {}

    public StudentDashboardDTO(double overallAttendance, double sgpa, double cgpa,
                               int enrolledCourses, List<StudentCourseDashboardDTO> courses) {
        this.overallAttendance = overallAttendance;
        this.sgpa = sgpa;
        this.cgpa = cgpa;
        this.enrolledCourses = enrolledCourses;
        this.courses = courses;
    }

    public double getOverallAttendance() { return overallAttendance; }
    public double getSgpa() { return sgpa; }
    public double getCgpa() { return cgpa; }
    public int getEnrolledCourses() { return enrolledCourses; }
    public List<StudentCourseDashboardDTO> getCourses() { return courses; }

    public void setOverallAttendance(double overallAttendance) { this.overallAttendance = overallAttendance; }
    public void setSgpa(double sgpa) { this.sgpa = sgpa; }
    public void setCgpa(double cgpa) { this.cgpa = cgpa; }
    public void setEnrolledCourses(int enrolledCourses) { this.enrolledCourses = enrolledCourses; }
    public void setCourses(List<StudentCourseDashboardDTO> courses) { this.courses = courses; }
}