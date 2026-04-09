package dto.requestDto.medical;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UpdateMedicalRequestDTO {
    @JsonProperty("medical_id")
    private Integer medicalId;

    @JsonProperty("student_id")
    private String studentId;

    @JsonProperty("course_id")
    private String courseId;

    @JsonProperty("exam_type")
    private String examType;

    @JsonProperty("date_submitted")
    private String dateSubmitted;

    @JsonProperty("medical_copy")
    private String medicalCopy;

    public Integer getMedicalId() {
        return medicalId;
    }

    public void setMedicalId(Integer medicalId) {
        this.medicalId = medicalId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getExamType() {
        return examType;
    }

    public void setExamType(String examType) {
        this.examType = examType;
    }

    public String getDateSubmitted() {
        return dateSubmitted;
    }

    public void setDateSubmitted(String dateSubmitted) {
        this.dateSubmitted = dateSubmitted;
    }

    public String getMedicalCopy() {
        return medicalCopy;
    }

    public void setMedicalCopy(String medicalCopy) {
        this.medicalCopy = medicalCopy;
    }
}
