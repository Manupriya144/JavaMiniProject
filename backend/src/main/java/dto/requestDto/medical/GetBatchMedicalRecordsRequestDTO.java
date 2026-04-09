package dto.requestDto.medical;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GetBatchMedicalRecordsRequestDTO {
    @JsonProperty("batch")
    private String batch;

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }
}
