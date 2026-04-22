package dto.responseDto.lecture_course;

public class LecturerCourseResponseDTO {
    private boolean success;
    private String message;

    public LecturerCourseResponseDTO() {
    }

    public LecturerCourseResponseDTO(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}