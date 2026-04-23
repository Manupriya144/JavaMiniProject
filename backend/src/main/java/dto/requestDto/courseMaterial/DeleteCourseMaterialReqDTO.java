package dto.requestDto.courseMaterial;

public class DeleteCourseMaterialReqDTO {
    private int materialId;

    public DeleteCourseMaterialReqDTO() {
    }

    public DeleteCourseMaterialReqDTO(int materialId) {
        this.materialId = materialId;
    }

    public int getMaterialId() {
        return materialId;
    }

    public void setMaterialId(int materialId) {
        this.materialId = materialId;
    }
}