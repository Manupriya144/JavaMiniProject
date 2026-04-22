package dto.requestDto.lecture_course;

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

    public void setLecturerId(String lecturerId) {
        this.lecturerId = lecturerId;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }
}