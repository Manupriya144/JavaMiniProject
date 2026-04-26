package com.example.frontend.model;

public class CAAssessmentTypeOption {
    private Integer assessmentTypeId;
    private String assessmentName;
    private Double weight;

    public Integer getAssessmentTypeId() {
        return assessmentTypeId;
    }

    public void setAssessmentTypeId(Integer assessmentTypeId) {
        this.assessmentTypeId = assessmentTypeId;
    }

    public String getAssessmentName() {
        return assessmentName;
    }

    public void setAssessmentName(String assessmentName) {
        this.assessmentName = assessmentName;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    @Override
    public String toString() {
        String namePart = assessmentName == null || assessmentName.isBlank() ? "Assignment" : assessmentName;
        String idPart = assessmentTypeId == null ? "-" : String.valueOf(assessmentTypeId);
        return namePart + " | " + idPart;
    }
}
