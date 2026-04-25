package dto.responseDto.attendance;

public class StudentAttendanceSummaryDTO {
    private String courseId;
    private String courseCode;
    private String courseName;
    private int totalSessions;
    private double totalHours;
    private double attendedHours;
    private double attendancePercentage;

    public StudentAttendanceSummaryDTO() {}

    public StudentAttendanceSummaryDTO(String courseId, String courseCode, String courseName,
                                       int totalSessions, double totalHours,
                                       double attendedHours, double attendancePercentage) {
        this.courseId = courseId;
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.totalSessions = totalSessions;
        this.totalHours = totalHours;
        this.attendedHours = attendedHours;
        this.attendancePercentage = attendancePercentage;
    }

    public String getCourseId() { return courseId; }
    public String getCourseCode() { return courseCode; }
    public String getCourseName() { return courseName; }
    public int getTotalSessions() { return totalSessions; }
    public double getTotalHours() { return totalHours; }
    public double getAttendedHours() { return attendedHours; }
    public double getAttendancePercentage() { return attendancePercentage; }
}