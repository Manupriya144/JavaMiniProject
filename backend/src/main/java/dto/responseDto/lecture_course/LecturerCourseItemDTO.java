package dto.responseDto.lecture_course;

public class LecturerCourseItemDTO {
    private String courseId;
    private String courseCode;
    private String courseName;
    private int courseCredit;

    public LecturerCourseItemDTO() {
    }

    public LecturerCourseItemDTO(String courseId, String courseCode, String courseName, int courseCredit) {
        this.courseId = courseId;
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.courseCredit = courseCredit;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public int getCourseCredit() {
        return courseCredit;
    }

    public void setCourseCredit(int courseCredit) {
        this.courseCredit = courseCredit;
    }
}