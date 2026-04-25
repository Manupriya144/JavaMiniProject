package dto.requestDto.lecturer;

public class AddLectureSessionReqDTO {
    private String courseId;
    private String sessionDate;   // yyyy-MM-dd
    private double sessionHours;
    private String type;          // Theory / Practical
    private String lecturerId;    // optional, useful for validation

    public AddLectureSessionReqDTO() {}

    public AddLectureSessionReqDTO(String courseId, String sessionDate, double sessionHours, String type, String lecturerId) {
        this.courseId = courseId;
        this.sessionDate = sessionDate;
        this.sessionHours = sessionHours;
        this.type = type;
        this.lecturerId = lecturerId;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getSessionDate() {
        return sessionDate;
    }

    public void setSessionDate(String sessionDate) {
        this.sessionDate = sessionDate;
    }

    public double getSessionHours() {
        return sessionHours;
    }

    public void setSessionHours(double sessionHours) {
        this.sessionHours = sessionHours;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLecturerId() {
        return lecturerId;
    }

    public void setLecturerId(String lecturerId) {
        this.lecturerId = lecturerId;
    }
}