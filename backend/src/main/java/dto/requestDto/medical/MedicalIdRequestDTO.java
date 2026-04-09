package dto.requestDto.medical;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MedicalIdRequestDTO {
    @JsonProperty("medical_id")
    private Integer medicalId;

    public Integer getMedicalId() {
        return medicalId;
    }

    public void setMedicalId(Integer medicalId) {
        this.medicalId = medicalId;
    }
}
