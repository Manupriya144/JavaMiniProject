package dto.responseDto.student;

import model.Student;

public class StudentResponseDTO {
    private boolean success;
    private String message;
    private Student data;

    public StudentResponseDTO() {}

    public StudentResponseDTO(boolean success, String message, Student data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Student getData() { return data; }
    public void setData(Student data) { this.data = data; }
}
