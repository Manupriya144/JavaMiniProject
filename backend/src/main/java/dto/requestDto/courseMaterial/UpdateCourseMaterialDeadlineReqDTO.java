package dto.requestDto.courseMaterial;

public class UpdateCourseMaterialDeadlineReqDTO {
    private int materialId;
    private String deadline;

    public UpdateCourseMaterialDeadlineReqDTO() {
    }

    public UpdateCourseMaterialDeadlineReqDTO(int materialId, String deadline) {
        this.materialId = materialId;
        this.deadline = deadline;
    }

    public int getMaterialId() {
        return materialId;
    }

    public void setMaterialId(int materialId) {
        this.materialId = materialId;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }
}