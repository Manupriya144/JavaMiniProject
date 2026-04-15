package dto.requestDto.eligibility;

public class FullEligibilityRequestDTO {

    private String studentId;
    private String courseId;
    private int academicYear;
    private int semester;

    public FullEligibilityRequestDTO() {}

    public FullEligibilityRequestDTO(String studentId, String courseId, int academicYear, int semester) {
        this.studentId = studentId;
        this.courseId = courseId;
        this.academicYear = academicYear;
        this.semester = semester;
    }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }

    public int getAcademicYear() { return academicYear; }
    public void setAcademicYear(int academicYear) { this.academicYear = academicYear; }

    public int getSemester() { return semester; }
    public void setSemester(int semester) { this.semester = semester; }
}
