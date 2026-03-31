package dto.requestDto.attendance;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GetBatchAttendanceRequestDTO {
    @JsonProperty("batch")
    private String batch;

    @JsonProperty("view_type")
    private String viewType;

    public GetBatchAttendanceRequestDTO() {
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public String getViewType() {
        return viewType;
    }

    public void setViewType(String viewType) {
        this.viewType = viewType;
    }
}
