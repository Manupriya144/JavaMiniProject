package model;

public class Medical {
    private Integer medicalId;
    private String studentId;
    private String courseId;
    private String examType;
    private String dateSubmitted;
    private String medicalCopy;
    private String status;

    public Medical() {
    }

    public Medical(Integer medicalId, String studentId, String courseId, String examType,
                   String dateSubmitted, String medicalCopy, String status) {
        this.medicalId = medicalId;
        this.studentId = studentId;
        this.courseId = courseId;
        this.examType = examType;
        this.dateSubmitted = dateSubmitted;
        this.medicalCopy = medicalCopy;
        this.status = status;
    }

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
