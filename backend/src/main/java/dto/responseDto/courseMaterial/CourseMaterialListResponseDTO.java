package dto.responseDto.courseMaterial;

import model.CourseMaterial;

import java.util.List;

public class CourseMaterialListResponseDTO {

    private boolean success;
    private String message;
    private List<CourseMaterial> data;

    public CourseMaterialListResponseDTO() {
    }

    public CourseMaterialListResponseDTO(boolean success, String message, List<CourseMaterial> data) {
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

    public List<CourseMaterial> getData() {
        return data;
    }

    public void setData(List<CourseMaterial> data) {
        this.data = data;
    }
}