package dto.responseDto.student;

public class StudentRegisteredCourseDTO {
    private String courseId;
    private String courseCode;
    private String courseName;
    private int courseCredit;

    public StudentRegisteredCourseDTO() {}

    public StudentRegisteredCourseDTO(String courseId, String courseCode, String courseName, int courseCredit) {
        this.courseId = courseId;
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.courseCredit = courseCredit;
    }

    public String getCourseId() { return courseId; }
    public String getCourseCode() { return courseCode; }
    public String getCourseName() { return courseName; }
    public int getCourseCredit() { return courseCredit; }
}