package dto.responseDto.student;

public class StudentEligibilityResDTO {

    private String courseId;
    private String courseCode;
    private String courseName;

    private double totalHours;
    private double attendedHours;
    private double medicalHours;
    private double finalAttendanceHours;
    private double attendancePercentage;
    private String attendanceStatus;

    private double caMaxMarks;
    private double caMarks;
    private double caPercentage;
    private String caStatus;

    private String finalEligibilityStatus;

    public StudentEligibilityResDTO() {}

    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }

    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public double getTotalHours() { return totalHours; }
    public void setTotalHours(double totalHours) { this.totalHours = totalHours; }

    public double getAttendedHours() { return attendedHours; }
    public void setAttendedHours(double attendedHours) { this.attendedHours = attendedHours; }

    public double getMedicalHours() { return medicalHours; }
    public void setMedicalHours(double medicalHours) { this.medicalHours = medicalHours; }

    public double getFinalAttendanceHours() { return finalAttendanceHours; }
    public void setFinalAttendanceHours(double finalAttendanceHours) { this.finalAttendanceHours = finalAttendanceHours; }

    public double getAttendancePercentage() { return attendancePercentage; }
    public void setAttendancePercentage(double attendancePercentage) { this.attendancePercentage = attendancePercentage; }

    public String getAttendanceStatus() { return attendanceStatus; }
    public void setAttendanceStatus(String attendanceStatus) { this.attendanceStatus = attendanceStatus; }

    public double getCaMaxMarks() { return caMaxMarks; }
    public void setCaMaxMarks(double caMaxMarks) { this.caMaxMarks = caMaxMarks; }

    public double getCaMarks() { return caMarks; }
    public void setCaMarks(double caMarks) { this.caMarks = caMarks; }

    public double getCaPercentage() { return caPercentage; }
    public void setCaPercentage(double caPercentage) { this.caPercentage = caPercentage; }

    public String getCaStatus() { return caStatus; }
    public void setCaStatus(String caStatus) { this.caStatus = caStatus; }

    public String getFinalEligibilityStatus() { return finalEligibilityStatus; }
    public void setFinalEligibilityStatus(String finalEligibilityStatus) {
        this.finalEligibilityStatus = finalEligibilityStatus;
    }
}