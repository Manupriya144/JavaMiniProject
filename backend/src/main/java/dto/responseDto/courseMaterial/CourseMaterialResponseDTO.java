package dto.responseDto.courseMaterial;

import model.CourseMaterial;

public class CourseMaterialResponseDTO {

    private boolean success;
    private String message;
    private CourseMaterial data;

    public CourseMaterialResponseDTO() {
    }

    public CourseMaterialResponseDTO(boolean success, String message, CourseMaterial data) {
        this.success = success;
        this.message = message;
        this.data = data;
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

    public CourseMaterial getData() {
        return data;
    }

    public void setData(CourseMaterial data) {
        this.data = data;
    }
}