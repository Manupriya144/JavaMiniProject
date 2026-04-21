package dto.requestDto.ca;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UpdateCAMarksRequestDTO {
    @JsonProperty("mark_id")
    private Integer markId;

    @JsonProperty("marks")
    private Double marks;

    public Integer getMarkId() {
        return markId;
    }

    public void setMarkId(Integer markId) {
        this.markId = markId;
    }

    public Double getMarks() {
        return marks;
    }

    public void setMarks(Double marks) {
        this.marks = marks;
    }
}
