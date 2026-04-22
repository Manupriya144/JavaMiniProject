package model.ca;

public class CAMark {
    private Integer markId;
    private String studentId;
    private Integer assessmentTypeId;
    private String courseId;
    private String assessmentName;
    private Double assessmentWeight;
    private Double marks;

    public CAMark() {
    }

    public CAMark(Integer markId, String studentId, Integer assessmentTypeId, String courseId,
                  String assessmentName, Double assessmentWeight, Double marks) {
        this.markId = markId;
        this.studentId = studentId;
        this.assessmentTypeId = assessmentTypeId;
        this.courseId = courseId;
        this.assessmentName = assessmentName;
        this.assessmentWeight = assessmentWeight;
        this.marks = marks;
    }

    public Integer getMarkId() {
        return markId;
    }

    public void setMarkId(Integer markId) {
        this.markId = markId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public Integer getAssessmentTypeId() {
        return assessmentTypeId;
    }

    public void setAssessmentTypeId(Integer assessmentTypeId) {
        this.assessmentTypeId = assessmentTypeId;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getAssessmentName() {
        return assessmentName;
    }

    public void setAssessmentName(String assessmentName) {
        this.assessmentName = assessmentName;
    }

    public Double getAssessmentWeight() {
        return assessmentWeight;
    }

    public void setAssessmentWeight(Double assessmentWeight) {
        this.assessmentWeight = assessmentWeight;
    }

    public Double getMarks() {
        return marks;
    }

    public void setMarks(Double marks) {
        this.marks = marks;
    }
}
